package de.hochschuledarmstadt.client;

import de.hochschuledarmstadt.config.Credential;

import java.net.DatagramSocket;
import java.net.SocketException;

public class SocketClientFactory {

    private Credential credential;

    public SocketClientFactory(Credential credential){
        this.credential = credential;
    }

    public ISocketClient createSocket() {
        return isTcpTransport() ? buildTcpSocket() : buildUdpSocket();
    }

    private boolean isTcpTransport() {
        return credential.getProtocol().equals(Credential.PROTOCOL_TCP);
    }

    private ISocketClient buildTcpSocket(){
        String ip = credential.getIp();
        int port = credential.getPort();
        return new TcpSocketClient(ip, port);
    }

    private ISocketClient buildUdpSocket(){
        try {
            String ip = credential.getIp();
            int port = credential.getPort();
            return new UdpSocketClient(new DatagramSocket(), ip, port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

}
