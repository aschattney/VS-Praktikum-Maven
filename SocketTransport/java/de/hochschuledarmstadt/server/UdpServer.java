package de.hochschuledarmstadt.server;

import de.hochschuledarmstadt.component.IServer;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.transport.processor.TransportProcessor;
import de.hochschuledarmstadt.transport.udp.UdpMessageProcessor;
import de.hochschuledarmstadt.transport.udp.UdpMessageProcessorFactory;
import de.hochschuledarmstadt.transport.udp.UdpMessageReader;

import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UdpServer implements IServer {

    private final ExecutorService executorService;
    private final UdpMessageProcessorFactory processorFactory;
    private final Credential credential;
    private DatagramSocket socket;

    private UdpServer(Credential credential, ExecutorService executorService, UdpMessageProcessorFactory processorFactory){
        this.credential = credential;
        this.executorService = executorService;
        this.processorFactory = processorFactory;
    }

    @Override
    public void open() {
        try {
            final String ip = credential.getIp();
            final int port = credential.getPort();
            socket = new DatagramSocket(port, Inet4Address.getByName(ip));
            socket.setSoTimeout(5000);
            UdpMessageProcessor processor = processorFactory.create(socket);
            UdpMessageReader messageReader = new UdpMessageReader(socket);
            executorService.submit(new TransportProcessor(messageReader, processor));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        socket.close();
        executorService.shutdown();
    }

    public static class Builder{

        private final Credential credential;
        private UdpMessageProcessorFactory processorFactory;

        public Builder(Credential credential){
            this.credential = credential;
        }

        public void setMessageProcessorFactory(UdpMessageProcessorFactory processorFactory){
            this.processorFactory = processorFactory;
        }

        public UdpServer create(){
            return new UdpServer(credential, Executors.newCachedThreadPool(), processorFactory);
        }

    }

}
