package org.quanye.sobj;

import org.quanye.sobj.annotation.DateFormat;
import org.quanye.sobj.exception.InValidSObjSyntaxException;
import org.quanye.sobj.struct.SObjNode;
import org.quanye.sobj.tools.C$;
import org.quanye.sobj.tools.S$;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * SObj Parser
 * SObj stand for `symbolic object`
 * This source code is license on the Apache-License v2.0
 *
 * @author QuanyeChen
 */
public class SObjParser {
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.FORMAT_STYLE);
    public static final String BRACKET_START = "(";
    public static final char BRACKET_START_C = '(';
    public static final String BRACKET_CLOSE = ")";
    public static final char BRACKET_CLOSE_C = ')';
    public static final String BRACKET_OBJECT = "(*obj";
    public static final String OBJECT_NAME = "*obj";
    public static final String BRACKET_LIST = "(*list";
    public static final String LIST_NAME = "*list";
    public static final String SEPARATOR = " ";
    public static final char SEPARATOR_C = ' ';
    public static final String NULL = "()";
    public static final char COMMENT_C = ';';
    public static final String FALSE_VALUE = "#f";
    public static final String TRUE_VALUE = "#t";

    /**
     * Parse the Java Object to the SObj
     *
     * @param obj Object
     * @return SObj
     */
    public static String fromObject(Object obj) {
        StringBuilder result = new StringBuilder();
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            // When clazz is an array
            Object[] values = (Object[]) obj;
            StringBuilder sb = new StringBuilder();
            sb.append(BRACKET_LIST);
            for (Object v : values) {
                if (v instanceof String) {
                    sb.append('\"').append(v).append('\"');
                } else {
                    sb.append(fromObject(v));
                }
            }
            sb.append(BRACKET_CLOSE);
            result.append(sb);
        } else {
            // Otherwise clazz is an object
            StringBuilder sb = new StringBuilder(BRACKET_OBJECT);// + obj.getClass().getSimpleName());
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    if (value instanceof String) {
                        value = String.format("\"%s\"", value);
                    } else if (value instanceof Date) {
                        Object tmp = value;
                        value = String.format("\"%s\"", sdf.format(value));
                        for (Annotation an : field.getAnnotations()) {
                            if (an instanceof DateFormat) {
                                DateFormat df = (DateFormat) an;
                                sdf.applyPattern(df.value());
                                value = String.format("\"%s\"", sdf.format((Date) tmp));
                            }
                        }
                    } else if (value instanceof Boolean) {
                        value = ((boolean) value) ? TRUE_VALUE : FALSE_VALUE;
                    } else if (value.getClass().getClassLoader() != null) {
                        // If clazz is a user-defined type(in there must the POJO-type), then extract it
                        value = fromObject(value);
                    } else if (value.getClass().isArray()) {
                        value = fromObject(value);
                    }
                    String name = field.getName();
                    sb.append(BRACKET_START).append(name).append(SEPARATOR_C).append(value).append(BRACKET_CLOSE);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
            sb.append(BRACKET_CLOSE);
            result.append(sb);
        }
        return result.toString();
    }


    /**
     * Parse the SObj to the Java Object
     *
     * @param sexp  SObj
     * @param clazz Resulting object's type
     * @param <T>   Object's generic type
     * @return Object
     * @throws InValidSObjSyntaxException Throws the exception when SObj syntax is non-valid
     */
    public static <T> T toObject(String sexp, Class<T> clazz) throws InValidSObjSyntaxException {
        sexp = S$.removeBoilerplateEmptyCode(sexp);
        if (!S$.isValidSexp(sexp)) {
            throw new InValidSObjSyntaxException("invalid SObj syntax");
        }
        SObjNode lo = toAST(sexp);
        try {
            if (clazz.isArray()) {
                Class<?> compClazz = clazz.getComponentType();
                String pkgName = compClazz.getPackage().getName();
                return setArrayValue(lo, pkgName, compClazz.getSimpleName());
            } else {
                return setValue(lo, clazz.getDeclaredConstructor().newInstance());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static <T> T setArrayValue(SObjNode sObjNode, String pkgName, String compClazzName) {
        List<Object> list = new LinkedList<>();
        SObjNode arrEleNode = sObjNode.getCdr();
        if (C$.isSObj(arrEleNode.getCarValue())) {
            try {
                Class<?> compClazz = Class.forName(String.format("%s.%s", pkgName, compClazzName));
                while (arrEleNode != null && arrEleNode.getCar() != null) {
                    Object instance = setValue(arrEleNode.getCar(), compClazz.getDeclaredConstructor().newInstance());
                    list.add(instance);
                    arrEleNode = arrEleNode.getCdr();
                }
                int lSize = list.size();
                if (lSize > 0) {
                    Object target = Array.newInstance(compClazz, lSize);
                    for (int i = 0; i < lSize; ++i) {
                        Array.set(target, i, list.get(i));
                    }
                    return (T) target;
                }
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String carV = arrEleNode.getCarValue();
            while (arrEleNode != null) {
                String v = arrEleNode.getCarValue();
                v = C$.trimStr(v);
                list.add(v);
                arrEleNode = arrEleNode.getCdr();
            }
            int lSize = list.size();
            if (carV != null && lSize > 0) {
                Object target = Array.newInstance(C$.getValueType(carV), lSize);
                for (int i = 0; i < lSize; ++i) {
                    Array.set(target, i, list.get(i));
                }
                return (T) target;
            }
        }
        return null;
    }


    private static <T> T setValue(SObjNode sobjNode, T target) {
        SObjNode firstV = sobjNode.getCar();
        SObjNode leftV = sobjNode.getCdr();

        // Key
        if (firstV == null && leftV != null) {
            String key = sobjNode.getCarValue();
            if (!(key.equals(OBJECT_NAME) || key.equals(LIST_NAME))) {
                String value = leftV.getCarValue();
                if (C$.isSObj(value)) {
                    String pkgName = target.getClass().getPackage().getName();
                    String clazzName = String.format("%s%s", key.substring(0, 1).toUpperCase(), key.substring(1));
                    try {
                        Class<?> clazz = Class.forName(pkgName + "." + clazzName);
                        Object instance = setValue(leftV.getCar(), clazz.getDeclaredConstructor().newInstance());
                        putField(target, key, instance);
                    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (C$.isList(value)) {
                    String pkgName = target.getClass().getPackage().getName();
                    String clazzName = String.format("%s%s", key.substring(0, 1).toUpperCase(), key.substring(1));
                    T arrInstance = setArrayValue(leftV.getCar(), pkgName, clazzName);
                    putField(target, key, arrInstance);
                    // *list process had been done above, don't need to process by `setValue` ever.
                    return arrInstance;
                } else {
                    value = C$.trimStr(value);
                    putField(target, key, value);
                }
            }
        }

        // car is a list
        if (firstV != null) {
            setValue(firstV, target);
        }

        // not end list
        if (leftV != null) {
            setValue(leftV, target);
        }

        return target;
    }


    private static SObjNode toAST(String sexp) {
        String firstV = S$.car(sexp);
        String leftV = S$.cdr(sexp);
        SObjNode result = new SObjNode(firstV);

        if (S$.isPair(firstV)) {
            result.setCar(toAST(firstV));
        }

        if (S$.isPair(leftV)) {
            result.setCdr(toAST(leftV));
        }

        return result;
    }


    private static <T> void putField(T target, String key, String value) {
        Field field = C$.getFieldByName(target, key);
        if (field != null) {
            field.setAccessible(true);
            Class<?> typeClazz = field.getType();
            if (C$.isPrimitive(typeClazz)) {
                // Change `TURE_VALUE` and `FALSE_VALUE` to `true` and `false`
                if (value.equals(TRUE_VALUE)) {
                    value = "true";
                } else if (value.equals(FALSE_VALUE)) {
                    value = "false";
                }
                try {
                    Constructor<?> c = typeClazz.getConstructor(String.class);
                    field.set(target, c.newInstance(value));
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                // Other type
                if (typeClazz.getName().equals("java.util.Date")) {
                    for (Annotation an : field.getAnnotations()) {
                        if (an instanceof DateFormat) {
                            DateFormat df = (DateFormat) an;
                            sdf.applyPattern(df.value());
                            break;
                        }
                    }
                    try {
                        field.set(target, sdf.parse(value));
                    } catch (ParseException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    private static void putField(Object target, String key, Object instance) {
        Field field = C$.getFieldByName(target, key);
        if (field != null) {
            field.setAccessible(true);
            try {
                field.set(target, instance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    public static String minimize(String sObj) {
        return S$.minimizeSexp(sObj);
    }
}
