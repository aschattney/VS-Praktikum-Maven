package de.hochschuledarmstadt.dashboard.app;

import java.util.*;

public class Application {

    private static Map<Integer, String> idToUrlMap = new HashMap<Integer, String>();

    public static void main(String[] args) {

        buildIdToUrlMap();
        printMenu();
        Scanner sc = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            int eingabe = sc.nextInt();
            switch (eingabe) {
                case 1: {
                    try {
                        String ipAndPort = getUrlByIdFromUserInput(sc);
                        PrinterService printerService = new PrinterService(ipAndPort);
                        String response = printerService.requestPrinterStatus();
                        System.out.println(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2: {
                    String ipAndPort = getUrlByIdFromUserInput(sc);
                    break;
                }
                case 3: {
                    String ipAndPort = getUrlByIdFromUserInput(sc);
                    break;
                }

                case 4:
                    isRunning = false;
                    break;
            }

        }

    }

    private static String getUrlByIdFromUserInput(Scanner sc) {
        int id = getId(sc);
        return idToUrlMap.get(id);
    }

    private static void buildIdToUrlMap() {
        idToUrlMap.put(1, "http://127.0.0.1:1111");
        idToUrlMap.put(2, "http://127.0.0.1:2222");
    }

    private static void printMenu() {
        System.out.println("(1) Print");
        System.out.println("(2) Printer Status");
        System.out.println("(3) Material Status");
        System.out.println("(4) Shutdown");
    }

    private static int getId(Scanner sc) {
        String s = getIdsAsString();
        System.out.println(String.format("Printer available with id: (%s)", s));
        System.out.print("Choose a printer id: ");
        int id = sc.nextInt();
        return id;
    }

    private static String getIdsAsString() {
        List<Integer> ids = buildIdList();
        return joinIdsToCommaSeparatedString(ids);
    }

    private static String joinIdsToCommaSeparatedString(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ids.size(); i++){
            sb.append(ids.get(i));
            if (i < ids.size() - 1)
                sb.append(", ");
        }
        return sb.toString();
    }

    private static List<Integer> buildIdList() {
        List<Integer> ids = new ArrayList<Integer>();
        for (Map.Entry<Integer, String> entry : idToUrlMap.entrySet()){
            ids.add(entry.getKey());
        }
        return ids;
    }
}
