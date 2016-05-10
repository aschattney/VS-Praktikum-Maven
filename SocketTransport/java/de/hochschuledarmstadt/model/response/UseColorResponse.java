package de.hochschuledarmstadt.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UseColorResponse {

    public static final String COLOR_DECREMENT_RESPONSE = "use_color_response";

    public static final String STATUS_OK = "ok";
    public static final String STATUS_FAILED = "failed";

    public UseColorResponse(String status){
        this.status = status;
    }

    @SerializedName("type")
    @Expose
    private String type = COLOR_DECREMENT_RESPONSE;

    @SerializedName("status")
    @Expose
    private String status = STATUS_OK;

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