package de.hochschuledarmstadt.dashboard.app;


import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class MaterialService extends HttpService {


    public MaterialService(String ip, int port) {
        super(ip, port);
    }

    public MaterialService(String ipAndPort) {
        super(ipAndPort);
    }

    @Override
    protected ClientResponse performHttpRequest(WebResource.Builder webResource) {
        return webResource.get(ClientResponse.class);
    }

    @Override
    protected String getHttpResource() {
        return "material/status";
    }

    public String requestMaterialStatus() throws Exception {
        return execute();
    }
}
