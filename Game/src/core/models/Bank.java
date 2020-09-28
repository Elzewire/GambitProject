package core.models;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bank implements Entity {
    //Main
    public int x;
    public int y;

    //Animations
    public Sprite sprite;
    public int offsetX;
    public int offsetY;
    public boolean isActivated = false;

    //Physics
    public Mask mask;
    public int maskOffsetX = 0;
    public int maskOffsetY = 0;

    //Healthbar
    public int actionBar = 0;
    public int actionBarMax = 100;
    private int healthbarWidth = 50;
    private int healthbarBorder = 2;
    private int healthbarHeight = 5;
    private int healthbarOffsetY = 10;

    public Bank(int x, int y, Sprite sprite, Mask mask, int maskOffsetX, int maskOffsetY) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
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
        gc.drawImage(sprite.getImage(0), x - offsetX, y - offsetY);
        gc.setStroke(Color.RED);

        if (isActivated) {
            int value = healthbarWidth * actionBar / actionBarMax;
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(x - healthbarWidth / 2 - healthbarBorder,
                    y - offsetY - healthbarOffsetY - healthbarBorder,
                    healthbarWidth + 2 * healthbarBorder,
                    healthbarHeight + 2 * healthbarBorder);
            gc.setFill(Color.YELLOW);
            gc.fillRect(x - healthbarWidth / 2, y - offsetY - healthbarOffsetY, value, healthbarHeight);
        }
    }

    public boolean collides(Bounds bounds) {
        return bounds.intersects(mask.getBounds());
    }
}
