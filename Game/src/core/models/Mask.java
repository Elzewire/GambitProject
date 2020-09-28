package core.models;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class Mask {
    double x;
    double y;
    int width;
    int heigth;

    public Mask(double x, double y, int width, int heigth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.heigth = heigth;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeigth() {
        return heigth;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeigth(int heigth) {
        this.heigth = heigth;
    }

    public Bounds getBounds() {
        return new BoundingBox(x, y, width, heigth);
    }
}
