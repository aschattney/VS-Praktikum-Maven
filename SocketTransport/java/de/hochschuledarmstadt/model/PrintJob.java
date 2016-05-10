package de.hochschuledarmstadt.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PrintJob {

    public PrintJob() {}

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("required_material")
    @Expose
    private RequiredMaterial requiredMaterial;
    @SerializedName("tasks")
    @Expose
    private List<Task> tasks = new ArrayList<Task>();

    public String getType() {
        return type;
    }
    public RequiredMaterial getRequiredMaterial() {
        return requiredMaterial;
    }
    public Task getTaskAtPosition(int position){
        return tasks.get(position);
    }
    public int sizeOfTasks(){
        return tasks.size();
    }

}

