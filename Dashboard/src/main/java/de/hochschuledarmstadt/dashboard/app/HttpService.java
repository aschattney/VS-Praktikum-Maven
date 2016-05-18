package de.hochschuledarmstadt.dashboard.app;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.xml.ws.http.HTTPException;

public abstract class HttpService {

    private final String url;

    public HttpService(String ip, int port){
        this.url = String.format("%s:%s/%s", ip, port, getHttpResource());
    }

    public HttpService(String ipAndPort){
        this.url = String.format("%s/%s", ipAndPort, getHttpResource());
    }

    protected abstract ClientResponse performHttpRequest(WebResource.Builder webResource);
    protected abstract String getHttpResource();

    protected String execute() throws Exception{

        Client client = Client.create();

        WebResource webResource = client.resource(url);

        ClientResponse response = performHttpRequest(webResource.accept("application/json"));

        if (response.getStatus() >= 400) {
            throw new Exception("Failed : HTTP error code : " + response.getStatus());
        }

        String output = response.getEntity(String.class);
        return output;

    }

}
