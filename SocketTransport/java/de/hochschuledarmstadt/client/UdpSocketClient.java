package de.hochschuledarmstadt.client;

import de.hochschuledarmstadt.config.Config;
import org.json.JSONObject;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public class UdpSocketClient implements ISocketClient{

    private static final int MESSAGE_SIZE_BYTE_LENGTH = Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH;
    private static final String UTF_8 = "UTF-8";

    private String ip;
    private int port;
    private DatagramSocket socket;

    public UdpSocketClient(DatagramSocket socket, String ip, int port){
        this.socket = socket;
        try {
            this.socket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        this.ip = ip;
        this.port = port;
    }

    @Override
    public JSONObject sendMessage(String message) throws IOException {
        internalSendMessage(message);
        return readResponse();
    }

    private JSONObject readResponse() throws IOException {
        byte[] messageLengthData = new byte[MESSAGE_SIZE_BYTE_LENGTH];
        DatagramPacket dataLengthPacket = new DatagramPacket(messageLengthData, messageLengthData.length);
        socket.receive(dataLengthPacket);
        int messageLength = ByteBuffer.wrap(messageLengthData).getInt();
        byte[] messageData = new byte[messageLength];
        DatagramPacket payloadPacket = new DatagramPacket(messageData, messageData.length);
        socket.receive(payloadPacket);
        String message = new String(payloadPacket.getData(), 0, payloadPacket.getLength(), UTF_8);
        return new JSONObject(message);
    }

    private void internalSendMessage(String message) {
        try {
            InetAddress ia = InetAddress.getByName(ip);
            byte[] data = message.getBytes("UTF-8");
            byte[] messageLength = intToBytes(data.length);
            DatagramPacket packetDataLength = new DatagramPacket(messageLength, messageLength.length, ia, port);
            DatagramPacket packet = new DatagramPacket(data, data.length, ia, port);
            socket.send(packetDataLength);
            socket.send( packet );
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
