/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.hochschuledarmstadt.controlpanel.app;

import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.client.SocketClientFactory;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.config.CredentialParser;

import java.util.concurrent.Executors;

public class Application {

    private static boolean performanceTestEnabled = false;

    private static final int DEFAULT_PRINTER_PORT = 3333;
    private static final int DEFAULT_MATERIAL_PORT = 5555;
    private static final String DEFAULT_IP = "127.0.0.1";

    private static final String MODULE_PRINTER = "printer";
    private static final String MODULE_MATERIAL = "material";

    private static final String ARG_PERFORMANCETEST = "PERFORMANCETEST";

    public static void main(String[] args){

        performanceTestEnabled = shouldPerformPerformanceTest(args);

        // Build credentials. A Credential consists of the used protocol, ip adress and port
        Credential printerCredential = CredentialParser.parse(MODULE_PRINTER, args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_PRINTER_PORT);
        Credential materialCredential = CredentialParser.parse(MODULE_MATERIAL, args, Credential.PROTOCOL_TCP, DEFAULT_IP, DEFAULT_MATERIAL_PORT);

        // Build the client sockets based on the specified credentials
        ISocketClient printerClient = new SocketClientFactory(printerCredential).createSocket();
        ISocketClient materialClient = new SocketClientFactory(materialCredential).createSocket();

        // Spawn a new thread who will serve as a message queue for print jobs
        PrintJobExecutor printJobExecutor = new PrintJobExecutor(System.out, printerClient, materialClient);
        Executors.newSingleThreadExecutor().submit(printJobExecutor);

        IApplicationProcessor applicationProcessor = buildApplicationProcessor(printerClient, materialClient, printJobExecutor);
        applicationProcessor.processSync();

    }

    private static IApplicationProcessor buildApplicationProcessor(ISocketClient printerClient, ISocketClient materialClient, PrintJobExecutor printJobExecutor) {
        IApplicationProcessor applicationProcessor;
        if (!performanceTestEnabled)
            applicationProcessor = new ApplicationProcessor(System.in, System.out, printJobExecutor, materialClient);
        else
            applicationProcessor = new PerformanceTestProcessor(materialClient, printerClient);
        return applicationProcessor;
    }

    private static boolean shouldPerformPerformanceTest(String[] args) {
        for (String arg : args){
            if (arg.toUpperCase().equals(ARG_PERFORMANCETEST))
                return true;
        }
        return false;
    }

}