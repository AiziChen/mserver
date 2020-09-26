package org.coqur.mserver;

import org.coqur.mserver.record.Config;
import org.quanye.sobj.SObjParser;
import org.quanye.sobj.exception.InValidSObjSyntaxException;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private Config config;

    public Server(String[] args) {
        String cfgPath = args[0];
        try {
            initConfig(cfgPath);
        } catch (IOException | InValidSObjSyntaxException e) {
            e.printStackTrace();
        }
        try {
            startDaemon();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startDaemon() throws IOException {
        ServerSocket ss = new ServerSocket(config.listenPort);
        while (true) {
            Socket s = ss.accept();
            BypassServeRunnable serve = new BypassServeRunnable(s, config);
            new Thread(serve).start();
        }
    }

    private void initConfig(String cfgPath) throws IOException, InValidSObjSyntaxException {
        FileInputStream fis = new FileInputStream(cfgPath);
        String content = new String(fis.readAllBytes());
        config = SObjParser.toObject(content, Config.class);
        System.out.println(config);
    }

}
