package de.hochschuledarmstadt.controlpanel.app;

import com.google.gson.Gson;
import de.hochschuledarmstadt.model.PrintJob;

import java.io.*;

public class PrintPlanJSONFileReader {

    public static PrintJob readFile(String fileName) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return new Gson().fromJson(sb.toString(), PrintJob.class);
    }

}
