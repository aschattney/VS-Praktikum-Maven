package de.hochschuledarmstadt.dashboard.app;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class PrinterService {

    public static String getPrinterStatus(){
        try {

            Client client = Client.create();

            WebResource webResource = client
                    .resource("http://localhost:1111/printer/status");

            ClientResponse response = webResource.accept("application/json")
                    .get(ClientResponse.class);

            if (response.getStatus() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            String output = response.getEntity(String.class);

            return output;

        } catch (Exception e) {

            e.printStackTrace();

        }
        return null;
    }

}

