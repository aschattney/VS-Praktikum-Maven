package de.hochschuledarmstadt.dashboard.app;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JobService extends HttpService{

    private final String jobName;

    public JobService(String ip, int port, String jobName) {
        super(ip, port);
        this.jobName = jobName;
    }

    public JobService(String ipAndPort, String jobName) {
        super(ipAndPort);
        this.jobName = jobName;
    }

    @Override
    protected ClientResponse performHttpRequest(WebResource.Builder webResource) {
        return webResource.post(ClientResponse.class);
    }

    @Override
    protected String getHttpResource() {
        return "printer/job";
    }

    public String sendJob() throws Exception {
        return execute();
    }
}
