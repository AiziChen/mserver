package org.coqur.mserver;

import org.coqur.mserver.record.Config;
import org.coqur.mserver.record.Destinations;
import org.coqur.mserver.struct.RequestHeader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.logging.Level;

import static org.coqur.mserver.Main.LOGGER;

public class BypassServeRunnable implements Runnable {
    private final Socket socket;
    private final Config config;
    private final $ tools;

    public BypassServeRunnable(Socket s, Config config) {
        this.socket = s;
        this.config = config;
        this.tools = $.newInstance();
    }

    @Override
    public void run() {
        String hostName = socket.getInetAddress().getHostName();
        LOGGER.info(String.format("address %s now coming to processing...", hostName));
        try {
            /* Socket Operation */
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            StringBuilder hsb = new StringBuilder();
            String line;
            while ((line = tools.readLine(is)) != null) {
                hsb.append(line);
                if (line.isBlank()) {
                    break;
                }
            }
            /* Modify headers */
            String header = hsb.toString();
            String listenUrl = String.format("%s:%s", hostName, config.listenPort);
            for (Destinations d : config.destinations) {
                String reqUri = new RequestHeader(header).getRequestUri();
                for (String path : d.paths) {
                    if (reqUri.startsWith(path)) {
                        String destUrl = String.format("%s:%s", d.host, d.port);
                        header = header.replaceAll(listenUrl, destUrl);
                        LOGGER.info(d.host + ": " + d.port + " -> " + reqUri);
                        try {
                            bypassProcess(os, header, d.host, d.port);
                        } catch (IOException e) {
                            LOGGER.log(Level.WARNING, e.getMessage());
                        }
                        break;
                    }
                }
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void bypassProcess(OutputStream os, String header, String dHost, int dPort) throws IOException {
        /* Read data from the destination, and write to the client */
        Socket ds = new Socket(dHost, dPort);
        OutputStream dos = ds.getOutputStream();
        PrintStream ps = new PrintStream(dos);
        // writing data
        header.lines().forEach(ps::println);
        // reading data
        InputStream dis = ds.getInputStream();
        StringBuilder dhsb = new StringBuilder();
        String line;
        while ((line = tools.readLine(dis)) != null) {
            os.write(line.getBytes());
            dhsb.append(line);
            if (line.isBlank()) {
                break;
            }
        }
        RequestHeader drh = new RequestHeader(dhsb.toString());
        String scl = drh.get("content-length");
        if (scl == null) {
            String transferEncoding = drh.get("transfer-encoding");
            if (transferEncoding != null) {
                if (transferEncoding.equals("chunked")) {
                    // `transfer-encoding` value is chunked
                    while (true) {
                        String chunkedLenStr = tools.readLine(dis);
                        if (chunkedLenStr == null) {
                            break;
                        }
                        os.write(chunkedLenStr.getBytes());

                        chunkedLenStr = chunkedLenStr.trim();
                        if (chunkedLenStr.equals("0")) {
//                            System.out.println("chunked code is now zero.");
                            os.write("\r\n".getBytes());
                            break;
                        }
                        long chunkedLen = tools.hexToLong(chunkedLenStr);
                        int b;
                        for (int totalRead = 0; (b = dis.read()) != -1; totalRead++) {
                            os.write(b);
                            if (totalRead >= chunkedLen + 1) {
                                break;
                            }
                        }
                    }
                } else {
                    // `transfer-encoding` value is not chunked
                }
            } else {
                // both of no `content-length` and `transfer-encoding`
            }
        } else {
            // read the `content-length` length
            int cLen = Integer.parseInt(scl);
            int totalRead = 0;
            int hasRead;
            byte[] buff = new byte[2048];
            while ((hasRead = dis.read(buff)) != -1) {
                os.write(buff, 0, hasRead);
                totalRead += hasRead;
                if (totalRead >= cLen) {
                    break;
                }
            }
        }
        os.close();
    }

}
