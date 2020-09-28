package core.misc;

import java.util.Random;

public class Playlist {
    private String [] music;
    private String playing;
    private Random rnd;

    public Playlist(String [] music) {
        this.music = music;
        rnd = new Random();
    }

    public String next() {
        playing = music[rnd.nextInt(music.length)];
        return playing;
    }

    public String playing() {
        return playing;
    }
}
