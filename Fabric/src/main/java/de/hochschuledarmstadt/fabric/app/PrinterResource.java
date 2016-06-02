package de.hochschuledarmstadt.fabric.app;


import com.google.gson.Gson;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.model.PrintJob;
import de.hochschuledarmstadt.model.request.PrintJobRequest;
import de.hochschuledarmstadt.model.request.PrintheadStatusRequest;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Path("printer")
public class PrinterResource {

    private Map<Integer, ISocketClient> panelSockets = new HashMap<Integer, ISocketClient>();

    public PrinterResource(Map<Integer, ISocketClient> panelSockets) {
        this.panelSockets = panelSockets;
    }

    private static final Object LOCK_STATUS = new Object();

    private Gson gson = new Gson();

    @GET
    @Path("{id}/status")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String Status(@PathParam("id") int id) throws Exception {
        try {
            PrintheadStatusRequest request = new PrintheadStatusRequest();
            String message = gson.toJson(request);
            ISocketClient client = panelSockets.get(id);
            synchronized (LOCK_STATUS) {
                JSONObject jsonObject = client.sendMessage(message);
                return jsonObject.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @POST
    @Path("{id}/job/{name}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String Job(@PathParam("id") int id, @PathParam("name") String name) throws IOException {
        try {
            Gson gson = new Gson();
            PrintJobRequest printJobRequest = new PrintJobRequest(name);
            String message = gson.toJson(printJobRequest);
            ISocketClient client = panelSockets.get(id);
            JSONObject jsonObject = client.sendMessage(message);
            return jsonObject.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }


}
