package de.hochschuledarmstadt.fabric.app;

import com.sun.net.httpserver.HttpServer;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.client.SocketClientFactory;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.config.CredentialParser;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Application {

    private static final String DEFAULT_CONTROLPANEL_IP = "127.0.0.1";
    private static final String MODULE_PANEL = "panel";

    private static ArrayList<Process> processes = new ArrayList<Process>();

    public static void main(String[] args){

        List<Integer> ports = new ArrayList<Integer>();

        int seed = readSocketPortSeed(args);

        for (int i = 0; i < 3; i++){
            seed = spawnWholePrinter(seed);
            ports.add(seed);
            seed = updateSeed(seed);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                System.out.println("killing processes");
                for (Process process : processes)
                    process.destroy();
            }
        }));

        int restPort = readPortForRest(args);

        Map<Integer, ISocketClient> panelClients = new HashMap<Integer, ISocketClient>();
        int id = 1;

        for (int port : ports){
            Credential credential = new Credential(Credential.PROTOCOL_TCP, "127.0.0.1", port);
            ISocketClient client = new SocketClientFactory(credential).createSocket();
            panelClients.put(id++, client);
        }

        URI baseUri = UriBuilder.fromUri("http://localhost/").port(restPort).build();

        PrinterResource printerResource = new PrinterResource(panelClients);
        MaterialResource materialResource = new MaterialResource(panelClients);
        FabricResource fabricResource = new FabricResource(panelClients.keySet());
        ResourceConfig config = new ResourceConfig();
        config.register(printerResource);
        config.register(materialResource);
        config.register(fabricResource);
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseUri, config);
    }

    private static int updateSeed(int seed) {
        return seed + 5;
    }

    private static int spawnWholePrinter(int seed) {
        try {
            ProcessBuilder mpb = new ProcessBuilder("java", "-jar", "out/artifacts/Material_jar/Material.jar", String.format("tcp://127.0.0.1:%s", seed));
            int materialPort = seed++;
            processes.add(mpb.start());
            ProcessBuilder ppb = new ProcessBuilder("java", "-jar", "out/artifacts/Printer_jar/Printer.jar", String.format("tcp://127.0.0.1:%s", seed));
            int printerPort = seed++;
            processes.add(ppb.start());
            Thread.sleep(1000);
            ProcessBuilder cpb = new ProcessBuilder("java", "-jar", "out/artifacts/ControlPanel_jar/ControlPanel.jar", String.format("tcp://127.0.0.1:%s", seed), String.format("printer=tcp://127.0.0.1:%s", printerPort), String.format("material=tcp://127.0.0.1:%s", materialPort));
            processes.add(cpb.start());
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return seed;
    }

    private static int readPortForRest(String[] args) {
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
        return restPort;
    }

    private static int readSocketPortSeed(String[] args) {
        int seed = 10000;
        for (String command : args) {
            try {
                String[] pair = command.split("=");
                if (pair[0].equals("seed")) {
                    seed = Integer.parseInt(pair[1].trim());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return seed;
    }

}
