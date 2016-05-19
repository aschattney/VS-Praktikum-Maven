package de.hochschuledarmstadt.dashboard.app;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class FabricService extends HttpService{

    public FabricService(String ipAndPort, int printerId) {
        super(ipAndPort, printerId);
    }

    @Override
    protected ClientResponse performHttpRequest(WebResource.Builder webResource) {
        webResource.accept("text/plain");
        return webResource.get(ClientResponse.class);
    }

    @Override
    protected String getHttpResource(int printerId) {
        return "fabric/status";
    }
}
