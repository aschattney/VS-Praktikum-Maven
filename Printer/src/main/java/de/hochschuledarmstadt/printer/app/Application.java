package de.hochschuledarmstadt.printer.app;

import java.util.Scanner;
import java.util.concurrent.Executors;

public class Application {

    private static final String ARG_PERFORMANCETEST = "PERFORMANCETEST";

    public static void main(String[] args){

        boolean performanceTestEnabled = shouldPerformPerformanceTest(args);

        MessageConsumer messageConsumer = new MessageConsumer();
        PrinterServerExecutor executor = new PrinterServerExecutor(args, messageConsumer);
        Executors.newSingleThreadExecutor().submit(executor);

        Scanner scanner = new Scanner(System.in);

        while(true){
            printMenu();
            int procedureId = scanner.nextInt();
            switch(procedureId){
                case 1:
                    messageConsumer.setPrintHeadStatus(MessageConsumer.PRINTHEAD_STATUS_BLOCKED);
                    break;
                case 2:
                    messageConsumer.setPrintHeadStatus(MessageConsumer.PRINTHEAD_STATUS_OK);
                    break;
            }
        }

    }

    private static void printMenu(){
        System.out.println("(1) Block printhead");
        System.out.println("(2) Unblock printhead");
    }

    private static boolean shouldPerformPerformanceTest(String[] args) {
        for (String arg : args){
            if (arg.toUpperCase().equals(ARG_PERFORMANCETEST))
                return true;
        }
        return false;
    }

}
