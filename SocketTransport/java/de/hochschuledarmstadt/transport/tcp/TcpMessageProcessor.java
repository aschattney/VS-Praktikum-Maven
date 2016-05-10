package de.hochschuledarmstadt.transport.tcp;

import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IMessageProcessor;
import de.hochschuledarmstadt.component.IMessageSender;
import de.hochschuledarmstadt.transport.model.TcpMessage;
import de.hochschuledarmstadt.transport.model.TransportMessage;

public class TcpMessageProcessor implements IMessageProcessor {

    private final TcpMessageSender messageSender;
    private final IMessageConsumer messageConsumer;

    public TcpMessageProcessor(TcpMessageSender messageSender, IMessageConsumer messageConsumer){
        this.messageSender = messageSender;
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void processMessage(TransportMessage receivedMessage) {
        processReceivedMessage(messageSender, (TcpMessage) receivedMessage);
    }

    protected void processReceivedMessage(IMessageSender messageSender, TcpMessage receivedMessage){
        messageConsumer.consumeMessage(messageSender, receivedMessage.getJsonMessage());
    }
}
