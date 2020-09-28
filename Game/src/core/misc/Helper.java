package core.misc;

import core.models.Sprite;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.shape.Line;

import java.util.List;

public class Helper {

    public static Sprite loadSprite(String path, int n, String name) {
        Image[] images = new Image[n];
        for (int i = 0; i < n; i++) {
            images[i] = new Image(path + i + ".png");
        }
        return new Sprite(images, name);
    }

    public static boolean placeMeeting(Bounds bbox, List<BoundingBox> walls) {
        for (BoundingBox wall : walls) {
            if (bbox.intersects(wall)) {
                return true;
            }
        }
        return false;
    }

    public static double distance(int x, int y, int x1, int y1) {
        return Math.sqrt((x1 - x) * (x1 - x) + (y - y1) * (y - y1));
    }

    public static boolean collisionLine(int x1, int y1, int x2, int y2, List<BoundingBox> walls) {
        Line line = new Line(x1, y1, x2, y2);
        for (BoundingBox wall : walls) {
            if (line.intersects(wall)) {
                return true;
            }
        }
        return false;
    }

    public static void initWalls(List<BoundingBox> walls, int levelWidth, int levelHeight) {
        //Level boxes
        walls.add(new BoundingBox(-20, 0, 20, levelHeight));
        walls.add(new BoundingBox(levelWidth, 0, 20, levelHeight));
        walls.add(new BoundingBox(0, -20, levelWidth, 20));
        walls.add(new BoundingBox(0, 443, levelWidth, 92));

        //Blocks; sorted by X val
        //Blue side
        //First three iterations
        for (int i = 0; i < 3; i++) {
            walls.add(new BoundingBox(i * 959, 393, 257, 50));
            walls.add(new BoundingBox(94 + i * 959, 353, 116, 42));
            walls.add(new BoundingBox(327 + i * 959, 176, 154, 96));
            walls.add(new BoundingBox(487 + i * 959, 393, 250, 50));
            walls.add(new BoundingBox(807 + i * 959, 353, 151, 92));
        }

        //Middle section
        walls.add(new BoundingBox(2876, 353, 47, 92));

        //Orange side
        //Second three iterations
        for (int i = 0; i < 3; i++) {
            walls.add(new BoundingBox(2923 + i * 959, 353, 151, 92));
            walls.add(new BoundingBox(3145 + i * 959, 393, 250, 50));
            walls.add(new BoundingBox(3402 + i * 959, 176, 154, 96));
            walls.add(new BoundingBox(3625 + i * 959, 393, 257, 50));
            walls.add(new BoundingBox(3672 + i * 959, 353, 116, 42));
        }
    }
}
