package de.hochschuledarmstadt.component;

import org.json.JSONObject;

/**
 * Interface for handling received messages
 */
public interface IMessageConsumer {
    /**
     * In this method the received message {@code message} should be
     * handled and a response should be delivered by accessing {@code messageSender} => {@link IMessageSender#sendMessage(String)}
     * @param messageSender sender
     * @param message the received message as JSON Object
     */
    void consumeMessage(IMessageSender messageSender, JSONObject message);
}
