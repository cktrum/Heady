package miska.heady;

public enum Side {
    LEFT("Left"),
    MIDDLE("Middle"),
    RIGHT("Right");

    private String verboseName;

    Side(String verboseName) {
        this.verboseName = verboseName;
    }

    @Override
    public String toString() {
        return verboseName;
    }

    public int toInt() {
        switch (verboseName.toLowerCase()) {
            case "left": return 0;
            case "middle": return 1;
            case "right": return 2;
            default: return 0;
        }
    }

    public static Side fromString(String text) {
        switch (text.toLowerCase()) {
            case "left": return LEFT;
            case "middle": return MIDDLE;
            case "right": return RIGHT;
            default: return LEFT;
        }
    }
}