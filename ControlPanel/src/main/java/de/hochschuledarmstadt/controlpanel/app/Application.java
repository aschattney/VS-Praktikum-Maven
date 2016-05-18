/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hochschuledarmstadt.controlpanel.app;

import com.sun.net.httpserver.HttpServer;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.client.SocketClientFactory;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.config.CredentialParser;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.concurrent.Executors;

public class Application {

    private static final int DEFAULT_PRINTER_PORT = 3333;
    private static final int DEFAULT_MATERIAL_PORT = 5555;

    private static final String DEFAULT_IP = "127.0.0.1";
    public static final String MODULE_PRINTER = "printer";
    private static final String MODULE_MATERIAL = "material";

    public static void main(String[] args){

        int restPort = 1111;

        for (String command : args) {
            try {
                String[] pair = command.split("=");
                if (pair[0].equals("port")) {
                    restPort = Integer.parseInt(pair[1].trim());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        // Build credentials. A Credential consists of the used protocol, ip adress and port
        Credential printerCredential = CredentialParser.parse(MODULE_PRINTER, args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_PRINTER_PORT);
        Credential materialCredential = CredentialParser.parse(MODULE_MATERIAL, args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_MATERIAL_PORT);

        // Build the client sockets based on the specified credentials
        ISocketClient printerClient = new SocketClientFactory(printerCredential).createSocket();
        ISocketClient materialClient = new SocketClientFactory(materialCredential).createSocket();

        // Spawn a new thread who will serve as a message queue for print jobs
        PrintJobExecutor printJobExecutor = new PrintJobExecutor(System.out, printerClient, materialClient);
        Executors.newSingleThreadExecutor().submit(printJobExecutor);

        // Transforms user input into message requests and sends those requests to the server
        ApplicationProcessor userInputProcessor = new ApplicationProcessor(System.in, System.out, printJobExecutor, materialClient);
        //userInputProcessor.processSync();

        URI baseUri = UriBuilder.fromUri("http://localhost/").port(restPort).build();
        PrinterResource printerResource = new PrinterResource(printerClient, printJobExecutor);
        MaterialResource materialResource = new MaterialResource(materialClient);
        ResourceConfig config = new ResourceConfig().register(printerResource).register(materialResource); // interner aufbau vom rest-server
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);

    }

}