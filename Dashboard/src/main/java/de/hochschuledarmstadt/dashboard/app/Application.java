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

        Credential credential = CredentialParser.parse(args, Credential.PROTOCOL_UDP, DEFAULT_IP, DEFAULT_PORT);

        Scanner sc = new Scanner(System.in);
        boolean isRunning = true;

        try {
            MessageConsumer messageConsumer = new MessageConsumer(map, localIdToUniqueIdMap, uniqeToLocalIdMap);
            server = new ServerSocketFactory(credential, messageConsumer).buildServer();
            server.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (isRunning) {
            printMenu();
            int input = sc.nextInt();
            switch (input) {
                case SEND_PRINT_JOB: {
                    try {
                        sendPrintJob(sc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case SEND_PRINTER_STATUS_REQUEST: {
                    try {
                        sendPrinterStatusRequest(sc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case SEND_MATERIAL_STATUS_REQUEST: {
                    try {
                        sendMaterialStatusRequest(sc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case SHUTDOWN:
                    server.close();
                    isRunning = false;
                    break;
            }

        }

    }

    private void sendMaterialStatusRequest(Scanner sc) throws Exception {
        Identifier identifier = getIdentifier(sc);
        Credential c = map.get(localIdToUniqueIdMap.get(identifier.getFabricId()));
        String ipAndPort = buildUrl(c);
        MaterialService materialService = new MaterialService(ipAndPort, identifier.getPrinterId());
        String response = materialService.requestMaterialStatus();
        System.out.println(response);
    }

    private void sendPrinterStatusRequest(Scanner sc) throws Exception {
        Identifier identifier = getIdentifier(sc);
        Credential c = map.get(localIdToUniqueIdMap.get(identifier.getFabricId()));
        String ipAndPort = buildUrl(c);
        PrinterService printerService = new PrinterService(ipAndPort, identifier.getPrinterId());
        String response = printerService.requestPrinterStatus();
        System.out.println(response);
    }

    private void sendPrintJob(Scanner sc) throws Exception {
        Identifier identifier = getIdentifier(sc);
        Credential c = map.get(localIdToUniqueIdMap.get(identifier.getFabricId()));
        String ipAndPort = buildUrl(c);
        String jobName = getJobName(sc);
        JobService jobService = new JobService(ipAndPort, identifier.getPrinterId(), jobName);
        String response = jobService.sendJob();
        System.out.println(response);
    }

    private String buildUrl(Credential c) {
        return "http://" + c.getIp() + ":" + c.getPort();
    }

    private String getJobName(Scanner sc) {
        System.out.println("(1) Nail");
        System.out.println("(2) Hammer");
        int job = sc.nextInt();
        return (job == 1) ? "nail" : "hammer";
    }

    private void printMenu() {
        System.out.println("(1) Print");
        System.out.println("(2) Printer Status");
        System.out.println("(3) Material Status");
        System.out.println("(4) Shutdown");
    }

    private Identifier getIdentifier(Scanner sc) {
        String s = getIdsAsString();
        System.out.println(String.format("Fabrics available with id: (%s)", s));
        System.out.print("Choose a fabric id: ");
        int fabricId = sc.nextInt();
        Credential c = map.get(localIdToUniqueIdMap.get(fabricId));
        String ipAndPort = buildUrl(c);
        FabricService service = new FabricService(ipAndPort, fabricId);
        try {
            String response = service.execute();
            System.out.println(String.format("Printer available in fabric %s with id: (%s)", fabricId, response));
            System.out.print("Choose a printer id: ");
            int printerId = sc.nextInt();
            return new Identifier(fabricId, printerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getIdsAsString() {
        List<Integer> ids = buildIdList();
        return joinIdsToCommaSeparatedString(ids);
    }

    private String joinIdsToCommaSeparatedString(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            sb.append(ids.get(i));
            if (i < ids.size() - 1)
                sb.append(", ");
        }
        return sb.toString();
    }

    private List<Integer> buildIdList() {
        List<Integer> ids = new ArrayList<Integer>();
        for (Map.Entry<Integer, String> entry : localIdToUniqueIdMap.entrySet()) {
            ids.add(entry.getKey());
        }
        return ids;
    }
}
