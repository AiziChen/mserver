package org.coqur.mserver.struct;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RequestHeader {
    private String header;
    private Map<String, String> headerMap;

    public RequestHeader(String header) {
        this.header = header;
        this.headerMap = new HashMap<>();
        header.lines().forEach(h -> {
            if (h.contains(":")) {
                int i = h.indexOf(":");
                String key = h.substring(0, i).trim().toLowerCase();
                String value = h.substring(i + 1).trim().toLowerCase();
                headerMap.put(key, value);
            }
        });
    }

    public String get(String key) {
        return headerMap.get(key);
    }

    public String getFirstLine() {
        StringBuilder sb = new StringBuilder();
        int len = header.length();
        for (int i = 0; i < len; ++i) {
            char c = header.charAt(i);
            sb.append(c);
            if (c == '\n') {
                break;
            }
        }
        return sb.toString();
    }

    public String getMethod() {
        String line = getFirstLine();
        return line.substring(0, line.indexOf(' '));
    }

    public String getRequestUri() {
        String line = getFirstLine();
        if (line.isEmpty()) {
            return "";
        }
        return line.substring(line.indexOf(' ') + 1, line.lastIndexOf(' '));
    }

    public String getHttpVersion() {
        String line = getFirstLine();
        return line.substring(line.lastIndexOf(' ')).trim();
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }
}
