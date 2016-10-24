package ch.ethz.inf.vs.a3.solution.clock;

import ch.ethz.inf.vs.a3.clock.Clock;

public class LamportClock implements Clock {
    public void setTime(int time) {

    }

    public int getTime() {
        return 0;
    }

    @Override
    public void update(Clock other) {

    }

    @Override
    public void setClock(Clock other) {

    }

    @Override
    public void tick(Integer pid) {

    }

    @Override
    public boolean happenedBefore(Clock other) {
        return false;
    }

    @Override
    public void setClockFromString(String clock) {

    }
}
