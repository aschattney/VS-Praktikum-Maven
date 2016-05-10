package de.hochschuledarmstadt.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrintHeadCommandResponse {

    public static final String PRINTHEAD_COMMAND_RESPONSE = "printhead_command_response";

    public static final String STATUS_OK = "ok";
    public static final String STATUS_BLOCKED = "blocked";

    @SerializedName("type")
    @Expose
    private String type = PRINTHEAD_COMMAND_RESPONSE;
    @SerializedName("status")
    @Expose
    private String status;

    public PrintHeadCommandResponse(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }
    public String getStatus() {
        return status;
    }
    public String toJSON(){
        return new Gson().toJson(this);
    }

}
