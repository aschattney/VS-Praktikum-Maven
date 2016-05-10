package de.hochschuledarmstadt.component;

import java.io.IOException;

/**
 * Interface for opening and binding server sockets
 */
public interface IServer {
    /**
     * Opens a server socket
     * @throws IOException will be thrown if an error occurs
     */
    void open() throws IOException;

    /**
     * Closes the server socket
     */
    void close();
}
