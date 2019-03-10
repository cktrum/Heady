package miska.heady;

import java.io.Serializable;
import java.util.Date;

public class Setting implements Serializable {
    private int numberOfEntries;
    private Date fromDate;

    public Setting (int numberOfEntries, Date fromDate) {
        this.numberOfEntries = numberOfEntries;
        this.fromDate = fromDate;
    }

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public long getFromDate() {
        return fromDate.getTime();
    }
}
