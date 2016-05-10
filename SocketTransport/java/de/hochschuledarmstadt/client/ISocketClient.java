package de.hochschuledarmstadt.client;

import org.json.JSONObject;

import java.io.IOException;

/**
 * Interface for socket clients
 */
public interface ISocketClient {
    /**
     * sends a {@code message} through the socket
     * @param message the message to be send
     * @return the response from the server
     * @throws IOException will be thrown if an error occurs while sending the message or while receiving the response
     */
    JSONObject sendMessage(String message) throws IOException;
}
