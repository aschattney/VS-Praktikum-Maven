package de.hochschuledarmstadt.component;

import java.io.IOException;

/**
 * Interface for sending messages through the socket
 */
public interface IMessageSender {
    /**
     * sends a {@code message} through the socket
     * @param message the message as a string
     * @throws IOException will be thrown if an error occurs while sending the message
     */
    void sendMessage(String message) throws IOException;
}
