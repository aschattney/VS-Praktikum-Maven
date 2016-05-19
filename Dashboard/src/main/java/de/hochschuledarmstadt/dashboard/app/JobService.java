package de.hochschuledarmstadt.dashboard.app;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class JobService extends HttpService{

    private final String jobName;

    public JobService(String ipAndPort, int printerId, String jobName) {
        super(ipAndPort, printerId);
        this.jobName = jobName;
    }

    @Override
    protected ClientResponse performHttpRequest(WebResource.Builder webResource) {
        return webResource.post(ClientResponse.class);
    }

    @Override
    protected String getHttpResource(int printerId) {
        return String.format("printer/%s/job/%s", printerId, jobName);
    }

    public String sendJob() throws Exception {
        return execute();
    }
}
