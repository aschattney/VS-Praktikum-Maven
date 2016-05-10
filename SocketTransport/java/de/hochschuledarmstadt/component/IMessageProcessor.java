package de.hochschuledarmstadt.component;

import de.hochschuledarmstadt.transport.model.TransportMessage;

public interface IMessageProcessor {
    void processMessage(TransportMessage message);
}
