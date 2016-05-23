package de.hochschuledarmstadt.dashboard.app;

import de.hochschuledarmstadt.component.IMessageConsumer;
import de.hochschuledarmstadt.component.IMessageSender;
import de.hochschuledarmstadt.config.Credential;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MessageConsumer implements IMessageConsumer{

    private final Map<String, Credential> map;
    private final Map<Integer, String> localIdToUniqueIdMap;
    private final Map<String, Integer> uniqeToLocalIdMap;

    private static List<Integer> availableIds = new ArrayList<Integer>();

    static {
        for (int number = 1; number <= 100; number++){
            availableIds.add(number);
        }
    }

    public MessageConsumer(Map<String, Credential> map, Map<Integer, String> localIdToUniqueIdMap, Map<String, Integer> uniqeToLocalIdMap){
        this.map = map;
        this.localIdToUniqueIdMap = localIdToUniqueIdMap;
        this.uniqeToLocalIdMap = uniqeToLocalIdMap;
    }

    public void consumeMessage(IMessageSender messageSender, JSONObject message) {
        String id = message.getString("id");
        if (message.getString("type").equals("bootup")){
            String ip = message.getString("ip");
            int port = message.getInt("port");
            if (!map.containsKey(id)) {
                map.put(id, new Credential(Credential.PROTOCOL_UDP, ip, port));
                int localId = availableIds.get(0);
                availableIds.remove(0);
                localIdToUniqueIdMap.put(localId, id);
                uniqeToLocalIdMap.put(id, localId);
            }
        }else if(message.getString("type").equals("shutdown")){
            if (map.containsKey(id)) {
                map.remove(id);
                int localId = uniqeToLocalIdMap.get(id);
                uniqeToLocalIdMap.remove(id);
                localIdToUniqueIdMap.remove(localId);
                availableIds.add(localId);
                Collections.sort(availableIds);
            }
        }
    }
}
