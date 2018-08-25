package miska.heady;

public enum PainType {
    CONSTANT("Constant pain"),
    PULSATING("Pulsating pain");

    private String verboseName;

    PainType(String verboseName) {
        this.verboseName = verboseName;
    }

    @Override
    public String toString() {
        return verboseName;
    }

    public int toInt() {
        switch (verboseName.toLowerCase()) {
            case "constant pain": return 0;
            case "pulsating pain": return 1;
            default: return 0;
        }
    }

    public static PainType fromString(String text) {
        switch (text.toLowerCase()) {
            case "constant pain": return CONSTANT;
            case "pulsating pain": return PULSATING;
            default: return CONSTANT;
        }
    }
}
