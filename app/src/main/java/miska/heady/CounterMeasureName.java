package miska.heady;

public enum CounterMeasureName {
    COFFEE("Coffee"),
    FOOD("Food"),
    SLEEP("Sleep"),
    EXERCISE("Exercise"),
    IBUPROFEN("Ibuprofen");

    private String verboseName;

    CounterMeasureName(String verboseName) {
        this.verboseName = verboseName;
    }

    @Override
    public String toString() {
        return verboseName;
    }

    public int toInt() {
        switch (verboseName.toLowerCase()) {
            case "coffee": return 0;
            case "food": return 1;
            case "sleep": return 2;
            case "exercise": return 3;
            case "ibuprofen": return 4;
            default: return 0;
        }
    }

    public static CounterMeasureName fromString(String text) {
        switch (text.toLowerCase()) {
            case "coffee": return COFFEE;
            case "food": return FOOD;
            case "sleep": return SLEEP;
            case "exercise": return EXERCISE;
            case "ibuprofen": return IBUPROFEN;
            default: return COFFEE;
        }
    }
}
