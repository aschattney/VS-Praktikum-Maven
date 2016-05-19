package de.hochschuledarmstadt.model.request;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrintJobRequest {

    public static final String PRINTJOB_REQUEST = "printjob_request";

    public PrintJobRequest(String jobName){
        this.jobName = jobName;
    }

    @SerializedName("type")
    @Expose
    private String type = PRINTJOB_REQUEST;

    @SerializedName("name")
    @Expose
    private String jobName;

    public String getJobName(){ return jobName; }
    public String getType() {
        return type;
    }
    public String toJSON(){
        return new Gson().toJson(this);
    }

}

