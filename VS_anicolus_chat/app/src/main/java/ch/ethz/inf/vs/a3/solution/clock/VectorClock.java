package ch.ethz.inf.vs.a3.solution.clock;

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
        String s = clock.substring(1, clock.length()-1);
        String[] pairs = s.split(",");
        for (int i=0;i<pairs.length;i++) {
            String pair = pairs[i];
            String[] keyValue = pair.split(":");
            cl.addProcess(Integer.valueOf(keyValue[0].substring(1, keyValue[0].length()-1)), Integer.valueOf(keyValue[1]));
        }
        update(cl);
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
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
