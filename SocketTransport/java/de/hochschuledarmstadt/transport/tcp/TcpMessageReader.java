package de.hochschuledarmstadt.transport.tcp;

import de.hochschuledarmstadt.component.IMessageReader;
import de.hochschuledarmstadt.config.Config;
import de.hochschuledarmstadt.transport.model.TcpMessage;
import de.hochschuledarmstadt.transport.model.TransportMessage;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class TcpMessageReader implements IMessageReader {

    public static final int MESSAGE_SIZE_BYTE_LENGTH = Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH;

    public static final String UTF_8 = "UTF-8";
    private final InputStream inputStream;

    public TcpMessageReader(InputStream inputStream){
        this.inputStream = inputStream;
    }

    @Override
    public TransportMessage read() throws IOException{
        byte[] dataMessageSize = new byte[MESSAGE_SIZE_BYTE_LENGTH];
        int messageLength = readMessageLength(dataMessageSize);
        JSONObject jsonObject = readPayload(messageLength);
        return new TcpMessage(jsonObject);
    }

    private JSONObject readPayload(int messageLength) throws IOException {
        int read = 0;
        byte[] data = new byte[messageLength];
        while (read < messageLength) {
            int messageLengthRead = inputStream.read(data, 0, data.length);
            if (messageLengthRead == -1) {
                throw new IOException("socket error");
            }
            read += messageLengthRead;
        }
        return new JSONObject(new String(data, UTF_8));
    }

    private int readMessageLength(byte[] dataMessageSize) throws IOException {
        int read = 0;
        int messageLength;
        while (read < MESSAGE_SIZE_BYTE_LENGTH) {
            int messageLengthRead = inputStream.read(dataMessageSize, 0, dataMessageSize.length);
            if (messageLengthRead == -1) {
                throw new IOException("socket error");
            }
            read += messageLengthRead;
            if (read == MESSAGE_SIZE_BYTE_LENGTH) {
                break;
            }
        }
        messageLength = ByteBuffer.wrap(dataMessageSize).getInt();
        return messageLength;
    }

}
