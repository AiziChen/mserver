package org.coqur.mserver.record;

import java.util.Arrays;

public class Config {
    public Integer listenPort;
    public Destinations[] destinations;

    @Override
    public String toString() {
        return "Config{" +
                "listenPort=" + listenPort +
                ", destinations=" + Arrays.toString(destinations) +
                '}';
    }
}
