package de.hochschuledarmstadt.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Task {

    public Task() {}

    @SerializedName("x")
    @Expose
    private Integer x;
    @SerializedName("y")
    @Expose
    private Integer y;
    @SerializedName("z")
    @Expose
    private Integer z;
    @SerializedName("color")
    @Expose
    private String color;

    public Integer getX() {
        return x;
    }
    public Integer getY() {
        return y;
    }
    public Integer getZ() {
        return z;
    }
    public String getColor() {
        return color;
    }

}
