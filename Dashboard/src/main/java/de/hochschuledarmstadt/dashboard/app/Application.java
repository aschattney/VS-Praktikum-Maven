package de.hochschuledarmstadt.dashboard.app;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        System.out.println("(1) Print");
        System.out.println("(2) Printer Status");
        System.out.println("(3) Material Status");
        System.out.println("(4) Shutdown");

        Scanner sc = new Scanner(System.in);

        boolean isRunning = true;

        while (isRunning) {
            int eingabe = sc.nextInt();
            switch (eingabe) {
                case 1: {
                    //int id = getId(sc);
                    String response = PrinterService.getPrinterStatus();
                    System.out.println(response);
                    break;
                }
                case 2: {
                    int id = getId(sc);
                    break;
                }
                case 3: {
                    int id = getId(sc);
                    break;
                }

                case 4:
                    isRunning = false;
                    break;
            }

        }

    }

    private static int getId(Scanner sc) {
        System.out.println("Printer available with id: (1, 2, ..., n");
        System.out.print("Choose a printer id: ");
        int id = sc.nextInt();
        return id;
    }
}
