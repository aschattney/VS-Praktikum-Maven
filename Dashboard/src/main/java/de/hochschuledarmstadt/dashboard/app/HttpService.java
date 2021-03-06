package de.hochschuledarmstadt.dashboard.app;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class HttpService {

    public static final int TIMEOUT = 8000;
    private String ipAndPort;
    private int printerId;
    private Client client;

    public HttpService(String ipAndPort, int printerId){
        this.ipAndPort = ipAndPort;
        this.printerId = printerId;
        client = Client.create();
        client.setConnectTimeout(TIMEOUT);
        client.setReadTimeout(TIMEOUT);
    }

    protected abstract ClientResponse performHttpRequest(WebResource.Builder webResource);
    protected abstract String getHttpResource(int printerId);

    protected String execute() throws Exception{

        String url = String.format("%s/%s", ipAndPort, getHttpResource(printerId));
        WebResource webResource = client.resource(url);
        ClientResponse response = performHttpRequest(webResource.accept("application/json"));

        if (response.getStatus() >= 400) {
            throw new Exception("Failed : HTTP error code : " + response.getStatus());
        }

        String output = response.getEntity(String.class);
        return output;

    }

}
