package de.hochschuledarmstadt.printer.app;

import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IServer;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.config.CredentialParser;
import de.hochschuledarmstadt.server.ServerSocketFactory;

import java.io.IOException;

public class PrinterServerExecutor implements Runnable{

    private static final String DEFAULT_IP = "127.0.0.1";
    private static final int DEFAULT_PORT = 3333;

    private String[] args;
    private IMessageConsumer messageConsumer;

    public PrinterServerExecutor(String[] args, IMessageConsumer messageConsumer){
        this.args = args;
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void run() {
        try {
            Credential credentials = CredentialParser.parse(args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_PORT);
            ServerSocketFactory socketFactory = new ServerSocketFactory(credentials, messageConsumer);
            IServer server = socketFactory.buildServer();
            server.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
