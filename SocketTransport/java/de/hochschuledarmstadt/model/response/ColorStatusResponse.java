package de.hochschuledarmstadt.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.hochschuledarmstadt.model.RequiredMaterial;

public class ColorStatusResponse {

    public static final String COLOR_STATUS_RESPONSE = "color_status_response";

    public static final String STATUS_OK = "ok";
    public static final String STATUS_MISSING = "missing";

    public ColorStatusResponse(String status, RequiredMaterial material){
        this.status = status;
        this.material = material;
    }

    @SerializedName("type")
    @Expose
    private String type = COLOR_STATUS_RESPONSE;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("material")
    @Expose
    private RequiredMaterial material;

    public RequiredMaterial getMaterial(){
        return material;
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