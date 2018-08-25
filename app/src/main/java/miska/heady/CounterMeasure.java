package miska.heady;

import java.io.Serializable;
import java.util.Date;

public class CounterMeasure implements Serializable {
    private CounterMeasureName name;
    private Date timeOfIntake;

    public CounterMeasure(CounterMeasureName name, Date time) {
        this.name = name;
        this.timeOfIntake = time;
    }

    public CounterMeasureName getName() {
        return this.name;
    }

    public long getTimeOfIntake() {
        return this.timeOfIntake.getTime();
    }
}
