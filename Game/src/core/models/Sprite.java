package core.models;

import javafx.scene.image.Image;

public class Sprite {
    private Image[] images;
    private String name;

    public Sprite(Image[] images, String name) {
        this.name = name;
        this.images = images;
    }

    public Image getImage(int i) {
        return images[i];
    }

    public int getSize() {
        return images.length;
    }

    public double getWidth() {
        return images[0].getWidth();
    }

    public double getHeight() {
        return images[0].getHeight();
    }

    public String getName() {
        return name;
    }
}
