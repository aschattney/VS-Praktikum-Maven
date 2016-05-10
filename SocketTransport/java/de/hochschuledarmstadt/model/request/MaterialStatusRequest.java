package de.hochschuledarmstadt.model.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Nadja on 03.05.2016.
 */
public class MaterialStatusRequest {

    public static final String MATERIAL_STATUS_REQUEST = "material_status_request";

    public MaterialStatusRequest(){

    }

    @SerializedName("type")
    @Expose
    private String type = MATERIAL_STATUS_REQUEST;

    public String getType() {
        return type;
    }
    public String toJSON(){
        return new Gson().toJson(this);
    }
}
