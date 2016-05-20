package de.hochschuledarmstadt.fabric.app;

public class RestUri {

    private final String ip;
    private final int port;

    public RestUri(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
