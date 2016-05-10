package de.hochschuledarmstadt.transport.model;

import org.json.JSONObject;

public abstract class TransportMessage {

    private final JSONObject jsonMessage;

    public TransportMessage(JSONObject jsonMessage){
        this.jsonMessage = jsonMessage;
    }

    public JSONObject getJsonMessage(){
        return jsonMessage;
    }

}
