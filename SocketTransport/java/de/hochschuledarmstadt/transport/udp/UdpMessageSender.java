package de.hochschuledarmstadt.transport.udp;

import de.hochschuledarmstadt.component.IMessageSender;
import de.hochschuledarmstadt.config.Config;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class UdpMessageSender implements IMessageSender {

    private static final String UTF_8 = "UTF-8";
    private final DatagramPacket datagramPacket;
    private final DatagramSocket socket;

    public UdpMessageSender(DatagramSocket socket, DatagramPacket datagramPacket){
        this.socket = socket;
        this.datagramPacket = datagramPacket;
    }

    @Override
    public void sendMessage(String message) throws IOException {
        try {
            byte[] data = message.getBytes(UTF_8);
            byte[] messageLength = intToBytes(data.length);
            datagramPacket.setData(messageLength);
            socket.send(datagramPacket);
            datagramPacket.setData(data);
            socket.send(datagramPacket);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static byte[] intToBytes(int x) throws IOException {
        return ByteBuffer.allocate(Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH).putInt(x).array();
    }

}
