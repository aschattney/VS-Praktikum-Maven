package de.hochschuledarmstadt.dashboard.app;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.net.httpserver.HttpServer;
import com.sun.org.apache.xml.internal.serialize.Printer;

public class PrinterService extends HttpService {

    public PrinterService(String ipAndPort, int printerId){
        super(ipAndPort, printerId);
    }

    @Override
    protected ClientResponse performHttpRequest(WebResource.Builder webResource) {
        return webResource.get(ClientResponse.class);
    }

    @Override
    protected String getHttpResource(int printerId) {
        return String.format("printer/%s/status", printerId);
    }

    public String requestPrinterStatus() throws Exception {
        return execute();
    }

}

