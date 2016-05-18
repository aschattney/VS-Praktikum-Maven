package de.hochschuledarmstadt.dashboard.app;

import java.nio.charset.Charset;

public class PerformanceTest implements Runnable {

    private final String ipAndPort;
    private final Callback callback;

    public PerformanceTest(String ipAndPort, Callback callback){
        this.ipAndPort = ipAndPort;
        this.callback = callback;
    }

    public void run() {

        long start = System.currentTimeMillis();
        long byteCount = 0;
        PrinterService service = new PrinterService(ipAndPort);
        for (int i = 0 ; i < 2000; i++){
            try {
                String response = service.requestPrinterStatus();
                byte[] responseBytes = response.getBytes(Charset.forName("UTF-8"));
                byteCount += responseBytes.length;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        long end = System.currentTimeMillis();

        long duration = end - start;
        callback.onPerformanceTestCompleted(duration, byteCount);
    }

    public interface Callback {
        void onPerformanceTestCompleted(double duration, long byteCount);
    }

}
