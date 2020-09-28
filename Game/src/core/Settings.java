package core;

import javafx.scene.input.KeyCode;

import java.util.HashMap;
import java.util.Map;

public class Settings {
    private static Map<String, KeyCode> keys = new HashMap<>();

    public static void init() {
        keys.put("right", KeyCode.D);
        keys.put("left", KeyCode.A);
        keys.put("jump", KeyCode.SPACE);
        keys.put("shoot", KeyCode.ENTER);
        keys.put("action", KeyCode.E);
    }

    public static KeyCode key(String key) {
        return keys.get(key);
    }
}
