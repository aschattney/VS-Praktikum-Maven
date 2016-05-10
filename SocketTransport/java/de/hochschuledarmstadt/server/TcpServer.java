package de.hochschuledarmstadt.server;

import de.hochschuledarmstadt.component.*;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.transport.processor.TransportProcessor;
import de.hochschuledarmstadt.transport.tcp.TcpMessageProcessorFactory;
import de.hochschuledarmstadt.transport.tcp.TcpMessageReaderFactory;

import javax.net.ServerSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpServer implements IServer, OnConnectionResetListener {

    private final ExecutorService executorService;
    private final TcpMessageReaderFactory messageReaderFactory;
    private final TcpMessageProcessorFactory messageProcessorFactory;
    private final Credential credential;
    private ServerSocket serverSocket;

    private TcpServer(Credential credential, ExecutorService executorService, TcpMessageReaderFactory messageReaderFactory, TcpMessageProcessorFactory messageProcessorFactory){
        this.credential = credential;
        this.executorService = executorService;
        this.messageReaderFactory = messageReaderFactory;
        this.messageProcessorFactory = messageProcessorFactory;
    }

    @Override
    public void open() throws IOException {
        createServerSocket();
        acceptConnection();
    }

    @Override
    public void close() {
        try {
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            serverSocket = null;
        }
    }

    private void createServerSocket() throws IOException {
        int port = credential.getPort();
        String ip = credential.getIp();
        serverSocket = ServerSocketFactory.getDefault().createServerSocket(port, 0, InetAddress.getByName(ip));
    }

    private void acceptConnection() throws IOException {
        Socket socket = serverSocket.accept();
        socket.setSoTimeout(5000);
        IMessageReader messageReader = messageReaderFactory.create(socket.getInputStream());
        IMessageProcessor messageProcessor = messageProcessorFactory.create(socket.getOutputStream());
        TransportProcessor transportProcessor = new TransportProcessor(messageReader, messageProcessor, this);
        executorService.submit(transportProcessor);
    }

    @Override
    public void onConnectionReset() {
        try {
            if (isServerSocketValid())
                acceptConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isServerSocketValid() {
        return serverSocket != null;
    }

    public static class Builder{

        private final Credential credential;

        public Builder(Credential credential){
            this.credential = credential;
        }

        private TcpMessageProcessorFactory messageProcessorFactory;

        public void setMessageProcessorFactory(TcpMessageProcessorFactory messageProcessor) {
            this.messageProcessorFactory = messageProcessor;
        }

        public TcpServer create(){
            return new TcpServer(credential, Executors.newCachedThreadPool(), new TcpMessageReaderFactory(), messageProcessorFactory);
        }
    }

}
