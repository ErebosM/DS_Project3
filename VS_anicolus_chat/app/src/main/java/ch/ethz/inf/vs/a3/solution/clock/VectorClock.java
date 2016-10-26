package ch.ethz.inf.vs.a3.solution.clock;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import ch.ethz.inf.vs.a3.clock.Clock;

public class VectorClock implements Clock {
    private Map<Integer, Integer> vector = new HashMap<>();
    public int getTime(Integer pid) {
        return vector.get(pid);
    }

    public void addProcess(Integer pid, int time) {
        vector.put(pid, time);
    }

    @Override
    public void update(Clock other) {
        Map<Integer, Integer> secondvector = ((VectorClock) other).vector;
        for(Map.Entry<Integer, Integer> entry: secondvector.entrySet()) {
            if(vector.containsKey(entry.getKey())) {
                if(vector.get(entry.getKey()) < entry.getValue()) {
                    vector.remove(entry.getKey());
                    vector.put(entry.getKey(), entry.getValue());
                }
            } else {
                vector.put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setClock(Clock other) {
        vector = ((VectorClock) other).vector;
    }

    @Override
    public void tick(Integer pid) {
        int value = vector.get(pid);
        vector.remove(pid);
        vector.put(pid, value + 1);
    }

    @Override
    public boolean happenedBefore(Clock other) {
        boolean value = true;
        Map<Integer, Integer> secondvector = ((VectorClock) other).vector;
        for(Map.Entry<Integer, Integer> entry: secondvector.entrySet()) {
            if(vector.containsKey(entry.getKey())) {
                if(vector.get(entry.getKey()) > entry.getValue()) {
                    value = false;
                }
            }
        }
        return value;
    }

    @Override
    public void setClockFromString(String clock) {
        VectorClock cl = new VectorClock();
        boolean valid = true;
        if (clock.length() > 2) {
            String s = clock.substring(1, clock.length() - 1);
            String[] pairs = s.split(",");
            for (int i = 0; i < pairs.length; i++) {
                String pair = pairs[i];
                String[] keyValue = pair.split(":");
                boolean status1 = true;
                for (int j = 1; j <= keyValue[0].length() - 2; j++) {
                    char c = keyValue[0].charAt(j);
                    if (!Character.isDigit(c)) {
                        status1 = false;
                    }
                }
                boolean status2 = true;
                for (int j = 0; j <= keyValue[1].length()-1; j++) {
                    char c = keyValue[1].charAt(j);
                    if (!Character.isDigit(c)) {
                        status2 = false;
                    }
                }
                if (status1 && status2) {
                    cl.addProcess(Integer.valueOf(keyValue[0].substring(1, keyValue[0].length() - 1)), Integer.valueOf(keyValue[1]));
                } else {
                    valid = false;
                }
            }
        }
        if(valid) {
            setClock(cl);
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for(Map.Entry<Integer, Integer> entry: vector.entrySet()) {
            stringBuilder.append("\"");
            stringBuilder.append(entry.getKey());
            stringBuilder.append("\"");
            stringBuilder.append(":");
            stringBuilder.append(entry.getValue());
            stringBuilder.append(",");
        }
        if(vector.entrySet().size() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
