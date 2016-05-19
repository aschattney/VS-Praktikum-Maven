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
                        int[] ids = getId(sc);
                        String ipAndPort = idToUrlMap.get(ids[0]);
                        JobService jobService = new JobService(ipAndPort, ids[1], "nail");
                        String response = jobService.sendJob();
                        System.out.println(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2: {
                    try {
                        int[] ids = getId(sc);
                        String ipAndPort = idToUrlMap.get(ids[0]);
                        PrinterService printerService = new PrinterService(ipAndPort, ids[1]);
                        String response = printerService.requestPrinterStatus();
                        System.out.println(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3: {
                    try {
                        int[] ids = getId(sc);
                        String ipAndPort = idToUrlMap.get(ids[0]);
                        MaterialService materialService = new MaterialService(ipAndPort, ids[1]);
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
/*        ExecutorService executorService = Executors.newFixedThreadPool(2);
        for (int id = 1 ; id <= 2; id++){
            String ipAndPort = idToUrlMap.get(id);
            executorService.submit(new PerformanceTest(ipAndPort, new PerformanceTest.Callback() {

                public void onPerformanceTestCompleted(double duration, long byteCount) {
                    System.out.println(String.format("Dauer: %s, Anzahl: %s", duration, byteCount));
                }

            }));
        }
        */
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

    private static int[] getId(Scanner sc) {
        String s = getIdsAsString();
        System.out.println(String.format("Fabrics available with id: (%s)", s));
        System.out.print("Choose a fabric id: ");
        int fabricId = sc.nextInt();
        String ipAndPort = idToUrlMap.get(fabricId);
        FabricService service = new FabricService(ipAndPort, fabricId);
        try {
            String response = service.execute();
            System.out.println(String.format("Printer available in fabric %s with id: (%s)", fabricId, response));
            System.out.print("Choose a printer id: ");
            int id = sc.nextInt();
            return new int[]{fabricId, id};
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
