package de.hochschuledarmstadt.transport.udp;

import de.hochschuledarmstadt.component.IMessageReader;
import de.hochschuledarmstadt.config.Config;
import de.hochschuledarmstadt.transport.model.TransportMessage;
import de.hochschuledarmstadt.transport.model.UdpMessage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;

public class UdpMessageReader implements IMessageReader {

    public static final int MESSAGE_SIZE_BYTE_LENGTH = Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH;
    public static final String UTF_8 = "UTF-8";

    private final DatagramSocket socket;

    public UdpMessageReader(DatagramSocket socket){
        this.socket = socket;
    }

    @Override
    public TransportMessage read() throws IOException {
        byte[] messageLengthData = new byte[MESSAGE_SIZE_BYTE_LENGTH];
        DatagramPacket packet = new DatagramPacket(messageLengthData, messageLengthData.length);
        socket.receive(packet);
        int messageLength = ByteBuffer.wrap(messageLengthData).getInt();
        byte[] messageData = new byte[messageLength];
        packet.setData(messageData, 0, messageData.length);
        socket.receive(packet);
        String message = new String(packet.getData(), 0, packet.getLength(), UTF_8);
        return new UdpMessage(new JSONObject(message), packet);
    }

}
