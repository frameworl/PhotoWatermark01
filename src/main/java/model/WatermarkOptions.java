package model;

import java.awt.Color;

public class WatermarkOptions {
    private int fontSize = 12;
    private Color color = Color.WHITE;
    private Position position = Position.BOTTOM_RIGHT;

    // Getters and setters
    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setColor(String colorName) {
        switch (colorName.toLowerCase()) {
            case "black": this.color = Color.BLACK; break;
            case "white": this.color = Color.WHITE; break;
            case "red": this.color = Color.RED; break;
            case "green": this.color = Color.GREEN; break;
            case "blue": this.color = Color.BLUE; break;
            default: this.color = Color.WHITE; break;
        }
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}