package core.models;

import javafx.scene.canvas.GraphicsContext;

public class Animation {
    public static final String REPEAT = "repeat";
    public static final String FREEZE = "freeze";

    //Main
    public int x;
    public int y;

    //Animations
    public boolean play;
    public Sprite sprite;
    public double imageIndex;
    public double imageSpeed;
    public int offsetX;
    public int offsetY;
    public String onEnd;

    public Animation(int x, int y, Sprite sprite, int imageIndex, double imageSpeed) {
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.imageIndex = imageIndex;
        this.imageSpeed = imageSpeed;
        this.offsetX = (int)sprite.getImage(0).getWidth() / 2;
        this.offsetY = (int)sprite.getImage(0).getHeight();
        this.onEnd = Animation.REPEAT;
        this.play = false;
    }

    public void draw(GraphicsContext gc) {
        //Increase frame index of image
        if (play) {
            if (imageIndex + imageSpeed <= sprite.getSize() - imageSpeed) {
                imageIndex += imageSpeed;
            } else {
                if (onEnd.equals(Animation.REPEAT)) {
                    imageIndex = 0;
                } else if (onEnd.equals(Animation.FREEZE)) {
                    imageIndex = sprite.getSize() - 1;
                }
            }
            gc.drawImage(sprite.getImage((int) imageIndex), x - offsetX, y - offsetY);
        }
    }

    public void setOnEnd(String onEnd) {
        this.onEnd = onEnd;
    }

    public  void play() {
        play = true;
    }
}
