package de.hochschuledarmstadt.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequiredMaterial {

    public RequiredMaterial(int red, int blue, int yellow){
        this.red = red;
        this.blue = blue;
        this.yellow = yellow;
    }

    @SerializedName("red")
    @Expose
    private Integer red;
    @SerializedName("blue")
    @Expose
    private Integer blue;
    @SerializedName("yellow")
    @Expose
    private Integer yellow;

    public Integer getRed() {
        return red;
    }
    public Integer getBlue() {
        return blue;
    }
    public Integer getYellow() {
        return yellow;
    }


}
