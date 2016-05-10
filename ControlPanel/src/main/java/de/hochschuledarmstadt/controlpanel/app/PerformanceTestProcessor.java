package de.hochschuledarmstadt.controlpanel.app;

import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.model.PrintJob;
import de.hochschuledarmstadt.model.Task;
import de.hochschuledarmstadt.model.request.ColorStatusRequest;
import de.hochschuledarmstadt.model.request.PrintHeadCommandRequest;
import de.hochschuledarmstadt.model.request.UseColorRequest;
import org.json.JSONObject;

import java.io.IOException;

public class PerformanceTestProcessor implements IApplicationProcessor {

    private static final String KEY_STATUS = "status";
    private static final String STATUS_OK = "ok";
    private static final String STATUS_BLOCKED = "blocked";

    private static final String NAIL_JSON_FILE = "nail.json";
    private static final int PERFORMANCETEST_CYCLES = 5;

    public static final String ERROR_MESSAGE_PERFORMANCE_TEST_FORMAT_STRING = "An error occured while executing performance test: %s \n Performance test will be aborted";
    public static final String MESSAGE_EXECUTING_PERFORMANCE_TEST = "Executing performance test ...";
    public static final String MESSAGE_PERFORMANCE_TEST_FINISHED = "Performance test finished!";

    private ISocketClient materialClient;
    private ISocketClient printerClient;

    public PerformanceTestProcessor(ISocketClient materialClient, ISocketClient printerClient){
        this.materialClient = materialClient;
        this.printerClient = printerClient;
    }

    @Override
    public void processSync() {
        System.out.println(MESSAGE_EXECUTING_PERFORMANCE_TEST);
        try {
            PrintJob printJob = PrintPlanJSONFileReader.readFile(NAIL_JSON_FILE);
            for (int counter = 0; counter < PERFORMANCETEST_CYCLES; counter++) {
                int[] amountJobs = new int[]{500, 1000, 2000, 5000, 10000, 20000, 40000, 80000};
                for (int amount : amountJobs) {
                    startPerformanceTest(printJob, amount);
                }
            }
        } catch (IOException e) {
            System.out.println(String.format(ERROR_MESSAGE_PERFORMANCE_TEST_FORMAT_STRING, e.toString()));
        }
        System.out.println(MESSAGE_PERFORMANCE_TEST_FINISHED);
    }

    private void startPerformanceTest(PrintJob printJob, int amount) throws IOException {
        long start = System.currentTimeMillis();
        for (int i = 0; i < amount; i++){
            sendPrintJob(printJob);
        }
        long end = System.currentTimeMillis();
        String timeAsString = String.valueOf((end - start) / 1000.0);
        System.out.println(String.valueOf(amount) + " Jobs: " + timeAsString + " seconds");
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
                        return false;
                    }
                }
            }
        }else{
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
        return useColorResponse;
    }

    private JSONObject sendMaterialStatusRequest(PrintJob printJob) throws IOException {
        ColorStatusRequest materialStatusRequest = new ColorStatusRequest(printJob.getRequiredMaterial());
        JSONObject materialStatusResponse = materialClient.sendMessage(materialStatusRequest.toJSON());
        return materialStatusResponse;
    }

}
