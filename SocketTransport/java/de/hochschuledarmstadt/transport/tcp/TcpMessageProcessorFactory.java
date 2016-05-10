package de.hochschuledarmstadt.transport.tcp;

import de.hochschuledarmstadt.component.IMessageConsumer;

import java.io.OutputStream;

public class TcpMessageProcessorFactory {

    private final TcpMessageSenderFactory messageSenderFactory;
    private final IMessageConsumer consumer;

    public TcpMessageProcessorFactory(TcpMessageSenderFactory messageSenderFactory, IMessageConsumer consumer){
        this.messageSenderFactory = messageSenderFactory;
        this.consumer = consumer;
    }

    public TcpMessageProcessor create(OutputStream outputStream){
        return new TcpMessageProcessor(messageSenderFactory.create(outputStream), consumer);
    }

}
