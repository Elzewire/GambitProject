package core.misc;

import javafx.scene.media.AudioClip;

import java.util.HashMap;
import java.util.Map;

public class AudioPlayer {
    private Map<String, AudioClip> audios = new HashMap<>();

    public void init() {
        audios.put("main_menu", new AudioClip(this.getClass().getResource("/res/sounds/main_menu.mp3").toExternalForm()));
        audios.put("loading", new AudioClip(this.getClass().getResource("/res/sounds/loading.mp3").toExternalForm()));
        audios.put("game_1", new AudioClip(this.getClass().getResource("/res/sounds/game_1.mp3").toExternalForm()));
        audios.put("game_2", new AudioClip(this.getClass().getResource("/res/sounds/game_2.mp3").toExternalForm()));
        audios.put("game_3", new AudioClip(this.getClass().getResource("/res/sounds/game_3.mp3").toExternalForm()));
        audios.put("game_4", new AudioClip(this.getClass().getResource("/res/sounds/game_4.mp3").toExternalForm()));

        //Sounds
        audios.put("death", new AudioClip(this.getClass().getResource("/res/sounds/ingame/death.mp3").toExternalForm()));
        audios.put("insert", new AudioClip(this.getClass().getResource("/res/sounds/ingame/insert.mp3").toExternalForm()));
        audios.put("kill", new AudioClip(this.getClass().getResource("/res/sounds/ingame/kill.mp3").toExternalForm()));
        audios.put("pickup", new AudioClip(this.getClass().getResource("/res/sounds/ingame/pickup.mp3").toExternalForm()));
        audios.put("revive", new AudioClip(this.getClass().getResource("/res/sounds/ingame/revive.mp3").toExternalForm()));
        audios.put("shoot", new AudioClip(this.getClass().getResource("/res/sounds/ingame/shoot.mp3").toExternalForm()));
        audios.put("other_shoot", new AudioClip(this.getClass().getResource("/res/sounds/ingame/other_shoot.mp3").toExternalForm()));
        /*
        audios.put("main_menu", new AudioClip("file:/res/sounds/main_menu.mp3"));
        audios.put("loading", new AudioClip("file:/res/sounds/loading.mp3"));
        audios.put("game_1", new AudioClip("file:/res/sounds/game_1.mp3"));
        audios.put("game_2", new AudioClip("file:/res/sounds/game_2.mp3"));

        //Sounds
        audios.put("death", new AudioClip("file:/res/sounds/ingame/death.mp3"));
        audios.put("insert", new AudioClip("file:/res/sounds/ingame/insert.mp3"));
        audios.put("kill", new AudioClip("file:/res/sounds/ingame/kill.mp3"));
        audios.put("pickup", new AudioClip("file:/res/sounds/ingame/pickup.mp3"));
        audios.put("revive", new AudioClip("file:/res/sounds/ingame/revive.mp3"));
        audios.put("shoot", new AudioClip("file:/res/sounds/ingame/shoot.mp3"));
        audios.put("other_shoot", new AudioClip("file:/res/sounds/ingame/other_shoot.mp3"));
        */
    }

    public AudioClip get(String s) {
        return audios.get(s);
    }
}
