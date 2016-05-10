package de.hochschuledarmstadt.transport.udp;


import de.hochschuledarmstadt.component.IMessageConsumer;

import java.net.DatagramSocket;

public class UdpMessageProcessorFactory {

    private final IMessageConsumer messageProcessor;

    public UdpMessageProcessorFactory(IMessageConsumer messageProcessor){
        this.messageProcessor = messageProcessor;
    }

    public UdpMessageProcessor create(DatagramSocket socket){
        return new UdpMessageProcessor(new UdpMessageSenderFactory(socket), messageProcessor);
    }
}
