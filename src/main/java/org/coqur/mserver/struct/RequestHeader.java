package org.coqur.mserver.struct;

public class RequestHeader {
    private String header;

    public RequestHeader(String header) {
        this.header = header;
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

}
