package de.hochschuledarmstadt.controlpanel.app;


import com.google.gson.Gson;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.model.request.MaterialStatusRequest;
import de.hochschuledarmstadt.model.request.PrintheadStatusRequest;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONObject;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.IOException;

@Path("material")
public class MaterialResource {

    private ISocketClient material;

    public MaterialResource(ISocketClient material) {
        this.material = material;
    }

    @GET
    @Path("/status")
    @Produces("text/plain")
    public String Status(){
        Gson gson = new Gson();
        MaterialStatusRequest request = new MaterialStatusRequest();
        String message = gson.toJson(request);
        JSONObject jsonObject = null;
        try {
            jsonObject = material.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


}
