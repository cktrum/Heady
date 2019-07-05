package miska.heady;

public class DaysPerMonthEntry {

    private int amount;
    private String monthLabel;

    DaysPerMonthEntry(int amount, String monthLabel) {
        this.amount = amount;
        this.monthLabel = monthLabel;
    }

    public int getAmount() {
        return this.amount;
    }

    public String getMonthLabel() {
        return this.monthLabel;
    }
}
