package de.hochschuledarmstadt.model.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.hochschuledarmstadt.model.Task;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class PrintHeadCommandRequest {

    public static final String PRINTHEAD_COMMAND_REQUEST = "printhead_command_request";

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("task")
    @Expose
    private Task task;

    public PrintHeadCommandRequest(Task task) {
        this.task = task;
        this.type = PRINTHEAD_COMMAND_REQUEST;
    }

    public String getType() {
        return type;
    }
    public Task getTask() {
        return task;
    }
    public String toJSON() {
        return new Gson().toJson(this);
    }
}
