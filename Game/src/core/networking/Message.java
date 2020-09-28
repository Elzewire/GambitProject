package core.networking;

public class Message {
    //Broadcasted messages
    public static final String CREATE_PLAYER = "cp";
    public static final String CREATE_BULLET = "cb";
    public static final String CREATE_PARTICLE = "ca";
    public static final String MOVE_PLAYER = "mp";
    public static final String ANIMATE_PLAYER = "ap";
    public static final String REMOVE_PLAYER = "rp";
    public static final String ADD_SCORE = "ad";

    public static final String READY = "r";
    public static final String ID = "i";

    public static final String TEAM_BLUE = "tb";
    public static final String TEAM_ORANGE = "to";
    public static final String LOBBY_FULL = "f";
    public static final String FREE_TEAM_BLUE = "fr/b";
    public static final String FREE_TEAM_ORANGE = "fr/o";
    public static final String TERMINATE = "x";
}
