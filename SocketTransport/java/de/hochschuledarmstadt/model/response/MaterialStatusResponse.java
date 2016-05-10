package de.hochschuledarmstadt.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.hochschuledarmstadt.model.RequiredMaterial;

public class MaterialStatusResponse {

    public static final String MATERIAL_STATUS_RESPONSE = "material_status_response";

    @SerializedName("type")
    @Expose
    private String type = MATERIAL_STATUS_RESPONSE;

    @SerializedName("material")
    @Expose
    private RequiredMaterial requiredMaterial;

    public MaterialStatusResponse(RequiredMaterial requiredMaterial){
        this.requiredMaterial = requiredMaterial;
    }

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