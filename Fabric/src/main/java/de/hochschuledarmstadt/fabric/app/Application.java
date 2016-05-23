package de.hochschuledarmstadt.fabric.app;

import com.sun.net.httpserver.HttpServer;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.client.SocketClientFactory;
import de.hochschuledarmstadt.config.Credential;
import de.hochschuledarmstadt.config.CredentialParser;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONObject;

import javax.ws.rs.core.UriBuilder;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;

public class Application {

    public static final int AMOUNT_OF_PRINTER_PER_FABRIC = 3;

    private static final String MODULE_DASHBOARD = "dashboard";
    private static final String DEFAULT_DB_IP = "127.0.0.1";
    private static final int DEFAULT_DB_PORT = 9999;

    public static final String LOCAL_IP = "127.0.0.1";
    public static final int DEFAULT_REST_PORT = 1111;

    private ArrayList<Process> processes = new ArrayList<Process>();

    private ISocketClient client;
    private String uniqueId;
    private RestUri uri;

    public static void main(String[] args){
        Application app = new Application();
        app.start(args);
    }

    private void start(String[] args) {

        registerShutdownHook();

        uri = readUriForRestFromCommandLine(args);

        List<Integer> ports = generateUniquePortsForSubModules(args);

        Credential dashboardCredential =
                CredentialParser.parse(MODULE_DASHBOARD, args, Credential.PROTOCOL_UDP, DEFAULT_DB_IP, DEFAULT_DB_PORT);

        client = new SocketClientFactory(dashboardCredential).createSocket();

        notifyDashboardFabricBootUpCompleted();

        Map<Integer, ISocketClient> panelClients = connectToSubModulesInFabric(ports);

        HttpServer server = createHttpServer(uri, panelClients);
        server.start();

    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                killProcessesOfSubModules();
                notifyDashboardOfFabricShutdown();
            }
        }));
    }

    private void notifyDashboardFabricBootUpCompleted() {
        JSONObject bootUpMessage = new JSONObject();
        bootUpMessage.put("type", "bootup");
        bootUpMessage.put("ip", uri.getIp());
        bootUpMessage.put("port", uri.getPort());
        uniqueId = UUID.randomUUID().toString();
        bootUpMessage.put("id", uniqueId);

        try {
            client.sendMessage(bootUpMessage.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyDashboardOfFabricShutdown() {
        JSONObject message = new JSONObject();
        message.put("type", "shutdown");
        message.put("id", uniqueId);
        try {
            client.sendMessage(message.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void killProcessesOfSubModules() {
        System.out.println("killing processes");
        for (Process process : processes)
            process.destroy();
    }

    private Map<Integer, ISocketClient> connectToSubModulesInFabric(List<Integer> ports) {
        Map<Integer, ISocketClient> panelClients = new HashMap<Integer, ISocketClient>();
        int id = 1;
        for (int port : ports){
            Credential credential = new Credential(Credential.PROTOCOL_TCP, LOCAL_IP, port);
            ISocketClient client = new SocketClientFactory(credential).createSocket();
            panelClients.put(id++, client);
        }
        return panelClients;
    }

    private HttpServer createHttpServer(RestUri restUri, Map<Integer, ISocketClient> panelClients) {
        String ip = String.format("http://%s/", restUri.getIp());
        int port = restUri.getPort();
        URI baseUri = UriBuilder.fromUri(ip).port(port).build();
        PrinterResource printerResource = new PrinterResource(panelClients);
        MaterialResource materialResource = new MaterialResource(panelClients);
        FabricResource fabricResource = new FabricResource(panelClients.keySet());
        ResourceConfig config = new ResourceConfig();
        config.register(printerResource);
        config.register(materialResource);
        config.register(fabricResource);
        return JdkHttpServerFactory.createHttpServer(baseUri, config, false);
    }

    private List<Integer> generateUniquePortsForSubModules(String[] args) {
        List<Integer> ports = new ArrayList<Integer>();
        int seed = readSocketPortSeedFromCommandLine(args);
        for (int i = 0; i < AMOUNT_OF_PRINTER_PER_FABRIC; i++){
            seed = createExternalModuleProcesses(seed);
            ports.add(seed);
            seed = updateSeed(seed);
        }
        return ports;
    }

    private static int updateSeed(int seed) {
        return seed + 5;
    }

    private int createExternalModuleProcesses(int seed) {
        try {
            ProcessBuilder mpb = new ProcessBuilder("java", "-jar", findJarPathForModule("Material"), String.format("tcp://127.0.0.1:%s", seed));
            int materialPort = seed++;
            processes.add(mpb.start());
            ProcessBuilder ppb = new ProcessBuilder("java", "-jar", findJarPathForModule("Printer"), String.format("tcp://127.0.0.1:%s", seed));
            int printerPort = seed++;
            processes.add(ppb.start());
            Thread.sleep(1000);
            ProcessBuilder cpb = new ProcessBuilder("java", "-jar", findJarPathForModule("ControlPanel"), String.format("tcp://127.0.0.1:%s", seed), String.format("printer=tcp://127.0.0.1:%s", printerPort), String.format("material=tcp://127.0.0.1:%s", materialPort));
            processes.add(cpb.start());
            Thread.sleep(1000);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return seed;
    }

    private String findJarPathForModule(String moduleName) {
        if (new File(moduleName + ".jar").exists())
            return moduleName;
        String jarPath = String.format("out/artifacts/%s_jar/%s.jar", moduleName, moduleName);
        if (new File(jarPath).exists())
            return jarPath;
        jarPath = String.format("../%s_jar/%s.jar", moduleName, moduleName);
        if (new File(jarPath).exists())
            return jarPath;
        throw new RuntimeException(moduleName + ".jar not found!");
    }

    private RestUri readUriForRestFromCommandLine(String[] args) {
        int port = DEFAULT_REST_PORT;
        String ip  = LOCAL_IP;
        for (String command : args) {
            try {
                String[] pair = command.split("=");
                if (pair[0].equals("uri")) {
                    String strIpAndPort = pair[1];
                    String[] ipAndPort = strIpAndPort.split(":");
                    port = Integer.parseInt(ipAndPort[1]);
                    ip = ipAndPort[0];
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new RestUri(ip, port);
    }

    private static int readSocketPortSeedFromCommandLine(String[] args) {
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
