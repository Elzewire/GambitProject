package core.models;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class Player implements Entity {
    //Main
    public int x;
    public int y;

    //Stats
    public int hp = 100;
    public int maxHp = 100;
    public boolean dead = false;
    public boolean fade = false;

    //Networking
    public int id;
    public String team;
    public String name;

    //Animations
    public Sprite sprite;
    public double imageIndex;
    public double imageSpeed;
    public boolean right = true;
    public boolean jump = false;
    public int offsetX;
    public int offsetY;
    private int nameOffsetY = 15;

    //Physics
    public Mask mask;
    public int maskOffsetX = 0;
    public int maskOffsetY = 0;
    public int speed = 4;
    public int horSpeed = 0;
    public int verSpeed = 0;
    public int jumpSpeed = 20;
    public int gravity = 1;
    public int maxVerSpeed = 10;
    public int particles = 0;

    //Shooting
    public boolean shoot = false;
    public boolean shot = false;
    public int shootOffsetX = 0;
    public int shootOffsetY = 60;

    //Healthbar
    private int healthbarWidth = 50;
    private int healthbarBorder = 2;
    private int healthbarHeight = 5;
    private int healthbarOffsetY = 10;

    public Player(String name, int id, String team, int x, int y, Sprite sprite, int imageIndex, double imageSpeed, Mask mask, int maskOffsetX, int maskOffsetY) {
        this.name = name;
        this.id = id;
        this.team = team;
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
            if (fade) {
                dead = true;
            } else {
                imageIndex = 0;
            }
        }
        gc.drawImage(sprite.getImage((int)imageIndex), x - offsetX, y - offsetY);
        gc.setStroke(Color.RED);
        //gc.strokeRect(mask.x, mask.y, mask.width, mask.heigth);
        //gc.strokeOval(x, y, 4, 4);
    }

    public void drawName(GraphicsContext gc) {
        //Draw name
        gc.setTextAlign(TextAlignment.CENTER);
        if (team.equals("blue")) {
            gc.setFill(Color.DARKBLUE);
        } else {
            gc.setFill(Color.DARKORANGE);
        }
        gc.fillText(name + " [" + particles + "]", x, y - offsetY - nameOffsetY);
    }

    public void drawHealthbar(GraphicsContext gc) {
        int value = healthbarWidth * hp / maxHp;
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x - healthbarWidth / 2 - healthbarBorder,
                y - offsetY - healthbarOffsetY - healthbarBorder,
                healthbarWidth + 2 * healthbarBorder,
                healthbarHeight + 2 * healthbarBorder);
        if (team.equals("blue")) {
            gc.setFill(Color.DARKBLUE);
        } else {
            gc.setFill(Color.DARKORANGE);
        }
        gc.fillRect(x - healthbarWidth / 2, y - offsetY - healthbarOffsetY, value, healthbarHeight);
    }

    public boolean collides(Bounds bounds) {
        return bounds.intersects(mask.getBounds());
    }
}
