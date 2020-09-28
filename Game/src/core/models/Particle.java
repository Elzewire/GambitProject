package core.models;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {
    //Main
    public int x;
    public int y;

    //Animations
    public Sprite sprite;
    public double imageIndex;
    public double imageSpeed;
    public int offsetX;
    public int offsetY;

    //Physics
    public Mask mask;
    public int maskOffsetX = 0;
    public int maskOffsetY = 0;
    public int speed = 4;
    public int verSpeed = 0;
    public int gravity = 1;
    public int maxVerSpeed = 10;

    public Particle(int x, int y, Sprite sprite, int imageIndex, double imageSpeed, Mask mask, int maskOffsetX, int maskOffsetY) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.imageIndex = imageIndex;
        this.imageSpeed = imageSpeed;
        this.mask = mask;
        this.offsetX = (int)sprite.getImage(0).getWidth() / 2;
        this.offsetY = (int)sprite.getImage(0).getHeight();
        this.maskOffsetX = maskOffsetX;
        this.maskOffsetY = maskOffsetY;
    }

    public void update() {
        this.mask.x = x - maskOffsetX;
        this.mask.y = y - offsetY;
    }

    public void draw(GraphicsContext gc) {
        //Increase frame index of image
        if (imageIndex + imageSpeed <= sprite.getSize() - imageSpeed) {
            imageIndex += imageSpeed;
        } else {
            imageIndex = 0;
        }
        gc.drawImage(sprite.getImage((int)imageIndex), x - offsetX, y - offsetY);
        gc.setStroke(Color.RED);
        //gc.strokeRect(mask.x, mask.y, mask.width, mask.heigth);
        //gc.strokeOval(x, y, 4, 4);
    }

    public boolean collides(Bounds bounds) {
        return bounds.intersects(mask.getBounds());
    }
}
