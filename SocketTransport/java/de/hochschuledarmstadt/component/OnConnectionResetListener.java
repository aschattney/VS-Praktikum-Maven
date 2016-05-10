package de.hochschuledarmstadt.component;

/**
 * Used internally for recognizing the end of a connection. This interface is specific to the TCP implementation
 */
public interface OnConnectionResetListener {
    /**
     * Will be called if a tcp connection is closed
     */
    void onConnectionReset();
}
