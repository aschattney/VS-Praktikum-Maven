package de.hochschuledarmstadt.dashboard.app;

import de.hochschuledarmstadt.component.IServer;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.config.CredentialParser;
import de.hochschuledarmstadt.server.ServerSocketFactory;

import java.io.IOException;
import java.util.*;

public class Application {

    private static final int SEND_PRINT_JOB = 1;
    private static final int SEND_PRINTER_STATUS_REQUEST = 2;
    private static final int SEND_MATERIAL_STATUS_REQUEST = 3;
    private static final int SHUTDOWN = 4;

    private static final String DEFAULT_IP = "127.0.0.1";
    private static final int DEFAULT_PORT = 9999;

    private Map<String, Credential> map = new HashMap<String, Credential>();
    private Map<Integer, String> localIdToUniqueIdMap = new HashMap<Integer, String>();
    private Map<String, Integer> uniqeToLocalIdMap = new HashMap<String, Integer>();
    private IServer server;

    public static void main(String[] args) {

        Application app = new Application();
        app.start(args);

    }

    private void start(String[] args) {

        boolean test = false;

        if (args.length > 0) {
            if (args[0].toLowerCase().equals("test")) {
                test = true;
            }
        }

        Credential credential = CredentialParser.parse(args, Credential.PROTOCOL_UDP, DEFAULT_IP, DEFAULT_PORT);

        try {
            MessageConsumer messageConsumer = new MessageConsumer(map, localIdToUniqueIdMap, uniqeToLocalIdMap);
            server = new ServerSocketFactory(credential, messageConsumer).buildServer();
            server.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        IApplicationProcessor processor = null;
        if (test){
            processor = new TestProcessor(map, localIdToUniqueIdMap);
        }else{
            processor = new ApplicationProcessor(server, map, localIdToUniqueIdMap);
        }

        processor.start();

    }

}
