package de.hochschuledarmstadt.material.app;

import java.util.Scanner;
import java.util.concurrent.Executors;

public class Application {

    private static final int DEFAULT_FILL_LEVEL = 5000;
    private static final int PERFORMANCE_TEST_FILL_LEVEL = 1000000000;

    private static final String ARG_PERFORMANCETEST = "PERFORMANCETEST";
    private static final String FILL_LEVEL_ARG_KEY = "FILL-LEVEL";

    public static void main(String[] args){
        boolean performanceTestEnabled = shouldPerformPerformanceTest(args);
        int fillLevelForEachColor = performanceTestEnabled ? PERFORMANCE_TEST_FILL_LEVEL : parseFillLevel(args, DEFAULT_FILL_LEVEL);
        Material material = new Material(fillLevelForEachColor);
        MessageConsumer materialConsumer = new MessageConsumer(material);
        MaterialServerExecutor serverExecutor = new MaterialServerExecutor(args, materialConsumer);
        Executors.newSingleThreadExecutor().submit(serverExecutor);

        Scanner scanner = new Scanner(System.in);

        while(true){
            printMenu();
            int procedureId = scanner.nextInt();
            switch(procedureId){
                case 1:
                    materialConsumer.refillMaterial();
                    break;
                case 2:
                    materialConsumer.empty();
                    break;
            }
        }

    }

    private static int parseFillLevel(String[] args, int defaultFillLevel) {
        for (String arg : args){
            String[] splitted = arg.split("=");
            if (splitted[0].toUpperCase().equals(FILL_LEVEL_ARG_KEY)){
                return Integer.parseInt(splitted[1]);
            }
        }
        return defaultFillLevel;
    }

    private static void printMenu() {
        System.out.println("(1) Refill Material");
        System.out.println("(2) Empty Material");
    }

    private static boolean shouldPerformPerformanceTest(String[] args) {
        for (String arg : args){
            if (arg.toUpperCase().equals(ARG_PERFORMANCETEST))
                return true;
        }
        return false;
    }

}
