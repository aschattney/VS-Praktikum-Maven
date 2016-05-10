package de.hochschuledarmstadt.model.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrintheadStatusRequest {

    public static final String PRINTHEAD_STATUS_REQUEST = "printhead_status_request";

    public PrintheadStatusRequest(){

    }

    @SerializedName("type")
    @Expose
    private String type = PRINTHEAD_STATUS_REQUEST;

    public String getType() {
        return type;
    }
    public String toJSON(){
        return new Gson().toJson(this);
    }

}
