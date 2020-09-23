package org.quanye.sobj.tools;

import java.lang.reflect.Field;

import static org.quanye.sobj.SObjParser.*;

/**
 * Common tools
 * <p>
 * This source code is license on the Apache-License v2.0
 *
 * @author Quanyec
 */
public class C$ {

    public static boolean isString(String sexp) {
        int len = sexp.length();
        return len > 1 && sexp.charAt(0) == '\"' && sexp.charAt(len - 1) == '\"';
    }


    public static boolean isSObj(String sexp) {
        return sexp.startsWith(BRACKET_OBJECT) && sexp.endsWith(BRACKET_CLOSE);
    }


    public static boolean isList(String sexp) {
        return sexp.startsWith(BRACKET_LIST) && sexp.endsWith(BRACKET_CLOSE);
    }


    /**
     * Whether type-class is the primitive-type
     *
     * @param clazz object's type class
     * @return When the type clazz is the primitive-type, return true, others false
     */
    public static boolean isPrimitive(Class<?> clazz) {
        String cName = clazz.getName();
        return cName.equals("java.lang.String")
                || cName.equals("java.lang.Integer")
                || cName.equals("java.lang.Long")
                || cName.equals("java.lang.Boolean")
                || cName.equals("java.lang.Character")
                || cName.equals("java.lang.Float")
                || cName.equals("java.lang.Double")
                || cName.equals("java.lang.Byte")
                || cName.equals("java.lang.Short");
    }


    public static String trimStr(String value) {
        if (value.length() > 1 && value.charAt(0) == '\"') {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }


    public static Class<?> getValueType(String value) {
        char startC = value.charAt(0);
        char endC = value.charAt(value.length() - 1);
        if (startC == '\'' && endC == '\'') {
            return Character.class;
        } else if (startC == '\"' || startC == '\'') {
            // symbol is also treat as `String`
            return String.class;
        } else if (value.contains(".")) {
            return Double.class;
        } else {
            return Long.class;
        }
    }


    public static Field getFieldByName(Object target, String fieldName) {
        Field[] dfs = target.getClass().getDeclaredFields();
        for (Field df : dfs) {
            if (df.getName().equals(fieldName)) {
                return df;
            }
        }
        return null;
    }

}
