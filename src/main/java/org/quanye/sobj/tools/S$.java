package org.quanye.sobj.tools;

import java.util.stream.Collectors;

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
         * (obj ;; obj
         *   (name "DavidChen") ;; name
         *   (age 26))  ;; age
         * ---->
         * (obj
         *   (name "David")
         *   (age 26))
         */
        sexp = sexp.lines().map(s -> {
            int i = s.indexOf(COMMENT_C);
            return i != -1 ? s.substring(0, i) : s;
        }).collect(Collectors.joining());

        // example: (  a  (  b c  )  ) -> (a(b c))
        sexp = sexp.replaceAll("(\\s*)(\\()(\\s*)", BRACKET_START);
        sexp = sexp.replaceAll("(\\s*)(\\))(\\s*)", BRACKET_CLOSE);

        // example: (a(b   c)) -> (a(b c))
        sexp = sexp.replaceAll("\\s+", " ");

        return sexp;
    }

    public static boolean isValidSexp(String sexp) {
        int lb = 0;
        int rb = 0;
        for (int i = 0; i < sexp.length(); ++i) {
            if (BRACKET_START_C == sexp.charAt(i)) {
                lb++;
            } else if (BRACKET_CLOSE_C == sexp.charAt(i)) {
                rb++;
            }
        }
        return lb == rb;
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
