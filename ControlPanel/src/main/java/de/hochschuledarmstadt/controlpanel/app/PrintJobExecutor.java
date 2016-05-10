package de.hochschuledarmstadt.controlpanel.app;

import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.model.PrintJob;
import de.hochschuledarmstadt.model.Task;
import de.hochschuledarmstadt.model.request.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrintJobExecutor implements Runnable{

    private static final String KEY_STATUS = "status";
    private static final String STATUS_OK = "ok";
    private static final String STATUS_BLOCKED = "blocked";

    private static final String MESSAGE_PRINTJOB_SUCCEEDED = "Druckvorgang erfolgreich beendet";
    private static final String MESSAGE_PRINTHEAD_BLOCKED = "Druckkopf verstopft, Druckvorgang wurde abgebrochen!";
    private static final String MESSAGE_NOT_ENOUGH_COLOR_REMAINING = "Nicht genügend Farbe vorhanden, Druckvorgang wurde abgebrochen!";

    private PrintStream outputStream;
    private ISocketClient printerClient;
    private ISocketClient materialClient;

    private AtomicBoolean mIsPrinting = new AtomicBoolean(false);

    private BlockingQueue<PrintJob> printJobs = new ArrayBlockingQueue<PrintJob>(20000);

    public PrintJobExecutor(OutputStream os, ISocketClient printerClient, ISocketClient materialClient){
        this.outputStream = new PrintStream(os);
        this.printerClient = printerClient;
        this.materialClient = materialClient;
    }

    public boolean addPrintJob(PrintJob printJob){
        boolean success = false;
        try {
            printJobs.put(printJob);
            success = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return success;
    }

    public boolean isPrinting(){
        return mIsPrinting.get();
    }

    @Override
    public void run() {
        while(true){
            PrintJob job;
            while ((job = printJobs.poll()) != null) {
                try {
                    mIsPrinting.set(true);
                    if (sendPrintJob(job))
                        outputStream.println(MESSAGE_PRINTJOB_SUCCEEDED);
                } catch(SocketTimeoutException e){
                    outputStream.println("Socket Timeout Exception! Druckvorgang wurde abgebrochen!");
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    mIsPrinting.set(false);
                }
            }
        }
    }

    private boolean sendPrintJob(PrintJob printJob) throws IOException {
        JSONObject materialStatusResponse = sendMaterialStatusRequest(printJob);
        if (hasMaterialEnoughColorRemaining(materialStatusResponse)) {
            for (int position = 0; position < printJob.sizeOfTasks(); position++) {
                Task task = printJob.getTaskAtPosition(position);
                JSONObject useColorResponse = sendUseColorRequest(task);
                if (hasPrinterPreparedNewColor(useColorResponse)) {
                    JSONObject printHeadCommandResponse = sendPrintHeadCommandRequest(printerClient, task);
                    if (isPrintHeadBlocked(printHeadCommandResponse)){
                        outputStream.println(MESSAGE_PRINTHEAD_BLOCKED);
                        return false;
                    }
                }
            }
        }else{
            outputStream.println(MESSAGE_NOT_ENOUGH_COLOR_REMAINING);
            return false;
        }
        return true;
    }

    private boolean isPrintHeadBlocked(JSONObject response) {
        return response.getString(KEY_STATUS).equals(STATUS_BLOCKED);
    }

    private JSONObject sendPrintHeadCommandRequest(ISocketClient printerClient, Task task) throws IOException {
        PrintHeadCommandRequest command = new PrintHeadCommandRequest(task);
        JSONObject response = printerClient.sendMessage(command.toJSON());
        outputStream.println(response.toString());
        return response;
    }

    private boolean hasMaterialEnoughColorRemaining(JSONObject materialStatusResponse) {
        return materialStatusResponse.getString(KEY_STATUS).equals(STATUS_OK);
    }

    private boolean hasPrinterPreparedNewColor(JSONObject useColorResponse) {
        return useColorResponse.getString(KEY_STATUS).equals(STATUS_OK);
    }

    private JSONObject sendUseColorRequest(Task task) throws IOException {
        UseColorRequest useColorRequest = new UseColorRequest(task.getColor());
        JSONObject useColorResponse = materialClient.sendMessage(useColorRequest.toJSON());
        outputStream.println(useColorResponse.toString());
        return useColorResponse;
    }

    private JSONObject sendMaterialStatusRequest(PrintJob printJob) throws IOException {
        ColorStatusRequest materialStatusRequest = new ColorStatusRequest(printJob.getRequiredMaterial());
        JSONObject materialStatusResponse = materialClient.sendMessage(materialStatusRequest.toJSON());
        outputStream.println(materialStatusResponse);
        return materialStatusResponse;
    }

}
