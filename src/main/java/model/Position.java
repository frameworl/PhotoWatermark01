package model;

public enum Position {
    TOP_LEFT("top-left"),
    CENTER("center"),
    BOTTOM_RIGHT("bottom-right");

    private final String value;

    Position(String value) {
        this.value = value;
    }

    public static Position fromString(String text) {
        for (Position p : Position.values()) {
            if (p.value.equalsIgnoreCase(text)) {
                return p;
            }
        }
        return BOTTOM_RIGHT; // 默认值
    }

    public String getValue() {
        return value;
    }
}