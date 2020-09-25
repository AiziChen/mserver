package org.quanye.sobj.tools;

import static org.quanye.sobj.SObjParser.*;

/**
 * Sexp Tools
 * <p>
 * This source code is license on the Apache-License v2.0
 *
 * @author Quanyec
 */
public class S$ {

    public static String removeBoilerplateEmptyCode(String sexp) {
        /* example:
         * ;; define and obj
         * (*obj ;; obj
         *   (name "DavidChen") ;; name
         *   (age 26))  ;; age
         * ---->
         * (*obj
         *   (name "David")
         *   (age 26))
         */
        String regex = String.format("(%s)(.*)(\n)", COMMENT_C);
        sexp = sexp.replaceAll(regex, "");

        // example: (  a  (  b  c  )  ) -> (a(b  c))
        regex = String.format("(\\s*)(\\%c)(\\s*)", BRACKET_START_C);
        sexp = sexp.replaceAll(regex, BRACKET_START);
        regex = String.format("(\\s*)(\\%c)(\\s*)", BRACKET_CLOSE_C);
        sexp = sexp.replaceAll(regex, BRACKET_CLOSE);

        // example: (a(b   c)) -> (a(b c))
        sexp = sexp.replaceAll("\\s+", SEPARATOR);

        return sexp;
    }

    public static String minimizeSexp(String sObj) {
        // example: (name "DavidChen") -> (name"DavidChen")
        String regex = String.format("(\\s)(\\%c)", '\"');
        sObj = sObj.replaceAll(regex, "\"");

        // example: (*obj (name"DavidChen")) -> (*obj(name"DavidChen"))
        regex = String.format("(\\s)(\\%c)", BRACKET_START_C);
        sObj = sObj.replaceAll(regex, BRACKET_START);

        return sObj;
    }

    public static boolean isValidSexp(String sexp) {
        int lb = 0;
        int rb = 0;
        int i = 0;
        int sLen = sexp.length();
        for (; i < sLen; ++i) {
            if (BRACKET_START_C == sexp.charAt(i)) {
                lb++;
            } else if (BRACKET_CLOSE_C == sexp.charAt(i)) {
                rb++;
                if (rb == lb) {
                    break;
                }
            }
        }
        return (lb == rb) && ((i + 1) == sLen);
    }


    public static boolean isNull(String sexp) {
        return sexp.equals(NULL);
    }


    public static boolean isPair(String sexp) {
        if (!sexp.startsWith(BRACKET_START)) {
            return false;
        }
        return !isNull(sexp);
    }


    public static String car(String sexp) {
        int i = 1;
        if (sexp.charAt(1) == BRACKET_START_C) {
            int bStart = 0;
            int bClose = 0;
            for (; i < sexp.length(); ++i) {
                char c = sexp.charAt(i);
                if (c == BRACKET_START_C) {
                    bStart++;
                } else if (c == BRACKET_CLOSE_C) {
                    bClose++;
                    if (bClose >= bStart) {
                        i++;
                        break;
                    }
                }
            }
        } else if (sexp.charAt(1) == '\"') {
            for (i = 2; i < sexp.length(); ++i) {
                char c = sexp.charAt(i);
                if (c == '\"') {
                    i++;
                    break;
                }
            }
        } else if (sexp.charAt(1) == '\'') {
            for (i = 2; i < sexp.length(); ++i) {
                char c = sexp.charAt(i);
                if (c == BRACKET_CLOSE_C || c == SEPARATOR_C) {
                    i++;
                    break;
                }
            }
        } else {
            for (; i < sexp.length(); ++i) {
                char c = sexp.charAt(i);
                if (c == BRACKET_START_C || c == SEPARATOR_C || c == BRACKET_CLOSE_C || c == '\"') {
                    break;
                }
            }
        }
        return sexp.substring(1, i);
    }

    public static String cdr(String sexp) {
        int i = car(sexp).length();
        if (sexp.length() > i + 1 && sexp.charAt(i + 1) == SEPARATOR_C) {
            i++;
        }
        return BRACKET_START + sexp.substring(i + 1);
    }

}
