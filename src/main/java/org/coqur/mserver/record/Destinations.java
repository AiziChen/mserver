package org.coqur.mserver.record;

import java.util.Arrays;

public class Destinations {
    public String host;
    public Integer port;
    public String[] paths;

    @Override
    public String toString() {
        return "Destinations{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", paths=" + Arrays.toString(paths) +
                '}';
    }
}
