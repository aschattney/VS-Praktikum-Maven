package de.hochschuledarmstadt.controlpanel.app;


import com.google.gson.Gson;
import de.hochschuledarmstadt.client.ISocketClient;
import de.hochschuledarmstadt.model.PrintJob;
import de.hochschuledarmstadt.model.request.PrintheadStatusRequest;
import org.glassfish.jersey.server.ResourceConfig;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

@Path("printer")
public class PrinterResource {

    private ISocketClient printer;
    private PrintJobExecutor printJobExecutor;

    public PrinterResource(ISocketClient printer, PrintJobExecutor printJobExecutor) {
        this.printer = printer;
        this.printJobExecutor = printJobExecutor;
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String Status(){
        Gson gson = new Gson();
        PrintheadStatusRequest request = new PrintheadStatusRequest();
        String message = gson.toJson(request);
        JSONObject jsonObject = null;
        try {
            jsonObject = printer.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @POST
    @Path("/job/{name}")
    @Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
    public String Job(@PathParam("name") String name){

        String message = "";
        try {
            PrintJob printJob = PrintPlanJSONFileReader.readFile(name + ".json");
            if(printJobExecutor.isPrinting())
                message = "Der Auftrag kommt in die Warteschlange";
            else
                message = "Der Auftrag wird jetzt ausgeführt.";

            printJobExecutor.addPrintJob(printJob);
        } catch (IOException e) {
            e.printStackTrace();
        }

            return message;
    }


}
