package de.hochschuledarmstadt.material.app;

import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IServer;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.config.CredentialParser;
import de.hochschuledarmstadt.server.ServerSocketFactory;

import java.io.IOException;

public class MaterialServerExecutor implements Runnable {

    public static final int DEFAULT_PORT = 5555;
    public static final String DEFAULT_IP = "127.0.0.1";

    private final String[] args;
    private final IMessageConsumer messageConsumer;

    public MaterialServerExecutor(String[] args, IMessageConsumer messageConsumer){
        this.args = args;
        this.messageConsumer = messageConsumer;
    }

    @Override
    public void run() {
        Credential credentials = CredentialParser.parse(args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_PORT);
        ServerSocketFactory serverSocketFactory = new ServerSocketFactory(credentials, messageConsumer);
        IServer server = serverSocketFactory.buildServer();
        try {
            server.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
