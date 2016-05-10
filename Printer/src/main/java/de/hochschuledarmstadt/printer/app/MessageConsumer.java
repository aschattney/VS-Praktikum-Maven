package de.hochschuledarmstadt.printer.app;

import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IMessageSender;
import de.hochschuledarmstadt.model.request.PrintHeadCommandRequest;
import de.hochschuledarmstadt.model.request.PrintheadStatusRequest;
import de.hochschuledarmstadt.model.response.*;
import org.json.JSONObject;

import java.io.IOException;

public class MessageConsumer implements IMessageConsumer{

    private static final String KEY_TYPE = "type";
    private static final int PRINTHEAD_COMMAND_SIMULATE_TIMEOUT = 2000;

    public static final String PRINTHEAD_STATUS_OK = PrintHeadCommandResponse.STATUS_OK;
    public static final String PRINTHEAD_STATUS_BLOCKED = PrintHeadCommandResponse.STATUS_BLOCKED;

    private String printHeadStatus = PrintHeadCommandResponse.STATUS_OK;

    @Override
    public void consumeMessage(IMessageSender messageSender, JSONObject jsonObject) {
        if (isPrintHeadCommandRequest(jsonObject)) {
            simulateCommand();
            synchronized (printHeadStatus){
                PrintHeadCommandResponse response = new PrintHeadCommandResponse(printHeadStatus);
                sendMessage(messageSender, response.toJSON());
            }
        }else if(isPrintHeadStatusRequest(jsonObject)){
            synchronized (printHeadStatus){
                PrintHeadStatusResponse response = new PrintHeadStatusResponse(printHeadStatus);
                sendMessage(messageSender, response.toJSON());
            }
        }
    }

    private boolean isPrintHeadStatusRequest(JSONObject jsonObject) {
        return jsonObject.getString(KEY_TYPE).equals(PrintheadStatusRequest.PRINTHEAD_STATUS_REQUEST);
    }

    private boolean isPrintHeadCommandRequest(JSONObject jsonObject) {
        return jsonObject.getString(KEY_TYPE).equals(PrintHeadCommandRequest.PRINTHEAD_COMMAND_REQUEST);
    }

    private void onError(IOException e) {
        e.printStackTrace();
    }

    private void simulateCommand() {
        /*try {
            Thread.sleep(PRINTHEAD_COMMAND_SIMULATE_TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    private void sendMessage(IMessageSender messageSender, String message){
        try {
            messageSender.sendMessage(message);
        } catch (IOException e) {
            onError(e);
        }
    }


    public void setPrintHeadStatus(String status) {
        synchronized (printHeadStatus) {
            this.printHeadStatus = status;
        }
    }
}
