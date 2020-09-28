package org.coqur.mserver;

import org.coqur.mserver.record.Config;
import org.coqur.mserver.record.Destinations;
import org.coqur.mserver.struct.RequestHeader;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;

import static org.coqur.mserver.Main.LOGGER;

public class BypassServeRunnable implements Runnable {
    private final Socket socket;
    private final Config config;

    public BypassServeRunnable(Socket s, Config config) {
        this.socket = s;
        this.config = config;
    }

    @Override
    public void run() {
        String hostName = socket.getInetAddress().getHostName();
        LOGGER.info(String.format("address %s now coming to processing...", hostName));
        try {
            /* Socket Operation */
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\r\n");
                if (line.isEmpty()) {
                    break;
                }
            }
            /* Modify headers */
            String header = sb.toString();
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
        String contentLength = null;
        String line;
        while (!(line = readLine(dis)).isBlank()) {
            os.write((line).getBytes());
            if (line.toLowerCase().startsWith("content-length")) {
                contentLength = line.substring("content-length:".length()).trim();
            }
        }
        os.write("\r\n".getBytes());
        System.out.println(contentLength);
        if (contentLength == null) {
            int hasRead;
            byte[] buff = new byte[2048];
            while ((hasRead = dis.read(buff)) != -1) {
                System.out.println(new String(buff, 0, hasRead));
                os.write(buff, 0, hasRead);
            }
        } else {
            int cLen = Integer.parseInt(contentLength);
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
    }

    private String readLine(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = is.read()) != -1) {
            sb.append((char) b);
            if (b == '\n') {
                return sb.toString();
            }
        }
        return sb.toString();
    }
}
