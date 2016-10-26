package ch.ethz.inf.vs.a3.solution.message;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Message {

    private Map mappings;

    public Message(String username, UUID uuid, String type) {
        mappings = new HashMap();
        Map temp = new HashMap();
        temp.put("username", username);
        temp.put("uuid", uuid);
        temp.put("timestamp", new Object());
        temp.put("type", type);
        JSONObject o = new JSONObject(temp);
        mappings.put("header", o);
        mappings.put("body", new String());
    }

    public JSONObject getJSONObject() {
        return new JSONObject(mappings);
    }

}
