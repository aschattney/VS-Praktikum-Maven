package de.hochschuledarmstadt.client;


import de.hochschuledarmstadt.config.Config;
import org.json.JSONObject;

import javax.net.SocketFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TcpSocketClient implements ISocketClient {

    private static final String UTF_8 = "UTF-8";

    private final int port;
    private final String ip;
    private Socket socket;

    public TcpSocketClient(String ip, int port){
        this.ip = ip;
        this.port = port;
        connect();
    }

    private boolean connect(){
        try {
            socket = SocketFactory.getDefault().createSocket(ip, port);
            socket.setSoTimeout(5000);
            return socket.isConnected();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public JSONObject sendMessage(String message) throws IOException {
        reconnectIfNecessary();
        if (isConnected()) {
            OutputStream outputStream = socket.getOutputStream();
            byte[] messageBytes = message.getBytes("UTF-8");
            byte[] messageLength = intToBytes(messageBytes.length);
            outputStream.write(messageLength, 0, messageLength.length);
            outputStream.flush();
            outputStream.write(messageBytes, 0, messageBytes.length);
            outputStream.flush();
            return readResult();
        }
        return null;
    }

    private void reconnectIfNecessary() {
        if (!isConnected())
            connect();
    }

    private JSONObject readResult() {

        byte[] dataMessageSize = new byte[Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH];
        try {
            InputStream inputStream = socket.getInputStream();
            int messageLength = readMessageLength(dataMessageSize, inputStream);
            return readPayload(inputStream, messageLength);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private JSONObject readPayload(InputStream inputStream, int messageLength) throws Exception {
        int read = 0;
        byte[] data = new byte[messageLength];
        while (read < messageLength) {
            int messageLengthRead = inputStream.read(data, 0, data.length);
            if (messageLengthRead == -1) {
                throw new Exception("socket error");
            }
            read += messageLengthRead;
        }

        return new JSONObject(new String(data, UTF_8));
    }

    private int readMessageLength(byte[] dataMessageSize, InputStream inputStream) throws Exception {
        int read = 0;
        int messageLength;
        while (read < Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH) {
            int messageLengthRead = inputStream.read(dataMessageSize, 0, dataMessageSize.length);
            if (messageLengthRead == -1) {
                throw new Exception("socket error");
            }
            read += messageLengthRead;
            if (read == Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH) {
                break;
            }
        }

        messageLength = ByteBuffer.wrap(dataMessageSize).getInt();
        return messageLength;
    }

    private static byte[] intToBytes(int x) throws IOException {
        return ByteBuffer.allocate(Config.PREFIX_MESSAGE_SIZE_BYTE_LENGTH).putInt(x).array();
    }

    private boolean isConnected() {
        return socket != null && socket.isConnected();
    }

}
