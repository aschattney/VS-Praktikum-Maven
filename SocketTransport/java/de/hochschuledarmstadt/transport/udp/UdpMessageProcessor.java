package de.hochschuledarmstadt.transport.udp;


import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IMessageProcessor;
import de.hochschuledarmstadt.component.IMessageSender;
import de.hochschuledarmstadt.transport.model.TransportMessage;
import de.hochschuledarmstadt.transport.model.UdpMessage;

public class UdpMessageProcessor implements IMessageProcessor {

    private final UdpMessageSenderFactory messageSenderFactory;
    private final IMessageConsumer messageConsumer;

    public UdpMessageProcessor(UdpMessageSenderFactory messageSenderFactory, IMessageConsumer messageConsumer){
        this.messageSenderFactory = messageSenderFactory;
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void processMessage(TransportMessage receivedMessage) {
        IMessageSender messageSender = messageSenderFactory.create(((UdpMessage) receivedMessage).getDatagramPacket());
        messageConsumer.consumeMessage(messageSender, receivedMessage.getJsonMessage());
    }
}
