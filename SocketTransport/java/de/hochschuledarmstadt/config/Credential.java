package de.hochschuledarmstadt.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a collection of necessary information to serve/establish a connection or to send packets through the network.
 * A Credential consists of the transport protocol (udp or tcp), ip adress and port
 */
public class Credential {

    public static String PROTOCOL_TCP = "TCP";
    public static String PROTOCOL_UDP = "UDP";

    @SerializedName("protocol")
    @Expose
    private String protocol;

    @SerializedName("ip")
    @Expose
    private String ip;

    @SerializedName("port")
    @Expose
    private int port;

    public Credential(String protocol, String ip, int port) {
        this.protocol = protocol;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public int hashCode() {
        return ip.hashCode() + port;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Credential))
            return false;
        return ip.equals(((Credential) obj).getIp()) && port == ((Credential) obj).getPort();
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return "IP: " + ip + " Port: " + port;
    }
}
