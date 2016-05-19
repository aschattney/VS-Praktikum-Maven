package de.hochschuledarmstadt.dashboard.app;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class MaterialService extends HttpService {

    public MaterialService(String ipAndPort, int printerId) {
        super(ipAndPort, printerId);
    }

    @Override
    protected ClientResponse performHttpRequest(WebResource.Builder webResource) {
        return webResource.get(ClientResponse.class);
    }

    @Override
    protected String getHttpResource(int printerId) {
        return String.format("material/%s/status", printerId);
    }

    public String requestMaterialStatus() throws Exception {
        return execute();
    }
}
