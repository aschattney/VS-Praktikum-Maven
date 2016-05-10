package de.hochschuledarmstadt.component;

import de.hochschuledarmstadt.transport.model.TransportMessage;

import java.io.IOException;

/**
 * Interface for reading messages from the socket
 */
public interface IMessageReader {
    /**
     * reads a message from the socket
     * @return the message read from the socket
     * @throws IOException will be thrown if an error occurs while reading from the socket
     */
    TransportMessage read() throws IOException;
}
