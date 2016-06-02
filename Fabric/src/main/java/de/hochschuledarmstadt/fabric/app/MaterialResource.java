package de.hochschuledarmstadt.fabric.app;


import com.google.gson.Gson;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.model.request.MaterialStatusRequest;
import de.hochschuledarmstadt.model.request.PrintheadStatusRequest;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Map;

@Path("material")
public class MaterialResource {

    Gson gson = new Gson();

    private Map<Integer,ISocketClient> panelSockets;

    public MaterialResource(Map<Integer,ISocketClient> panelSockets) {
        this.panelSockets = panelSockets;
    }

    @GET
    @Path("{id}/status")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String Status(@PathParam("id") int id){
        MaterialStatusRequest request = new MaterialStatusRequest();
        String message = gson.toJson(request);
        JSONObject jsonObject = null;
        try {
            jsonObject = panelSockets.get(id).sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


}
