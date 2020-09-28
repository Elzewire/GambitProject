package core.misc;

import core.models.Sprite;

import java.util.HashMap;
import java.util.Map;

public class SpriteBank {

    private static Map<String, Sprite> sprites = new HashMap<>();

    public static void init() {
        //Blue
        sprites.put("blue_player_idle", Helper.loadSprite("res/player/blue/blue_player_idle_", 8, "blue_player_idle"));
        sprites.put("blue_player_idle_left", Helper.loadSprite("res/player/blue/blue_player_idle_left_", 8, "blue_player_idle_left"));

        sprites.put("blue_player_run", Helper.loadSprite("res/player/blue/blue_player_run_", 8, "blue_player_run"));
        sprites.put("blue_player_run_left", Helper.loadSprite("res/player/blue/blue_player_run_left_", 8,"blue_player_run_left"));

        sprites.put("blue_player_jump", Helper.loadSprite("res/player/blue/blue_player_jump_", 4, "blue_player_jump"));
        sprites.put("blue_player_jump_left", Helper.loadSprite("res/player/blue/blue_player_jump_left_", 4,"blue_player_jump_left"));

        sprites.put("blue_player_fall", Helper.loadSprite("res/player/blue/blue_player_fall_", 4, "blue_player_fall"));
        sprites.put("blue_player_fall_left", Helper.loadSprite("res/player/blue/blue_player_fall_left_", 4, "blue_player_fall_left"));

        sprites.put("blue_player_shoot", Helper.loadSprite("res/player/blue/blue_player_shoot_", 4, "blue_player_shoot"));
        sprites.put("blue_player_shoot_left", Helper.loadSprite("res/player/blue/blue_player_shoot_left_", 4, "blue_player_shoot_left"));

        sprites.put("blue_player_die", Helper.loadSprite("res/player/blue/blue_player_die_", 9, "blue_player_die"));
        sprites.put("blue_player_die_left", Helper.loadSprite("res/player/blue/blue_player_die_left_", 9, "blue_player_die_left"));


        //Orange
        sprites.put("orange_player_idle", Helper.loadSprite("res/player/orange/orange_player_idle_", 8, "orange_player_idle"));
        sprites.put("orange_player_idle_left", Helper.loadSprite("res/player/orange/orange_player_idle_left_", 8, "orange_player_idle_left"));

        sprites.put("orange_player_run", Helper.loadSprite("res/player/orange/orange_player_run_", 8, "orange_player_run"));
        sprites.put("orange_player_run_left", Helper.loadSprite("res/player/orange/orange_player_run_left_", 8,"orange_player_run_left"));

        sprites.put("orange_player_jump", Helper.loadSprite("res/player/orange/orange_player_jump_", 4, "orange_player_jump"));
        sprites.put("orange_player_jump_left", Helper.loadSprite("res/player/orange/orange_player_jump_left_", 4,"orange_player_jump_left"));

        sprites.put("orange_player_fall", Helper.loadSprite("res/player/orange/orange_player_fall_", 4, "orange_player_fall"));
        sprites.put("orange_player_fall_left", Helper.loadSprite("res/player/orange/orange_player_fall_left_", 4, "orange_player_fall_left"));

        sprites.put("orange_player_shoot", Helper.loadSprite("res/player/orange/orange_player_shoot_", 4, "orange_player_shoot"));
        sprites.put("orange_player_shoot_left", Helper.loadSprite("res/player/orange/orange_player_shoot_left_", 4, "orange_player_shoot_left"));

        sprites.put("orange_player_die", Helper.loadSprite("res/player/orange/orange_player_die_", 9, "orange_player_die"));
        sprites.put("orange_player_die_left", Helper.loadSprite("res/player/orange/orange_player_die_left_", 9, "orange_player_die_left"));

        //Misc
        sprites.put("blue_bullet", Helper.loadSprite("res/other/blue_bullet_", 1, "blue_bullet"));
        sprites.put("orange_bullet", Helper.loadSprite("res/other/orange_bullet_", 1, "orange_bullet"));
        sprites.put("blue_bank", Helper.loadSprite("res/other/blue_bank_", 1, "blue_bank"));
        sprites.put("orange_bank", Helper.loadSprite("res/other/orange_bank_", 1, "orange_bank"));
        sprites.put("particle", Helper.loadSprite("res/other/particle_", 4, "particle"));

        //Enemy
        sprites.put("harpy_float", Helper.loadSprite("res/enemy/harpy_float_", 8, "harpy_float"));
        sprites.put("harpy_float_left", Helper.loadSprite("res/enemy/harpy_float_left_", 8, "harpy_float_left"));

        sprites.put("harpy_attack", Helper.loadSprite("res/enemy/harpy_attack_", 4, "harpy_attack"));
        sprites.put("harpy_attack_left", Helper.loadSprite("res/enemy/harpy_attack_left_", 4, "harpy_attack_left"));

        //Menu
        sprites.put("gambit_text", Helper.loadSprite("res/menu/gambit_text_", 6, "gambit_text"));
        sprites.put("start_text", Helper.loadSprite("res/menu/start_text_", 2, "start_text"));
        sprites.put("logo", Helper.loadSprite("res/menu/logo_", 4, "logo"));
        sprites.put("loading", Helper.loadSprite("res/menu/loading_", 8, "loading"));
    }

    public static Sprite get(String s) {
        return sprites.get(s);
    }
}
