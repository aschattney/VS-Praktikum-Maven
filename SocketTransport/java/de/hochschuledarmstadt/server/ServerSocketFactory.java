package de.hochschuledarmstadt.server;

import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IServer;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.transport.tcp.TcpMessageProcessorFactory;
import de.hochschuledarmstadt.transport.tcp.TcpMessageSenderFactory;
import de.hochschuledarmstadt.transport.udp.UdpMessageProcessorFactory;

public class ServerSocketFactory {

    private final Credential credential;
    private IMessageConsumer messageConsumer;

    public ServerSocketFactory(Credential credential, IMessageConsumer messageConsumer){
        this.credential = credential;
        this.messageConsumer = messageConsumer;
    }

    public IServer buildServer(){
        return isTcpTransport() ? buildTcpServer() : buildUdpServer();
    }

    private boolean isTcpTransport() {
        return credential.getProtocol().equals(Credential.PROTOCOL_TCP);
    }

    private IServer buildUdpServer(){
        UdpServer.Builder builder = new UdpServer.Builder(credential);
        builder.setMessageProcessorFactory(new UdpMessageProcessorFactory(messageConsumer));
        return builder.create();
    }

    private IServer buildTcpServer(){
        TcpServer.Builder builder = new TcpServer.Builder(credential);
        TcpMessageSenderFactory messageSenderFactory = new TcpMessageSenderFactory();
        TcpMessageProcessorFactory processorFactory = new TcpMessageProcessorFactory(messageSenderFactory, messageConsumer);
        builder.setMessageProcessorFactory(processorFactory);
        return builder.create();
    }

}
