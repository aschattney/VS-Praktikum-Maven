package de.hochschuledarmstadt.material.app;

import com.google.gson.Gson;
import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IMessageSender;
import de.hochschuledarmstadt.model.RequiredMaterial;
import de.hochschuledarmstadt.model.request.*;
import de.hochschuledarmstadt.model.response.*;
import org.json.JSONObject;

import java.io.IOException;

public class MessageConsumer implements IMessageConsumer {

    private static final String KEY_TYPE = "type";

    private Material material = new Material(100000);
    private Gson gson = new Gson();

    private void onError(IOException e) {
        e.printStackTrace();
    }


    @Override
    public void consumeMessage(IMessageSender messageSender, JSONObject message) {

        if (isColorStatusRequest(message)){

            ColorStatusRequest statusRequest = gson.fromJson(message.toString(), ColorStatusRequest.class);
            final RequiredMaterial requiredMaterial = statusRequest.getRequiredMaterial();
            boolean colorAvailable = material.isEnoughColorAvailable(requiredMaterial);
            String status = colorAvailable ? ColorStatusResponse.STATUS_OK : ColorStatusResponse.STATUS_MISSING;
            ColorStatusResponse response = new ColorStatusResponse(status, material.getColorFillLevel());
            sendMessage(messageSender, response.toJSON());

        }else if(isUseColorRequest(message)){

            UseColorRequest useColorRequest = gson.fromJson(message.toString(), UseColorRequest.class);
            final String color = useColorRequest.getColor();
            material.use(color);
            UseColorResponse response = new UseColorResponse(UseColorResponse.STATUS_OK);
            sendMessage(messageSender, response.toJSON());

        }else if(isMaterialStatusRequest(message)){

            RequiredMaterial currentFillLevel = material.getColorFillLevel();
            MaterialStatusResponse materialStatusResponse = new MaterialStatusResponse(currentFillLevel);
            sendMessage(messageSender, materialStatusResponse.toJSON());
        }

    }

    private boolean isMaterialStatusRequest(JSONObject message) {
        return message.getString(KEY_TYPE).equals(MaterialStatusRequest.MATERIAL_STATUS_REQUEST);
    }

    private boolean isUseColorRequest(JSONObject message) {
        return message.getString(KEY_TYPE).equals(UseColorRequest.COLOR_DECREMENT_REQUEST);
    }

    private boolean isColorStatusRequest(JSONObject message) {
        return message.getString(KEY_TYPE).equals(ColorStatusRequest.COLOR_STATUS_REQUEST);
    }

    private void sendMessage(IMessageSender messageSender, String message){
        try {
            messageSender.sendMessage(message);
        } catch (IOException e) {
            onError(e);
        }
    }


    public void refillMaterial() {
        material.refill();
    }
}
