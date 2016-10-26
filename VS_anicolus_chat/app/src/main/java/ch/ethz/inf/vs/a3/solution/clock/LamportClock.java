package ch.ethz.inf.vs.a3.solution.clock;

import ch.ethz.inf.vs.a3.clock.Clock;


public class LamportClock implements Clock {
    private int time = 0;
    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    @Override
    public void update(Clock other) {
        this.setTime(Math.max(((LamportClock) other).getTime(), this.getTime()));
    }

    @Override
    public void setClock(Clock other) {
        this.setTime(((LamportClock) other).getTime());
    }

    @Override
    public void tick(Integer pid) {
        time = time + 1;
    }

    @Override
    public boolean happenedBefore(Clock other) {
        if(this.getTime() < ((LamportClock) other).getTime())
            return true;
        return false;
    }

    @Override
    public void setClockFromString(String clock) {
        boolean status = true;
        if(clock.length() < 1) {
            status = false;
        }
        for(int i = 0; i<clock.length(); i++) {
            char c = clock.charAt(i);
            if(Character.isDigit(c) && i >= 1) {
            } else {
                if(c == '-' && i == 0) {
                } else {
                    status = false;
                }
            }
        }
        if(status) {
            this.setTime(Integer.valueOf(clock));
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.getTime());
    }
}
