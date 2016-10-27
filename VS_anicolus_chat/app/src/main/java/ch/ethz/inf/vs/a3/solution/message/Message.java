package ch.ethz.inf.vs.a3.solution.message;

import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import ch.ethz.inf.vs.a3.solution.clock.VectorClock;

public class Message {

    private Map mappings;

    public Message(String username, UUID uuid, String type, VectorClock timestamp, String body) {
        mappings = new HashMap();
        Map temp = new HashMap();
        temp.put("username", username);
        temp.put("uuid", uuid);
        if (timestamp == null) {
            temp.put("timestamp", new Object());
        } else {

            temp.put("timestamp", timestamp.toString());
        }
        temp.put("type", type);
        JSONObject o = new JSONObject(temp);
        mappings.put("header", o);
        mappings.put("body", body);
    }

    public JSONObject getJSONObject() {
        return new JSONObject(mappings);
    }

}
