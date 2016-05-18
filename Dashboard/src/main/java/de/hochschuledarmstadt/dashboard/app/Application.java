package de.hochschuledarmstadt.dashboard.app;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Application {

    private static Map<Integer, String> idToUrlMap = new HashMap<Integer, String>();

    public static void main(String[] args) {

        buildIdToUrlMap();
        printMenu();
        Scanner sc = new Scanner(System.in);
        boolean isRunning = true;

        //performanceTest();

        while (isRunning) {
            int eingabe = sc.nextInt();
            switch (eingabe) {
                case 1: {
                    try {
                        String ipAndPort = getUrlByIdFromUserInput(sc);
                        JobService jobService = new JobService(ipAndPort, "nail");
                        String response = jobService.sendJob();
                        System.out.println(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2: {
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
                case 3: {
                    try {
                        String ipAndPort = getUrlByIdFromUserInput(sc);
                        MaterialService materialService = new MaterialService(ipAndPort);
                        String response = materialService.requestMaterialStatus();
                        System.out.println(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case 4:
                    isRunning = false;
                    break;
            }

        }

    }

    private static void performanceTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int id = 1 ; id <= 2; id++){
            executorService.submit(new PerformanceTest(idToUrlMap.get(id), new PerformanceTest.Callback() {

                public void onPerformanceTestCompleted(double duration, long byteCount) {
                    System.out.println(String.format("Dauer: %s, Anzahl: %s", duration, byteCount));
                }

            }));
        }
    }

    private static String getUrlByIdFromUserInput(Scanner sc) {
        int id = getId(sc);
        return idToUrlMap.get(id);
    }

    private static void buildIdToUrlMap() {
        idToUrlMap.put(1, "http://127.0.0.1:1111");
        idToUrlMap.put(2, "http://127.0.0.1:15000");
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
        for (int i = 0; i < ids.size(); i++) {
            sb.append(ids.get(i));
            if (i < ids.size() - 1)
                sb.append(", ");
        }
        return sb.toString();
    }

    private static List<Integer> buildIdList() {
        List<Integer> ids = new ArrayList<Integer>();
        for (Map.Entry<Integer, String> entry : idToUrlMap.entrySet()) {
            ids.add(entry.getKey());
        }
        return ids;
    }
}
