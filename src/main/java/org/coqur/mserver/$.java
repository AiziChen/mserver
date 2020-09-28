package org.coqur.mserver;

/**
 * Tools
 *
 * @author Quanyec
 */
public class $ {
    private final static int HEX_BASE_SIZE = 16;

    public static long hexToLong(String hex) {
        int len = hex.length();
        int i = 0;
        if (len == 0) {
            return 0;
        }
        int base = 1;
        if (hex.charAt(0) == '-') {
            base = -1;
            i = 1;
        }
        long total = 0;
        for (; i < len; ++i) {
            char c = hex.charAt(i);
            if (c >= '0' && c <= '9') {
                int n = c - '0';
                total = total + n * (long) Math.pow(HEX_BASE_SIZE, len - 1 - i);
            } else if (c >= 'a' && c <= 'f') {
                int n = 10 + c - 'a';
                total = total + n * (long) Math.pow(HEX_BASE_SIZE, len - 1 - i);
            } else if (c >= 'A' && c <= 'F') {
                int n = 10 + c - 'A';
                total = total + n * (long) Math.pow(HEX_BASE_SIZE, len - 1 - i);
            }
        }

        return base * total;
    }
}
