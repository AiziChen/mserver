package org.coqur.mserver;

import org.coqur.mserver.record.Config;
import org.coqur.mserver.record.Destinations;
import org.coqur.mserver.struct.RequestHeader;
import org.coqur.mserver.tool.$;
import org.coqur.mserver.tool.Net$;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

import static org.coqur.mserver.Main.LOGGER;

public class BypassServeRunnable implements Runnable {
    private final Socket socket;
    private final Config config;
    private final $ tools;
    private final Net$ netTools;

    public BypassServeRunnable(Socket s, Config config) {
        this.socket = s;
        this.config = config;
        this.tools = $.newInstance();
        this.netTools = Net$.newInstance();
    }

    @Override
    public void run() {
        String hostName = socket.getInetAddress().getHostName();
        LOGGER.info(String.format("address %s now coming to processing...", hostName));
        try {
            /* Socket Operation */
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();
            /* Modify headers */
            String header = netTools.readIsHeader(is);
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
                            LOGGER.error(e.getMessage());
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
        String dHeader = netTools.readIsHeader(dis);
        RequestHeader drh = new RequestHeader(dHeader);
        String scl = drh.get("content-length");
        if (scl == null) {
            String transferEncoding = drh.get("transfer-encoding");
            if (transferEncoding != null) {
                if (transferEncoding.equals("chunked")) {
                    // `transfer-encoding` value is chunked
                    netTools.writeChunked(dis, os);
                } else {
                    // `transfer-encoding` value is not chunked
                }
            } else {
                // both of no `content-length` and `transfer-encoding`
            }
        } else {
            // read the `content-length` length
            int cLen = Integer.parseInt(scl);
            netTools.writeExactlySize(dis, os, cLen);
        }
        os.close();
    }

}
