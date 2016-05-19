package de.hochschuledarmstadt.fabric.app;

import com.google.gson.Gson;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.model.request.MaterialStatusRequest;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Path("fabric")
public class FabricResource {

    private final Set<Integer> ids;

    public FabricResource(Set<Integer> ids) {
        this.ids = ids;
    }

    @GET
    @Path("status")
    @Produces("text/plain;charset=utf-8")
    public String Status(){
        StringBuilder sb = new StringBuilder();
        Iterator<Integer> it = ids.iterator();
        while(it.hasNext()) {
            sb.append(it.next());
            if (it.hasNext())
                sb.append(", ");
        }
        return sb.toString();
    }


}