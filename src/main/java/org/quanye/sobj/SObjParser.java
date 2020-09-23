package org.quanye.sobj;

import org.quanye.sobj.annotation.DateFormat;
import org.quanye.sobj.exception.NotValidSObjSyntaxException;
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
     * Parse the Object to the SObj
     *
     * @param obj Object
     * @return SObj
     */
    public static String fromObject(Object obj) {
        StringBuilder result = new StringBuilder(BRACKET_OBJECT);// + obj.getClass().getSimpleName());
        Class<?> clazz = obj.getClass();
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
                } else if (value.getClass().isArray()) {
                    Object[] values = (Object[]) value;
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
                    value = sb.toString();
                } else if (value.getClass().getClassLoader() != null) {
                    // If clazz is a user-defined type(in there must the POJO-type), then extract it
                    value = fromObject(value);
                }
                String name = field.getName();
                result.append(BRACKET_START).append(name).append(SEPARATOR_C).append(value).append(BRACKET_CLOSE);
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
        result.append(BRACKET_CLOSE);
        return result.toString();
    }


    public static <T> T toObject(String sexp, Class<T> clazz) throws NotValidSObjSyntaxException {
        sexp = S$.removeBoilerplateEmptyCode(sexp);
        if (!S$.isValidSexp(sexp)) {
            throw new NotValidSObjSyntaxException("invalid SObj syntax");
        }
        SObjNode lo = toAST(sexp);
        try {
            return setValue(lo, clazz.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
                    String clazzName = key.substring(0, 1).toUpperCase() + key.substring(1);
                    try {
                        Class<?> clazz = Class.forName(pkgName + "." + clazzName);
                        Object instance = setValue(leftV.getCar(), clazz.getDeclaredConstructor().newInstance());
                        putField(target, instance, key);
                    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else if (C$.isList(value)) {
                    SObjNode arrNode = toAST(S$.cdr(value));
                    if (C$.isSObj(arrNode.getCarValue())) {
                        String pkgName = target.getClass().getPackage().getName();
                        String clazzName = key.substring(0, 1).toUpperCase() + key.substring(1);
                        try {
                            Class<?> clazz = Class.forName(String.format("%s.%s", pkgName, clazzName));
                            List<Object> list = new LinkedList<>();
                            while (arrNode != null && arrNode.getCar() != null) {
                                Object instance = setValue(arrNode.getCar(), clazz.getDeclaredConstructor().newInstance());
                                list.add(instance);
                                arrNode = arrNode.getCdr();
                            }
                            if (list.size() > 0) {
                                Object arr = Array.newInstance(clazz, list.size());
                                putArray(list, arr, target, key);
                            }
                        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } else {
                        List<Object> list = new LinkedList<>();
                        String carV = arrNode.getCarValue();
                        while (arrNode != null) {
                            String v = arrNode.getCarValue();
                            list.add(v);
                            arrNode = arrNode.getCdr();
                        }
                        if (carV != null && list.size() > 0) {
                            Object arr = Array.newInstance(C$.getValueType(carV), list.size());
                            putArray(list, arr, target, key);
                        }
                    }
                    // *list process had been done above, don't need to process by `setValue` ever.
                    return target;
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


    private static void putArray(List<Object> list, Object arr, Object target, String key) {
        for (int i = 0; i < list.size(); ++i) {
            Array.set(arr, i, list.get(i));
        }
        putField(target, arr, key);
    }


    private static void putField(Object target, Object instance, String key) {
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

}
