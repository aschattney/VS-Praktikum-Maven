package de.hochschuledarmstadt.transport.model;


import org.json.JSONObject;

public class TcpMessage extends TransportMessage{
    public TcpMessage(JSONObject jsonMessage) {
        super(jsonMessage);
    }
}
