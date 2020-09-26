package org.coqur.mserver;

import java.util.logging.Logger;

public class Main {
    public static final Logger LOGGER = Logger.getGlobal();

    public static void main(String[] args) {
        new Server(args);
    }
}
