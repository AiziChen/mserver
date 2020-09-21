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

    public static boolean isSObj(String sexp) {
        if (!sexp.startsWith(BRACKET_OBJECT)) {
            return false;
        }

        if (isEmpty(sexp)) {
            return false;
        }

        int bStart = 0;
        int bClose = 0;
        for (int i = 0; i < sexp.length(); ++i) {
            char c = sexp.charAt(i);
            if (c == BRACKET_START_C) {
                bStart++;
            } else if (c == BRACKET_CLOSE_C) {
                bClose++;
                if (bClose == bStart) {
                    return true;
                }
            }
        }

        return false;
    }


    public static boolean isString(String sexp) {
        int len = sexp.length();
        return sexp.charAt(0) == '\"' && sexp.charAt(len - 1) == '\"';
    }


    public static boolean isList(String sexp) {
        return sexp.startsWith(BRACKET_LIST) && sexp.endsWith(BRACKET_CLOSE);
    }


    public static boolean isEmpty(String sexp) {
        return sexp.equals(NULL);
    }


    /**
     * Whether object is the primitive-type
     *
     * @param clazz
     * @return
     */
    public static boolean isPrimitive(Class<?> clazz) {
        String cName = clazz.getName();
        if (cName.equals("java.lang.Integer")
                || cName.equals("java.lang.Boolean")
                || cName.equals("java.lang.Long")
                || cName.equals("java.lang.Character")
                || cName.equals("java.lang.Float")
                || cName.equals("java.lang.Double")
                || cName.equals("java.lang.Byte")
                || cName.equals("java.lang.Short")
                || cName.equals("java.lang.String")) {
            return true;
        } else {
            return false;
        }
    }


    public static String trimStr(String value) {
        if (value.length() > 1 && value.charAt(0) == '\"') {
            value = value.substring(1, value.length() - 1);
        }
        return value;
    }


    public static Class<?> getType(String value) throws ClassNotFoundException {
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return Class.forName("java.lang.String");
        } else if (value.startsWith("'") && value.endsWith("'")) {
            return Class.forName("java.lang.Character");
        } else if (value.contains(".")) {
            return Class.forName("java.lang.Double");
        } else {
            return Class.forName("java.lang.Long");
        }
    }


    public static Field getFieldByName(Object target, String fieldName) {
        Field[] dfields = target.getClass().getDeclaredFields();
        for (Field dfield : dfields) {
            if (dfield.getName().equals(fieldName)) {
                return dfield;
            }
        }
        return null;
    }

    public static boolean isSexp(String sexp) {
        if (!sexp.startsWith(BRACKET_START)) {
            return false;
        }

        if (isEmpty(sexp)) {
            return false;
        }

        int bStart = 0;
        int bClose = 0;
        for (int i = 0; i < sexp.length(); ++i) {
            char c = sexp.charAt(i);
            if (c == BRACKET_START_C) {
                bStart++;
            } else if (c == BRACKET_CLOSE_C) {
                bClose++;
                if (bClose == bStart) {
                    return true;
                }
            }
        }

        return false;
    }
}
