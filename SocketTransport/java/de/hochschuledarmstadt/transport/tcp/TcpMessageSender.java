package de.hochschuledarmstadt.transport.tcp;

import de.hochschuledarmstadt.component.IMessageSender;
import de.hochschuledarmstadt.config.Config;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class TcpMessageSender implements IMessageSender {

    private final OutputStream outputStream;

    public TcpMessageSender(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void sendMessage(String message) throws IOException {
        byte[] messageBytes = message.getBytes("UTF-8");
        byte[] messageLength = intToBytes(messageBytes.length);
        outputStream.write(messageLength, 0, messageLength.length);
        outputStream.flush();
        outputStream.write(messageBytes, 0, messageBytes.length);
        outputStream.flush();
    }

    private static byte[] intToBytes(int x) throws IOException {
        return ByteBuffer.allocate(Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH).putInt(x).array();
    }
}
