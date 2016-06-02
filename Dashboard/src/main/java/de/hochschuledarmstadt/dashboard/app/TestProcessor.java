package de.hochschuledarmstadt.dashboard.app;

import de.hochschuledarmstadt.config.Credential;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TestProcessor implements IApplicationProcessor, IOnTestRunFinishedCallback {

    private final Map<String, Credential> map;
    private final Map<Integer, String> localIdToUniqueIdMap;

    private static final int[] printerIds = new int[]{1,2,3};
    private static final List<Integer> packets = new LinkedList<Integer>();
    private static final Iterator<Integer> it;

    static {
        packets.add(5);
        packets.add(10);
        packets.add(20);
        packets.add(50);
        packets.add(100);
        packets.add(200);
        packets.add(500);
        it = packets.iterator();
    }

    private AtomicInteger testRunsFinished = new AtomicInteger(0);
    private AtomicInteger testRunsError = new AtomicInteger(0);

    private long end;
    private long start;
    private int currentPacket;
    private ExecutorService executorService;

    public TestProcessor(Map<String, Credential> map, Map<Integer, String> localIdToUniqueIdMap){
        this.map = map;
        this.localIdToUniqueIdMap = localIdToUniqueIdMap;
    }

    private PrinterService buildPrinterService(int printerId) {
        Identifier identifier = new Identifier(1, printerId);
        Credential c = map.get(localIdToUniqueIdMap.get(identifier.getFabricId()));
        String ipAndPort = buildUrl(c);
        return new PrinterService(ipAndPort, identifier.getPrinterId());
    }

    private String buildUrl(Credential c) {
        return "http://" + c.getIp() + ":" + c.getPort();
    }


    @Override
    public void start() {
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService = Executors.newFixedThreadPool(30);
        execute();
    }

    private void execute() {
        currentPacket = it.next();
        start = System.currentTimeMillis();
        testRunsFinished = new AtomicInteger(0);
        testRunsError = new AtomicInteger(0);
        for (int i = 0; i < currentPacket; i++) {
            for (int pos = 0; pos < printerIds.length; pos++) {
                int printerId = printerIds[pos];
                final PrinterService service = buildPrinterService(printerId);
                executorService.submit(new TestRun(service, this));
            }
        }
    }

    @Override
    public synchronized void onTestRunFinished() {
        testRunsFinished.incrementAndGet();
        onTestRunEnded();
    }

    private void onTestRunEnded() {
        int value = testRunsFinished.get();
        int errors  = testRunsError.get();
        value += errors;
        if (value == currentPacket * printerIds.length) {
            end = System.currentTimeMillis();
            System.out.println(String.format("%s requests per printer finished in %s seconds with %s errors",
                    currentPacket, ((end - start) / 1000), errors));
            if (it.hasNext()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        execute();
                    }
                }).start();
            }else{
                System.exit(0);
            }
        }
    }

    @Override
    public synchronized void onTestRunError() {
        testRunsError.incrementAndGet();
        onTestRunEnded();
    }

    private static class TestRun implements Runnable {

        private final PrinterService service;
        private final IOnTestRunFinishedCallback callback;

        public TestRun(PrinterService service, IOnTestRunFinishedCallback callback){
            this.service = service;
            this.callback = callback;
        }

        @Override
        public void run() {
            try {
                String response = service.requestPrinterStatus();
                //System.out.println(response);
                callback.onTestRunFinished();
            } catch (Exception e) {
                //e.printStackTrace();
                callback.onTestRunError();
            }
        }

    }


}
