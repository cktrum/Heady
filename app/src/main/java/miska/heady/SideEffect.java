package miska.heady;

public enum SideEffect {
    NONE("None"),
    VOMITING("Vomiting");

    private String verboseName;

    SideEffect(String verboseName) {
        this.verboseName = verboseName;
    }

    @Override
    public String toString() {
        return verboseName;
    }

    public int toInt() {
        switch (verboseName.toLowerCase()) {
            case "none": return 0;
            case "": return 0;
            case "vomiting": return 1;
            default: return 0;
        }
    }

    public static SideEffect fromString(String text) {
        switch (text.toLowerCase()) {
            case "": return NONE;
            case "none": return NONE;
            case "vomiting": return VOMITING;
            default: return NONE;
        }
    }
}
