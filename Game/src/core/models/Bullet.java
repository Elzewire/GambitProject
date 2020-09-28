package core.models;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Bullet {
    //Main
    public int x;
    public int y;
    public Player shooter;

    //Animations
    public Sprite sprite;

    //Physics
    public Mask mask;
    public int speed = 15;
    public int direction;

    //Game mechanics
    public double minDamage = 1;
    public double damage = 20;
    public double damageReduce = .1;

    public Bullet(int x, int y, int direction, Player shooter, Sprite sprite) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.shooter = shooter;
        this.sprite = sprite;
        this.mask = new Mask(x, y, (int)sprite.getWidth(), (int)sprite.getHeight());
    }

    public void update() {
        x += speed * Math.cos(Math.toRadians(direction));
        y += speed * Math.sin(Math.toRadians(direction));
        this.mask.x = x;
        this.mask.y = y;
        if (damage > minDamage) {
            damage -= damageReduce;
        } else {
            damage = minDamage;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(sprite.getImage(0), x, y);
        gc.setStroke(Color.RED);
        //gc.strokeRect(mask.x, mask.y, mask.width, mask.heigth);
    }
}
