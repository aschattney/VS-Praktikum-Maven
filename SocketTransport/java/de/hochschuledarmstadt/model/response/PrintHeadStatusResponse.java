package de.hochschuledarmstadt.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.hochschuledarmstadt.model.RequiredMaterial;

public class PrintHeadStatusResponse {

    public static final String PRINTHEAD_STATUS_RESPONSE = "printhead_status_response";

    public static final String STATUS_OK = "ok";
    public static final String STATUS_BLOCKED = "blocked";

    public PrintHeadStatusResponse(String status){
        this.status = status;
    }

    @SerializedName("type")
    @Expose
    private String type = PRINTHEAD_STATUS_RESPONSE;

    @SerializedName("status")
    @Expose
    private String status;

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