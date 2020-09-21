package org.quanye.sobj;

import org.quanye.sobj.annotation.DateFormat;
import org.quanye.sobj.exception.NotValidSObjSyntaxExcption;
import org.quanye.sobj.struct.Cons;
import org.quanye.sobj.tools.C$;
import org.quanye.sobj.tools.S$;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * LispParser
 * This source code is license on the Apache-License v2.0
 *
 * @author QuanyeChen
 */
public class SObjParser {

    private static final SimpleDateFormat sdf = new SimpleDateFormat(DateFormat.FORMAT_STYLE);
    public static final String BRACKET_START = "(";
    public static final char BRACKET_START_C = BRACKET_START.charAt(0);
    public static final String BRACKET_CLOSE = ")";
    public static final char BRACKET_CLOSE_C = BRACKET_CLOSE.charAt(0);
    public static final String BRACKET_OBJECT = "(*obj";
    public static final String BRACKET_LIST = "(*list";
    public static final String SEPARATOR = " ";
    public static final char SEPARATOR_C = ' ';
    public static final String NULL = "()";
    public static final char COMMENT_C = ';';

    /**
     * Write the Object to the String
     *
     * @param obj
     * @return
     */
    public static String fromObject(Object obj) {
        StringBuilder result = new StringBuilder(BRACKET_OBJECT);// + obj.getClass().getSimpleName());
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            try {
                Object value = field.get(obj);
                if (value instanceof String) {
                    value = "\"" + value + "\"";
                } else if (value instanceof Date) {
                    Object tmp = value;
                    value = "\"" + sdf.format(value) + "\"";
                    for (Annotation an : field.getAnnotations()) {
                        if (an instanceof DateFormat) {
                            DateFormat df = (DateFormat) an;
                            sdf.applyPattern(df.value());
                            value = "\"" + sdf.format((Date) tmp) + "\"";
                        }
                    }
                } else if (value.getClass().isArray()) {
                    Object[] values = (Object[]) value;
                    StringBuilder sb = new StringBuilder();
                    sb.append(BRACKET_LIST);
                    for (Object v : values) {
                        if (v instanceof String) {
                            sb.append("\"").append(v).append("\"");
                        } else {
                            sb.append(fromObject(v));
                        }
                    }
                    sb.append(BRACKET_CLOSE);
                    value = sb.toString();
                }
                // If clazz is a user-define type(in this must the POJO-type), then extract it
                else if (value.getClass().getClassLoader() != null) {
                    value = fromObject(value);
                }
                result.append(BRACKET_START).append(name).append(SEPARATOR_C).append(value).append(BRACKET_CLOSE);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        result.append(BRACKET_CLOSE);
        return result.toString();
    }


    public static <T> T toObject(String sexp, Class<T> clazz) throws NotValidSObjSyntaxExcption {
        sexp = S$.removeBoilerplateEmptyCode(sexp);
        if (!S$.isValidSexp(sexp)) {
            throw new NotValidSObjSyntaxExcption("invalid SObj syntax");
        }
        Cons lo = toAST(sexp);
        try {
            return (T) setValue(lo, clazz.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) new Object();
    }


    private static Object setValue(Cons cons, Object target) {
        Cons car = cons.getCar();
        Cons cdr = cons.getCdr();

        // Key
        if (car == null && cdr != null) {
            String key = cons.getCarValue();
            String value = cdr.getCarValue();
            String pkgName = target.getClass().getPackage().getName();
            String clazzName = key.substring(0, 1).toUpperCase() + key.substring(1);
            if (C$.isSObj(value)) {
                if (!clazzName.equals("*obj") && !clazzName.equals("*list"))
                    try {
                        Class<?> clazz = Class.forName(pkgName + "." + clazzName);
                        Object instance = setValue(cdr.getCar(), clazz.newInstance());
                        putField(target, instance, key);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } else if (C$.isList(value)) {
                Cons arrCons = toAST(S$.cdr(value));
                if (C$.isSObj(arrCons.getCarValue())) {
                    try {
                        Class<?> clazz = Class.forName(pkgName + "." + clazzName);
                        List<Object> list = new ArrayList<>();
                        while (arrCons != null && arrCons.getCar() != null) {
                            Object instance = setValue(arrCons.getCar(), clazz.newInstance());
                            list.add(instance);
                            arrCons = arrCons.getCdr();
                        }
                        if (list.size() > 0) {
                            Object arr = Array.newInstance(clazz, list.size());
                            putArray(list, arr, target, key);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    List<Object> list = new ArrayList<>();
                    String carV = arrCons.getCarValue();
                    while (arrCons != null) {
                        String v = C$.trimStr(arrCons.getCarValue());
                        list.add(v);
                        arrCons = arrCons.getCdr();
                    }
                    try {
                        if (carV != null && list.size() > 0) {
                            Object arr = Array.newInstance(C$.getType(carV), list.size());
                            putArray(list, arr, target, key);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                value = C$.trimStr(value);
                putField(target, key, value);
            }
        }

        // car is a list
        if (car != null) {
            setValue(car, target);
        }

        // not end list
        if (cdr != null) {
            setValue(cdr, target);
        }

        return target;
    }


    private static Cons toAST(String sexp) {
        String carValue = S$.car(sexp);
        String cdrValue = S$.cdr(sexp);
        Cons result = new Cons(carValue, "");

        if (C$.isSexp(carValue)) {
            result.setCar(toAST(carValue));
        }

        if (C$.isSexp(cdrValue)) {
            result.setCdr(toAST(cdrValue));
        }

        return result;
    }


    private static void putField(Object target, String key, String value) {
        Field field = C$.getFieldByName(target, key);
        if (field != null) {
            field.setAccessible(true);
            Class<?> typeClazz = field.getType();
            if (C$.isPrimitive(typeClazz)) {
                try {
                    Constructor c = typeClazz.getConstructor(String.class);
                    field.set(target, c.newInstance(value));
                } catch (Exception e) {
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
                    } catch (Exception e) {
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
