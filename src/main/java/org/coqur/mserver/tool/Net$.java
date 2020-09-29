package org.coqur.mserver.tool;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Net$ {

    private final $ tools;

    private Net$() {
        this.tools = $.newInstance();
    }

    public static Net$ newInstance() {
        return new Net$();
    }

    public void writeExactlySize(InputStream dis, OutputStream cos, long exactlySize) throws IOException {
        int totalRead = 0;
        int hasRead;
        byte[] buff = new byte[2048];
        while ((hasRead = dis.read(buff)) != -1) {
            cos.write(buff, 0, hasRead);
            totalRead += hasRead;
            if (totalRead >= exactlySize) {
                break;
            }
        }
    }

    public void writeChunked(InputStream dis, OutputStream cos) throws IOException {
        // `transfer-encoding` value is chunked
        while (true) {
            String chunkedLenStr = tools.readLine(dis);
            if (chunkedLenStr == null) {
                break;
            }
            cos.write(chunkedLenStr.getBytes());

            chunkedLenStr = chunkedLenStr.trim();
            if (chunkedLenStr.equals("0")) {
                System.out.println("chunked code is now zero.");
                cos.write("\r\n".getBytes());
                break;
            }
            long chunkedLen = tools.hexToLong(chunkedLenStr);
            int b;
            for (int totalRead = 0; (b = dis.read()) != -1; totalRead++) {
                cos.write(b);
                if (totalRead >= chunkedLen + 1) {
                    break;
                }
            }
        }
    }

    public String readIsHeader(InputStream is) throws IOException {
        StringBuilder hsb = new StringBuilder();
        String line;
        while ((line = tools.readLine(is)) != null) {
            hsb.append(line);
            if (line.isBlank()) {
                break;
            }
        }
        return hsb.toString();
    }


    public String readIsHeaderAndWriteToDos(InputStream is, OutputStream dos) throws IOException {
        StringBuilder hsb = new StringBuilder();
        String line;
        while ((line = tools.readLine(is)) != null) {
            dos.write(line.getBytes());
            hsb.append(line);
            if (line.isBlank()) {
                break;
            }
        }
        return hsb.toString();
    }
}
