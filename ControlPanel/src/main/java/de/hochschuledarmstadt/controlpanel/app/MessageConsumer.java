package de.hochschuledarmstadt.controlpanel.app;

import com.google.gson.Gson;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IMessageSender;
import de.hochschuledarmstadt.model.PrintJob;
import de.hochschuledarmstadt.model.request.ColorStatusRequest;
import de.hochschuledarmstadt.model.request.MaterialStatusRequest;
import de.hochschuledarmstadt.model.request.PrintJobRequest;
import de.hochschuledarmstadt.model.request.PrintheadStatusRequest;
import org.json.JSONObject;

import java.io.IOException;

public class MessageConsumer implements IMessageConsumer {

    private static final String KEY_TYPE = "type";

    private final ISocketClient printerClient;
    private final ISocketClient materialClient;
    private final PrintJobExecutor jobExecutor;

    public MessageConsumer(ISocketClient printerClient, ISocketClient materialClient, PrintJobExecutor jobExecutor){
        this.printerClient = printerClient;
        this.materialClient = materialClient;
        this.jobExecutor = jobExecutor;
    }

    @Override
    public void consumeMessage(IMessageSender messageSender, JSONObject message) {
        if (isPrintHeadStatusRequest(message)){
            JSONObject response = sendMessage(printerClient, message);
            sendMessage(messageSender, response.toString());
        }else if(isMaterialStatusRequest(message)){
            JSONObject response = sendMessage(materialClient, message);
            sendMessage(messageSender, response.toString());
        }else if(isPrintJobRequest(message)){
            try {
                PrintJobRequest printJobRequest = new Gson().fromJson(message.toString(), PrintJobRequest.class);
                String jobName = printJobRequest.getJobName();
                PrintJob printJob = PrintPlanJSONFileReader.readFile(jobName + ".json");
                String response = (jobExecutor.isPrinting()) ? "Druckauftrag in Warteschlange eingereiht" : "Druckauftrag gestartet";
                jobExecutor.addPrintJob(printJob);
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("message", response);
                sendMessage(messageSender, jsonResponse.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPrintJobRequest(JSONObject message) {
        return message.getString(KEY_TYPE).equals(PrintJobRequest.PRINTJOB_REQUEST);
    }

    private void onError(IOException e) {
        e.printStackTrace();
    }

    private void sendMessage(IMessageSender messageSender, String message){
        try {
            messageSender.sendMessage(message);
        } catch (IOException e) {
            onError(e);
        }
    }

    private boolean isMaterialStatusRequest(JSONObject message) {
        return message.getString(KEY_TYPE).equals(MaterialStatusRequest.MATERIAL_STATUS_REQUEST);
    }

    private JSONObject sendMessage(ISocketClient socketClient, JSONObject message){
        try {
            return socketClient.sendMessage(message.toString());
        } catch (IOException e) {
            onError(e);
        }
        return null;
    }

    private boolean isPrintHeadStatusRequest(JSONObject jsonObject) {
        return jsonObject.getString(KEY_TYPE).equals(PrintheadStatusRequest.PRINTHEAD_STATUS_REQUEST);
    }

}
