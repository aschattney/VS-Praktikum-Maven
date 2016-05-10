package de.hochschuledarmstadt.model.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UseColorRequest {

    public static final String COLOR_DECREMENT_REQUEST = "use_color_request";

    public static final String COLOR_RED = "red";
    public static final String COLOR_YELLOW = "yellow";
    public static final String COLOR_BLUE = "blue";

    public UseColorRequest(String color){
        this.color = color;
    }

    @SerializedName("type")
    @Expose
    private String type = COLOR_DECREMENT_REQUEST;

    @SerializedName("color")
    @Expose
    private String color;

    public String getType() {
        return type;
    }
    public String getColor(){
        return color;
    }
    public String toJSON(){
        return new Gson().toJson(this);
    }

}
