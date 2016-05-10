package de.hochschuledarmstadt.model.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.hochschuledarmstadt.model.RequiredMaterial;

public class ColorStatusRequest {

    public static final String COLOR_STATUS_REQUEST = "color_status_request";

    public ColorStatusRequest(RequiredMaterial requiredMaterial){
        this.requiredMaterial = requiredMaterial;
    }

    public ColorStatusRequest(){
        this(new RequiredMaterial(0, 0, 0));
    }

    @SerializedName("type")
    @Expose
    private String type = COLOR_STATUS_REQUEST;
    @SerializedName("required_material")
    @Expose
    private RequiredMaterial requiredMaterial;


    public String getType() {
        return type;
    }
    public RequiredMaterial getRequiredMaterial() {
        return requiredMaterial;
    }
    public String toJSON(){
        return new Gson().toJson(this);
    }

}
