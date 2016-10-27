package ch.ethz.inf.vs.a3.solution.message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;

import ch.ethz.inf.vs.a3.solution.clock.VectorClock;

public class Message {

    private Map mappings;

    public Message(String username, UUID uuid, String type, VectorClock timestamp, String body) {
        if (timestamp == null){
            System.out.println("Message received null timestamp");
        } else {
            System.out.println("Message received timestamp as " + timestamp.toString());
        }
        mappings = new HashMap();
        Map temp = new HashMap();
        temp.put("username", username);
        temp.put("uuid", uuid);
        if (timestamp == null) {
            temp.put("timestamp", new Object());
        } else {
            System.out.println("else case");
            temp.put("timestamp", timestamp.toString());
        }
        temp.put("type", type);
        JSONObject o = new JSONObject(temp);
        mappings.put("header", o);
        mappings.put("body", body);
    }

    public JSONObject getJSONObject() {
        JSONObject o = new JSONObject(mappings);

            System.out.println("after getJSONObject " + o);

        return new JSONObject(mappings);
    }

}
