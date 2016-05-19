/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hochschuledarmstadt.controlpanel.app;

import com.sun.net.httpserver.HttpServer;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.client.SocketClientFactory;
import de.hochschuledarmstadt.component.IServer;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.config.CredentialParser;
import de.hochschuledarmstadt.server.ServerSocketFactory;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Application {

    private static final int DEFAULT_PRINTER_PORT = 3333;
    private static final int DEFAULT_MATERIAL_PORT = 5555;
    private static final int DEFAULT_SERVER_PORT = 11000;

    private static final String DEFAULT_IP = "127.0.0.1";

    private static final String MODULE_PRINTER = "printer";
    private static final String MODULE_MATERIAL = "material";

    private static IServer server;

    public static void main(String[] args){

        // Build credentials. A Credential consists of the used protocol, ip adress and port
        Credential serverCredential = CredentialParser.parse(args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_SERVER_PORT);
        Credential printerCredential = CredentialParser.parse(MODULE_PRINTER, args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_PRINTER_PORT);
        Credential materialCredential = CredentialParser.parse(MODULE_MATERIAL, args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_MATERIAL_PORT);

        // Build the client sockets based on the specified credentials
        ISocketClient printerClient = new SocketClientFactory(printerCredential).createSocket();
        ISocketClient materialClient = new SocketClientFactory(materialCredential).createSocket();

        // Spawn a new thread who will serve as a message queue for print jobs
        PrintJobExecutor printJobExecutor = new PrintJobExecutor(System.out, printerClient, materialClient);
        Executors.newSingleThreadExecutor().submit(printJobExecutor);

        MessageConsumer messageConsumer = new MessageConsumer(printerClient, materialClient, printJobExecutor);
        ServerSocketFactory factory = new ServerSocketFactory(serverCredential, messageConsumer);

        server = factory.buildServer();

        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    server.open();
                    System.out.println("server open");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Transforms user input into message requests and sends those requests to the server
        ApplicationProcessor userInputProcessor = new ApplicationProcessor(System.in, System.out, printJobExecutor, materialClient);
        userInputProcessor.processSync();

    }

}