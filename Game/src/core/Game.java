package core;

import core.misc.AudioPlayer;
import core.misc.Helper;
import core.misc.Playlist;
import core.misc.SpriteBank;
import core.models.*;
import core.networking.Message;
import core.networking.Networker;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.BoundingBox;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.List;

public class Game extends Application {

    //Global game variables
    private AudioPlayer audioPlayer;
    public String serverAddress;
    private boolean gameWon = false;
    private boolean gameLost = false;
    private AudioClip audio;
    private Font nicknames;
    private Font stats;
    private int gameState = 0; //Format: state: 0 - Main Menu, 1 - Loading/Connecting, 2 - Playing
    private Map<KeyCode, Boolean> input = new HashMap<>();
    private Networker networker;
    private Pane appRoot = new Pane();
    private Pane gameRoot = new Pane();
    private Pane uiRoot = new Pane();
    private Canvas canvas;
    private Canvas ui;
    private boolean serverReady = false;
    private int restartTimer = 300;
    private int restartTime = 300;


    private int blueSpawnX = 150;
    private int blueSpawnY = 351;

    private int orangeSpawnX = 5648;
    private int orangeSpawnY = 351;

    //Menu
    private Canvas menuCanvas;
    private Canvas loadingCanvas;
    private Image menuBg;
    private Map<String, Animation> animationMap = new HashMap<>();
    private Map<String, Integer> animationClock = new HashMap<>();
    private Pane loadingRoot = new Pane();
    private Pane menuRoot = new Pane();
    private int animationTimer = 0;
    private int animationTimerStop = 1000;

    //Fx scenes
    private Scene menuScene;
    private Scene loadingScene;
    private Scene gameScene;

    //Game logic variables
    private boolean rollback = false;
    private Player player;
    private String playerTeam;
    private String playerName;
    private int playerId = 0;
    private List<Player> blueTeam = new ArrayList<>();
    private List<Player> orangeTeam = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<Bullet> enemyBullets = new ArrayList<>();
    private List<BoundingBox> walls = new ArrayList<>();
    private List<Particle> particles = new ArrayList<>();
    private Bank blueBank;
    private Bank orangeBank;
    private int blueScore = 0;
    private int orangeScore = 0;
    private int maxScore = 20;
    private int playerScore = 0;
    private int playerMaxScore = 15;
    private int respawnTimer = 300;
    private int respawnTime = 300;
    private int lasthitId = 0;
    private Playlist playlist;

    //Room and view variables
    private Image bg;
    private int levelWidth = 5800;
    private int levelHeight = 540;
    private int viewWidth = 960;
    private int viewHeight = 540;
    private int appWidth = 966;
    private int appHeight = 569;

    private void initGame() {
        //Init level walls
        Helper.initWalls(walls, levelWidth, levelHeight);

        //Font
        /*
        nicknames = Font.loadFont("file:src/client/res/font/12160.ttf", 12);
        stats = Font.loadFont("file:src/client/res/font/12160.ttf", 22);
        */

        nicknames = Font.loadFont(this.getClass().getResource("/res/font/12160.ttf").toExternalForm(), 12);
        stats = Font.loadFont(this.getClass().getResource("/res/font/12160.ttf").toExternalForm(), 22);

        //Init bg and game room
        bg = new Image("res/level.png");
        canvas = new Canvas(levelWidth, levelHeight);
        ui = new Canvas(viewWidth, viewHeight);

        //Spawn banks
        blueBank = new Bank(blueSpawnX, blueSpawnY, SpriteBank.get("blue_bank"),
                new Mask(blueSpawnX, blueSpawnY, 40, 40), 20, 0);
        orangeBank = new Bank(orangeSpawnX, orangeSpawnY, SpriteBank.get("orange_bank"),
                new Mask(orangeSpawnX, orangeSpawnY, 40, 40), 20, 0);

        //Init player
        if (playerTeam.equals("blue")) {
            player = new Player(playerName, playerId, playerTeam, blueSpawnX, blueSpawnY, SpriteBank.get("blue_player_idle"),
                    0, .1,
                    new Mask(blueSpawnX, blueSpawnY, 30, 71),
                    15, 0);
            blueTeam.add(player);
        } else {
            player = new Player(playerName, playerId, playerTeam, orangeSpawnX, orangeSpawnY, SpriteBank.get("orange_player_idle"),
                    0, .1,
                    new Mask(orangeSpawnX, orangeSpawnY, 30, 71),
                    15, 0);
            orangeTeam.add(player);
        }

        audioPlayer.get("revive").play();

        //TODO : BROADCAST FORMAT: id, team, x, y, imageSpeed, maskOffsetX, maskOffsetY
        broadcast(Message.CREATE_PLAYER + "/" +
                playerId + "," +
                playerTeam + "," +
                player.x + "," +
                player.y + "," +
                player.imageSpeed + "," +
                player.maskOffsetX + "," +
                player.maskOffsetY + "," +
                playerName
        );

        //Init fx roots
        canvas.getGraphicsContext2D().setFont(nicknames);
        gameRoot.getChildren().add(canvas);
        uiRoot.getChildren().addAll(ui);
        appRoot.getChildren().addAll(gameRoot, uiRoot);
        appRoot.setMaxSize(viewWidth, levelHeight);
        appRoot.setPrefSize(viewWidth, levelHeight);
        appRoot.setMinSize(viewWidth, levelHeight);

        playlist = new Playlist(new String []{"game_1", "game_2", "game_3", "game_4"});

        //audio = new AudioClip(audios[rnd.nextInt(audios.length)]);
        //audio.play();
        audioPlayer.get(playlist.next()).play();
    }

    private void initNetwork() throws IOException {
        //Init network handler
        networker = new Networker(this);
    }

    private void initLoading() {
        loadingCanvas = new Canvas(viewWidth, viewHeight);

        animationMap.put("loading", new Animation(480, 342, SpriteBank.get("loading"), 0, .1));
        animationMap.get("loading").setOnEnd(Animation.REPEAT);

        //New audio
        //audio = new AudioClip("file:src/client/res/sounds/loading.mp3");
        //audio.play();
        audioPlayer.get("loading").play();

        loadingRoot.getChildren().add(loadingCanvas);
    }

    private void initMenu() {

        menuBg = new Image("res/menu/menu_bg.png");
        menuCanvas = new Canvas(viewWidth, viewHeight);

        //Generate all text animations
        animationMap.put("gambit_text", new Animation(480, 160, SpriteBank.get("gambit_text"), 0, .1));
        animationMap.get("gambit_text").setOnEnd(Animation.FREEZE);
        animationClock.put("gambit_text", 550);

        animationMap.put("start_text", new Animation(480, 440, SpriteBank.get("start_text"), 0, .05));
        animationMap.get("start_text").setOnEnd(Animation.REPEAT);
        animationClock.put("start_text", 600);

        animationMap.put("logo", new Animation(480, 300, SpriteBank.get("logo"), 0, .1));
        animationMap.get("logo").setOnEnd(Animation.FREEZE);
        animationClock.put("logo", 150);

        menuRoot.getChildren().add(menuCanvas);

        //Play music
        //audio = new AudioClip("file:src/client/res/sounds/main_menu.mp3");
        //audio.play();
        audioPlayer.get("main_menu").play();
    }

    private void closeGame() {
        //Close level walls
        walls = new ArrayList<>();

        //Font
        nicknames = null;
        stats = null;

        //Close bg and game room
        bg = null;
        canvas = null;
        ui = null;

        //Spawn banks
        blueBank = null;
        orangeBank = null;

        //Init player
        player = null;

        audio.stop();
        audio = null;
    }

    private void closeLoading() {
        animationTimer = 0;
        animationMap = new HashMap<>();
        animationClock = new HashMap<>();
        loadingCanvas = null;
        audioPlayer.get("loading").stop();
        //audio.stop();
        //audio = null;
    }

    private void closeMenu() {
        animationTimer = 0;
        animationMap = new HashMap<>();
        animationClock = new HashMap<>();
        audioPlayer.get("main_menu").stop();
        //audio.stop();
        //audio = null;
        menuBg = null;
        menuCanvas = null;
        input = new HashMap<>();
    }
    //Game itself

    private void update() {
        //Every step
        //Respawn player

        if (!gameLost && !gameWon) {
            if (player.dead) {
                if (respawnTimer > 0) {
                    respawnTimer--;
                } else {
                    //Respawn
                    if (playerTeam.equals("blue")) {
                        player.x = blueSpawnX;
                        player.y = blueSpawnY;
                    } else {
                        player.x = orangeSpawnX;
                        player.y = orangeSpawnY;
                    }
                    player.fade = false;
                    player.dead = false;
                    player.hp = player.maxHp;
                    /* if (player.right) {
                        player.sprite = SpriteBank.get(playerTeam + "_player_idle");
                        //Update offset
                        player.offsetY = (int) player.sprite.getHeight();
                        player.offsetX = 25;
                        //Update player mask
                        player.mask.setHeigth((int) player.sprite.getHeight());
                    } else {
                        player.sprite = SpriteBank.get(playerTeam + "_player_idle_left");
                        //Update offset
                        player.offsetY = (int) player.sprite.getHeight();
                        player.offsetX = 20;
                        //Update player mask
                        player.mask.setHeigth((int) player.sprite.getHeight());
                    } */
                    //player.imageSpeed = .1;
                    respawnTimer = respawnTime;
                    audioPlayer.get("revive").play();
                }
            }

            //Player shoot
            //Player update
            if (!player.fade) {
                if (keyPressed(Settings.key("shoot")) && !player.jump) {
                    if (!player.shoot) {
                        player.shoot = true;
                        player.shot = false;
                        if (player.right) {
                            player.sprite = SpriteBank.get(playerTeam + "_player_shoot");
                            //Update offset
                            player.offsetY = (int) player.sprite.getHeight();
                            player.offsetX = 22;
                            //Update player mask
                            player.mask.setHeigth((int) player.sprite.getHeight());
                        } else {
                            player.sprite = SpriteBank.get(playerTeam + "_player_shoot_left");
                            //Update offset
                            player.offsetY = (int) player.sprite.getHeight();
                            player.offsetX = 30;
                            //Update player mask
                            player.mask.setHeigth((int) player.sprite.getHeight());
                        }
                        player.imageSpeed = .15;
                    }
                }

                //Shooting
                if (player.shoot) {
                    //Check if animation state is at second frame
                    if ((int) player.imageIndex == 1) {
                        if (!player.shot) {
                            if (player.right) {
                                audioPlayer.get("shoot").play();
                                bullets.add(new Bullet(player.x, player.y - player.shootOffsetY, 0, player, SpriteBank.get(playerTeam + "_bullet")));
                                //TODO : BROADCASTING FORMAT: x, y, shooterId, direction
                                broadcast(Message.CREATE_BULLET + "/" +
                                        player.x + "," +
                                        (player.y - player.shootOffsetY) + "," +
                                        playerId + "," +
                                        "0"
                                );
                            } else {
                                audioPlayer.get("shoot").play();
                                bullets.add(new Bullet(player.x, player.y - player.shootOffsetY, 180, player, SpriteBank.get(playerTeam + "_bullet")));
                                //TODO : BROADCASTING FORMAT: x, y, shooterId, direction
                                broadcast(Message.CREATE_BULLET + "/" +
                                        player.x + "," +
                                        (player.y - player.shootOffsetY) + "," +
                                        playerId + "," +
                                        "180"
                                );
                            }
                            System.out.println("pew-pew");
                            player.shot = true;
                        }
                    } else if ((int) player.imageIndex == 3) {
                        //End animation
                        if (!keyPressed(Settings.key("shoot"))) {
                            player.shoot = false;
                        }
                        player.shot = false;
                    }
                }

                //Player movement
                if (!player.shoot) {
                    //Check for ground
                    //Move bbox of player to a pos where it could collide a wall
                    BoundingBox temp = new BoundingBox(
                            player.mask.getX(),
                            player.mask.getY() + 1,
                            player.mask.getWidth(),
                            player.mask.getHeigth());
                    if (Helper.placeMeeting(temp, walls)) {
                        player.verSpeed = 0;
                        player.jump = false;

                        //Jump
                        if (keyPressed(Settings.key("jump"))) {
                            player.jump = true;
                            player.verSpeed = -player.jumpSpeed;
                        }
                    } else {
                        //Gravity
                        if (player.verSpeed < player.maxVerSpeed) {
                            player.jump = true;
                            player.verSpeed += player.gravity;
                        }
                    }

                    //Jumping animation
                    if (player.jump && !player.shoot) {
                        if (player.verSpeed < 0) {
                            if (player.right) {
                                player.sprite = SpriteBank.get(playerTeam + "_player_jump");
                                //Update offset
                                player.offsetY = (int) player.sprite.getHeight();
                                player.offsetX = 43;
                                //Update player mask
                                player.mask.setHeigth((int) player.sprite.getHeight());
                            } else {
                                player.sprite = SpriteBank.get(playerTeam + "_player_jump_left");
                                //Update offset
                                player.offsetY = (int) player.sprite.getHeight();
                                player.offsetX = 21;
                                //Update player mask
                                player.mask.setHeigth((int) player.sprite.getHeight());
                            }
                            player.imageSpeed = .2;
                        } else {
                            if (player.right) {
                                player.sprite = SpriteBank.get(playerTeam + "_player_fall");
                                //Update offset
                                player.offsetY = (int) player.sprite.getHeight();
                                player.offsetX = 53;
                                //Update player mask
                                player.mask.setHeigth((int) player.sprite.getHeight());
                            } else {
                                player.sprite = SpriteBank.get(playerTeam + "_player_fall_left");
                                //Update offset
                                player.offsetY = (int) player.sprite.getHeight();
                                player.offsetX = 19;
                                //Update player mask
                                player.mask.setHeigth((int) player.sprite.getHeight());
                            }

                            player.imageSpeed = .2;
                        }
                    }

                    if (keyPressed(Settings.key("right"))) {
                        player.right = true;
                        player.horSpeed = player.speed;
                        //Animation
                        if (!player.jump) {
                            player.sprite = SpriteBank.get(playerTeam + "_player_run");
                            player.imageSpeed = .2;
                            //Update offset
                            player.offsetY = (int) player.sprite.getHeight();
                            player.offsetX = 48;
                            //Update player mask
                            player.mask.setHeigth((int) player.sprite.getHeight());
                        }
                    }

                    if (keyPressed(Settings.key("left"))) {
                        player.right = false;
                        player.horSpeed = -player.speed;
                        //Animation
                        if (!player.jump) {
                            player.sprite = SpriteBank.get(playerTeam + "_player_run_left");
                            player.imageSpeed = .2;
                            //Update offset
                            player.offsetY = (int) player.sprite.getHeight();
                            player.offsetX = 22;
                            //Update player mask
                            player.mask.setHeigth((int) player.sprite.getHeight());
                        }
                    }

                    if ((!keyPressed(Settings.key("right")) && !keyPressed(Settings.key("left"))) ||
                            (keyPressed(Settings.key("right")) && keyPressed(Settings.key("left")))) {
                        player.horSpeed = 0;
                        //Animation
                        if (!player.jump) {
                            if (player.right) {
                                player.sprite = SpriteBank.get(playerTeam + "_player_idle");
                                //Update offset
                                player.offsetY = (int) player.sprite.getHeight();
                                player.offsetX = 25;
                                //Update player mask
                                player.mask.setHeigth((int) player.sprite.getHeight());
                            } else {
                                player.sprite = SpriteBank.get(playerTeam + "_player_idle_left");
                                //Update offset
                                player.offsetY = (int) player.sprite.getHeight();
                                player.offsetX = 20;
                                //Update player mask
                                player.mask.setHeigth((int) player.sprite.getHeight());
                            }
                            player.imageSpeed = .1;
                        }
                    }

                    //Horizontal collisions
                    //Move bbox of player to a pos where it could collide a wall
                    temp = new BoundingBox(
                            player.mask.getX() + player.horSpeed,
                            player.mask.getY(),
                            player.mask.getWidth(),
                            player.mask.getHeigth());

                    if (Helper.placeMeeting(temp, walls)) {
                        while (!Helper.placeMeeting(temp, walls)) {
                            player.x += (int) Math.signum(player.horSpeed);
                        }
                        player.horSpeed = 0;
                    }

                    //Update x pos
                    player.x += player.horSpeed;

                    //Vertical collisions
                    //Move bbox of player to a pos where it could collide a wall
                    temp = new BoundingBox(
                            player.mask.getX(),
                            player.mask.getY() + player.verSpeed,
                            player.mask.getWidth(),
                            player.mask.getHeigth());

                    if (Helper.placeMeeting(temp, walls)) {
                        while (!Helper.placeMeeting(temp, walls)) {
                            player.y += (int) Math.signum(player.verSpeed);
                        }
                        player.verSpeed = 0;
                    }

                    //Update y pos
                    player.y += player.verSpeed;

                }

                if (player.hp <= 0) {
                    audioPlayer.get("death").play();
                    player.fade = true;
                    player.shoot = false;
                    player.shot = false;
                    player.jump = false;
                    player.verSpeed = 0;
                    player.horSpeed = 0;
                    playerScore = 0;
                    if (player.right) {
                        player.sprite = SpriteBank.get(playerTeam + "_player_die");
                        //Update offset
                        player.offsetY = (int) player.sprite.getHeight();
                        player.offsetX = 25;
                        //Update player mask
                        player.mask.setHeigth((int) player.sprite.getHeight());
                    } else {
                        player.sprite = SpriteBank.get(playerTeam + "_player_die_left");
                        //Update offset
                        player.offsetY = (int) player.sprite.getHeight();
                        player.offsetX = 20;
                        //Update player mask
                        player.mask.setHeigth((int) player.sprite.getHeight());
                    }
                    player.imageSpeed = .1;
                    player.imageIndex = 0;
                    //TODO : BROADCASTING FORMAT: x, y, shooterId
                    broadcast(Message.CREATE_PARTICLE + "/" +
                            player.x + "," +
                            player.y + "," +
                            lasthitId
                    );

                }

                //Invest
                if (playerScore > 0) {
                    if (playerTeam.equals("blue")) {
                        if (player.collides(blueBank.mask.getBounds())) {
                            if (keyPressed(Settings.key("action"))) {
                                blueBank.isActivated = true;
                                if (blueBank.actionBar < blueBank.actionBarMax) {
                                    blueBank.actionBar++;
                                } else {
                                    audioPlayer.get("insert").play();
                                    blueScore += playerScore;
                                    //TODO : BROADCASTING FORMAT: x, y, shooterId, direction
                                    broadcast(Message.ADD_SCORE + "/" +
                                            playerScore + "," +
                                            playerTeam
                                    );
                                    playerScore = 0;
                                    blueBank.actionBar = 0;
                                }
                            } else {
                                blueBank.isActivated = false;
                                blueBank.actionBar = 0;
                            }
                        } else {
                            blueBank.isActivated = false;
                        }
                    } else {
                        if (player.collides(orangeBank.mask.getBounds())) {
                            if (keyPressed(Settings.key("action"))) {
                                orangeBank.isActivated = true;
                                if (orangeBank.actionBar < orangeBank.actionBarMax) {
                                    orangeBank.actionBar++;
                                } else {
                                    audioPlayer.get("insert").play();
                                    orangeScore += playerScore;
                                    //TODO : BROADCASTING FORMAT: x, y, shooterId, direction
                                    broadcast(Message.ADD_SCORE + "/" +
                                            playerScore + "," +
                                            playerTeam
                                    );
                                    playerScore = 0;
                                    orangeBank.actionBar = 0;
                                }
                            } else {
                                orangeBank.isActivated = false;
                                orangeBank.actionBar = 0;
                            }
                        } else {
                            orangeBank.isActivated = false;
                        }
                    }
                } else {
                    blueBank.isActivated = false;
                    orangeBank.isActivated = false;
                }
            }


            //Update entities
            updatePlayers();
            blueBank.update();
            orangeBank.update();
            updateBullets();
            updateParticles();

            //Broadcast player movement and animation
            //TODO : BROADCASTING FORMAT: id, x, y, hp, dead, fade
            broadcast(Message.MOVE_PLAYER + "/" +
                    playerId + "," +
                    player.x + "," +
                    player.y + "," +
                    player.hp + "," +
                    player.dead + "," +
                    player.fade + "," +
                    playerScore
            );
            //TODO : BROADCASTING FORMAT: id, sprite, offsetX, imageIndex, imageSpeed
            broadcast(Message.ANIMATE_PLAYER + "/" +
                    playerId + "," +
                    player.sprite.getName() + "," +
                    player.offsetX + "," +
                    player.imageIndex + "," +
                    player.imageSpeed
            );
        }

        //Render
        render();

        //Check if game is over
        if (blueScore >= maxScore) {
            if (playerTeam.equals("blue")) {
                gameWon = true;
            } else {
                gameLost = true;
            }
        } else if (orangeScore >= maxScore) {
            if (playerTeam.equals("orange")) {
                gameWon = true;
            } else {
                gameLost = true;
            }
        }

        //Exit
        if (gameWon || gameLost) {
            if (restartTimer > 0) {
                restartTimer--;
            } else {
                //Restart
                restartTimer = restartTime;
                quit();
            }
        }

        //Music
        if (!audioPlayer.get(playlist.playing()).isPlaying()) {
            audioPlayer.get(playlist.next()).play();
        }
    }

    private void quit() {
        networker.terminate();
        Platform.exit();
    }

    private void broadcast(String str) {
        networker.sendMessage(str);
    }

    private void updateParticles() {
        for (int i = 0; i < particles.size(); i++) {
            BoundingBox temp = new BoundingBox(
                    particles.get(i).mask.getX(),
                    particles.get(i).mask.getY() + 1,
                    particles.get(i).mask.getWidth(),
                    particles.get(i).mask.getHeigth());
            if (Helper.placeMeeting(temp, walls)) {
                particles.get(i).verSpeed = 0;
            } else {
                //Gravity
                if (particles.get(i).verSpeed < particles.get(i).maxVerSpeed) {
                    particles.get(i).verSpeed += particles.get(i).gravity;
                }
            }

            //Vertical collisions
            //Move bbox of particle to a pos where it could collide a wall
            temp = new BoundingBox(
                    particles.get(i).mask.getX(),
                    particles.get(i).mask.getY() + particles.get(i).verSpeed,
                    particles.get(i).mask.getWidth(),
                    particles.get(i).mask.getHeigth());
            if (Helper.placeMeeting(temp, walls)) {
                while (!Helper.placeMeeting(temp, walls)) {
                    particles.get(i).y += (int) Math.signum(particles.get(i).verSpeed);
                }
                particles.get(i).verSpeed = 0;
            }

            //Update y pos
            particles.get(i).y += particles.get(i).verSpeed;
            particles.get(i).update();

            //Check player collisions
            for (Player p : blueTeam) {
                if (particles.get(i).collides(p.mask.getBounds())) {
                    if (p.equals(player)) {
                        if (playerScore < playerMaxScore) {
                            particles.remove(particles.get(i));
                            playerScore++;
                            audioPlayer.get("pickup").play();
                            break;
                        }
                    }
                }
            }

            for (Player p : orangeTeam) {
                if (particles.get(i).collides(p.mask.getBounds())) {
                    if (p.equals(player)) {
                        if (playerScore < playerMaxScore) {
                            particles.remove(particles.get(i));
                            playerScore++;
                            audioPlayer.get("pickup").play();
                            break;
                        }
                    }
                }
            }
        }
    }

    private void updatePlayers() {
        for (Player p : blueTeam) {
            p.update();
        }

        for (Player p : orangeTeam) {
            p.update();
        }
    }

    private void updateBullets() {
        if (!bullets.isEmpty()) {
            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).update();
                if (Helper.placeMeeting(bullets.get(i).mask.getBounds(), walls)) {
                    bullets.remove(bullets.get(i));
                } else {
                    //Collide with other players
                    if (playerTeam.equals("blue")) {
                        for (Player p : orangeTeam) {
                            if (!p.dead && !p.fade) {
                                if (p.collides(bullets.get(i).mask.getBounds())) {
                                    if (p.hp - bullets.get(i).damage <= 0) {
                                        audioPlayer.get("kill").play();
                                    }
                                    bullets.remove(bullets.get(i));
                                    break;
                                }
                            }
                        }
                    } else {
                        for (Player p : blueTeam) {
                            if (!p.dead && !p.fade) {
                                if (p.collides(bullets.get(i).mask.getBounds())) {
                                    if (p.hp - bullets.get(i).damage <= 0) {
                                        audioPlayer.get("kill").play();
                                    }
                                    bullets.remove(bullets.get(i));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!enemyBullets.isEmpty()) {
            for (int i = 0; i < enemyBullets.size(); i++) {
                //What is this for? so that IDEA doesn't highlight this code as duplicate. I already know it's a duplicate
                hashCode();
                enemyBullets.get(i).update();
                if (Helper.placeMeeting(enemyBullets.get(i).mask.getBounds(), walls)) {
                    enemyBullets.remove(enemyBullets.get(i));
                } else {
                    //Collide with our team players
                    if (playerTeam.equals("blue")) {
                        for (Player p : blueTeam) {
                            if (!p.dead && !p.fade) {
                                if (p.collides(enemyBullets.get(i).mask.getBounds())) {
                                    if (p.equals(player)) {
                                        p.hp -= (int) enemyBullets.get(i).damage;
                                        lasthitId = enemyBullets.get(i).shooter.id;
                                    }
                                    enemyBullets.remove(enemyBullets.get(i));
                                    break;
                                }
                            }
                        }
                    } else {
                        for (Player p : orangeTeam) {
                            if (!p.dead && !p.fade) {
                                if (p.collides(enemyBullets.get(i).mask.getBounds())) {
                                    if (p.equals(player)) {
                                        p.hp -= (int) enemyBullets.get(i).damage;
                                        lasthitId = enemyBullets.get(i).shooter.id;
                                    }
                                    enemyBullets.remove(enemyBullets.get(i));
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void render() {
        //Render
        canvas.getGraphicsContext2D().clearRect(0, 0, levelWidth, levelHeight);
        canvas.getGraphicsContext2D().setFill(Color.SPRINGGREEN);
        canvas.getGraphicsContext2D().fillRect(0, 0, levelWidth, levelHeight);
        canvas.getGraphicsContext2D().drawImage(bg, 0, 0);
        blueBank.draw(canvas.getGraphicsContext2D());
        orangeBank.draw(canvas.getGraphicsContext2D());
        drawBullets();
        drawParticles();
        drawPlayers();

        //Control view
        //X change
        if (player.x < viewWidth / 2) {
            gameRoot.setTranslateX(0);
        } else if (player.x > levelWidth - viewWidth / 2) {
            gameRoot.setTranslateX(viewWidth - levelWidth);
        } else {
            gameRoot.setTranslateX(viewWidth / 2 - player.x);
        }
        //Y change
        if (player.y < viewHeight / 2) {
            gameRoot.setTranslateY(0);
        } else if (player.y > levelHeight - viewHeight / 2) {
            gameRoot.setTranslateY(viewHeight - levelHeight);
        } else {
            gameRoot.setTranslateY(viewHeight / 2 - player.y);
        }

        //Render ui
        drawUi();
    }

    private void drawUi() {
        ui.getGraphicsContext2D().clearRect(0, 0, viewWidth, viewHeight);

        //TODO : Gonna be a lot of random values, cleanup after setting the view
        //Blue stats
        //Blue score bar
        ui.getGraphicsContext2D().setFill(Color.DARKGRAY);
        ui.getGraphicsContext2D().fillRect(72, 28, 327, 16);

        int amountBlue = 0;
        if (blueScore <= maxScore) {
            amountBlue = 323 * blueScore / maxScore;
        } else {
            amountBlue = 323;
        }

        ui.getGraphicsContext2D().setFill(Color.DARKBLUE);
        ui.getGraphicsContext2D().fillRect(74 + 323 - amountBlue, 30, amountBlue, 12);

        //Blue score rect
        ui.getGraphicsContext2D().setFill(Color.DARKGRAY);
        ui.getGraphicsContext2D().fillRect(399, 28, 64, 64);
        ui.getGraphicsContext2D().setFill(Color.GRAY);
        ui.getGraphicsContext2D().fillRect(401, 30, 60, 60);

        //Bridge
        ui.getGraphicsContext2D().setFill(Color.LIGHTGRAY);
        ui.getGraphicsContext2D().fillRect(463, 28, 34, 32);

        //Orange stats
        //Orange score rect
        ui.getGraphicsContext2D().setFill(Color.DARKGRAY);
        ui.getGraphicsContext2D().fillRect(497, 28, 64, 64);
        ui.getGraphicsContext2D().setFill(Color.GRAY);
        ui.getGraphicsContext2D().fillRect(499, 30, 60, 60);

        //Orange score bar
        ui.getGraphicsContext2D().setFill(Color.DARKGRAY);
        ui.getGraphicsContext2D().fillRect(561, 28, 327, 16);

        int amountOrange = 0;
        if (blueScore <= maxScore) {
            amountOrange = 323 * orangeScore / maxScore;
        } else {
            amountOrange = 323;
        }

        ui.getGraphicsContext2D().setFill(Color.DARKORANGE);
        ui.getGraphicsContext2D().fillRect(563, 30, amountOrange, 12);

        //Player stats
        //Player score
        ui.getGraphicsContext2D().setFill(Color.DARKGRAY);
        ui.getGraphicsContext2D().fillRect(18, 458, 64, 64);
        ui.getGraphicsContext2D().setFill(Color.GRAY);
        ui.getGraphicsContext2D().fillRect(20, 460, 60, 60);

        //Player hp
        ui.getGraphicsContext2D().setFill(Color.DARKGRAY);
        ui.getGraphicsContext2D().fillRect(82, 506, 215, 16);

        int amountHp = 211 * player.hp / player.maxHp;

        ui.getGraphicsContext2D().setFill(Color.RED);
        ui.getGraphicsContext2D().fillRect(83, 508, amountHp, 12);

        //Text display
        ui.getGraphicsContext2D().setFont(stats);
        ui.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
        ui.getGraphicsContext2D().setTextBaseline(VPos.CENTER);
        ui.getGraphicsContext2D().setFill(Color.WHITE);

        //Blue score
        ui.getGraphicsContext2D().fillText(Integer.toString(blueScore), 431, 60);

        //Orange score
        ui.getGraphicsContext2D().fillText(Integer.toString(orangeScore), 529, 60);

        //Player score
        ui.getGraphicsContext2D().fillText(Integer.toString(playerScore), 50, 490);

        //Player name
        ui.getGraphicsContext2D().setTextAlign(TextAlignment.LEFT);
        ui.getGraphicsContext2D().fillText(playerName, 88, 481);

        //Draw respawn timer
        if (player.dead) {
            ui.getGraphicsContext2D().setFill(Color.BLACK);
            ui.getGraphicsContext2D().setGlobalAlpha(.5);
            ui.getGraphicsContext2D().fillRect( 0, 0, viewWidth, viewHeight);
            ui.getGraphicsContext2D().setGlobalAlpha(1);
            ui.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
            ui.getGraphicsContext2D().setFill(Color.WHITE);
            ui.getGraphicsContext2D().fillText("You are dead!", viewWidth / 2, viewHeight / 2  - 15);
            ui.getGraphicsContext2D().setFill(Color.LIGHTGRAY);
            ui.getGraphicsContext2D().fillText("Respawn in " + (respawnTimer / 60 + 1) + "s", viewWidth / 2, viewHeight / 2 + 15);
        }

        //Draw action text
        if (playerScore > 0) {
            if (playerTeam.equals("blue")) {
                if (player.collides(blueBank.mask.getBounds())) {
                    ui.getGraphicsContext2D().setFill(Color.WHITE);
                    ui.getGraphicsContext2D().fillText("Press 'E' to store Particles", viewWidth / 2, viewHeight - 30);
                }
            } else {
                if (player.collides(orangeBank.mask.getBounds())) {
                    ui.getGraphicsContext2D().setFill(Color.WHITE);
                    ui.getGraphicsContext2D().fillText("Press 'E' to store Particles", viewWidth / 2, viewHeight - 30);
                }
            }
        }

        if (gameWon) {
            ui.getGraphicsContext2D().setFill(Color.BLACK);
            ui.getGraphicsContext2D().setGlobalAlpha(.5);
            ui.getGraphicsContext2D().fillRect( 0, 0, viewWidth, viewHeight);
            ui.getGraphicsContext2D().setGlobalAlpha(1);
            ui.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
            ui.getGraphicsContext2D().setFill(Color.WHITE);
            ui.getGraphicsContext2D().fillText("VICTORY!", viewWidth / 2, viewHeight / 2 - 15);
            ui.getGraphicsContext2D().setFill(Color.LIGHTGRAY);
            ui.getGraphicsContext2D().fillText("Exiting in " + (restartTimer / 60 + 1) + "s", viewWidth / 2, viewHeight / 2 + 15);
        } else if (gameLost) {
            ui.getGraphicsContext2D().setFill(Color.BLACK);
            ui.getGraphicsContext2D().setGlobalAlpha(.5);
            ui.getGraphicsContext2D().fillRect( 0, 0, viewWidth, viewHeight);
            ui.getGraphicsContext2D().setGlobalAlpha(1);
            ui.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
            ui.getGraphicsContext2D().setFill(Color.WHITE);
            ui.getGraphicsContext2D().fillText("DEFEAT!", viewWidth / 2, viewHeight / 2 - 15);
            ui.getGraphicsContext2D().setFill(Color.LIGHTGRAY);
            ui.getGraphicsContext2D().fillText("Exiting in " + (restartTimer / 60 + 1) + "s", viewWidth / 2, viewHeight / 2 + 15);
        }
    }

    private void drawParticles() {
        for (Particle p : particles) {
            p.draw(canvas.getGraphicsContext2D());
        }
    }

    private void drawPlayers() {
        for (Player p : blueTeam) {
            if (!p.dead) {
                p.draw(canvas.getGraphicsContext2D());
                if (!p.equals(player) && !p.fade) {
                    p.drawHealthbar(canvas.getGraphicsContext2D());
                    p.drawName(canvas.getGraphicsContext2D());
                }
            }
        }

        for (Player p : orangeTeam) {
            if (!p.dead) {
                p.draw(canvas.getGraphicsContext2D());
                if (!p.equals(player) && !p.fade) {
                    p.drawHealthbar(canvas.getGraphicsContext2D());
                    p.drawName(canvas.getGraphicsContext2D());
                }
            }
        }
    }

    private void drawBullets() {
        for (Bullet b : bullets) {
            b.draw(canvas.getGraphicsContext2D());
        }
        for (Bullet b : enemyBullets) {
            b.draw(canvas.getGraphicsContext2D());
        }
    }

    private void drawWalls() {
        canvas.getGraphicsContext2D().setStroke(Color.GREEN);
        for (BoundingBox wall : walls) {
            canvas.getGraphicsContext2D().strokeRect(wall.getMinX(), wall.getMinY(), wall.getWidth(), wall.getHeight());
        }
    }

    private boolean keyPressed(KeyCode key) {
        return input.getOrDefault(key, false);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Init settings, load sprites
        Settings.init();
        SpriteBank.init();
        audioPlayer = new AudioPlayer();
        audioPlayer.init();

        TextInputDialog d = new TextInputDialog("localhost:3456");
        d.setTitle("Enter server address");
        d.setContentText("Server Address");
        Optional<String> address = d.showAndWait();
        while (!address.isPresent()) {
            address = d.showAndWait();
        }
        serverAddress = address.get();

        d = new TextInputDialog("Player");
        d.setTitle("Enter Username");
        d.setContentText("Username");
        Optional<String> result = d.showAndWait();
        while (!result.isPresent()) {
            result = d.showAndWait();
        }
        playerName = result.get();

        initMenu();

        //Fx scenes initialization
        menuScene = new Scene(menuRoot, viewWidth, viewHeight);
        menuScene.setOnKeyPressed(event -> input.put(event.getCode(), true));
        menuScene.setOnKeyReleased(event -> input.put(event.getCode(), false));

        loadingScene = new Scene(loadingRoot, viewWidth, viewHeight);

        gameScene = new Scene(appRoot, viewWidth, viewHeight);
        gameScene.setOnKeyPressed(event -> input.put(event.getCode(), true));
        gameScene.setOnKeyReleased(event -> input.put(event.getCode(), false));
        primaryStage.setMinWidth(appWidth);
        primaryStage.setMinHeight(appHeight);
        primaryStage.setMaxWidth(appWidth);
        primaryStage.setMaxHeight(appHeight);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Gambit");
        primaryStage.setOnCloseRequest(event -> {
            if (networker != null) {
                networker.terminate();
            }
        });
        primaryStage.setScene(menuScene);
        primaryStage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                //Game states
                switch (gameState) {
                    //Main menu
                    case 0:
                        primaryStage.setScene(menuScene);
                        menuUpdate();
                        break;

                    //Loading and connecting state
                    //No game updates or renders while teams haven't been formed
                    case 1:
                        primaryStage.setScene(loadingScene);
                        loadingUpdate();
                        break;

                    //Game itself
                    case 2:
                        primaryStage.setScene(gameScene);
                        update();
                        break;
                }
            }
        }.start();
    }

    private void loadingUpdate() {
        //Update settings
        animationMap.get("loading").play();

        if (playerTeam != null && playerId != 0) {
            //Notify server when ready
            networker.sendMessage(Message.READY);
        }

        loadingRender();

        if (serverReady) {
            closeLoading();
            initGame();
            gameState = 2;
        }

        if (rollback) {
            rollback = false;
            revert();
        }
    }

    private void loadingRender() {
        loadingCanvas.getGraphicsContext2D().clearRect(0, 0, levelWidth, levelHeight);
        animationMap.get("loading").draw(loadingCanvas.getGraphicsContext2D());
    }

    private void menuUpdate() {

        if (animationTimer < animationTimerStop) {
            animationTimer += 1;
        } else {
            animationTimer = animationTimerStop;
        }
        for (String key : animationClock.keySet()) {
            if (animationTimer >= animationClock.get(key)) {
                animationMap.get(key).play();
            }
        }

        menuRender();

        if (keyPressed(Settings.key("shoot"))) {
            try {
                initNetwork();
                gameState = 1;
                closeMenu();
                initLoading();
            } catch (IOException e) {
                gameState = 0;
            }
        }
    }

    private void menuRender() {
        menuCanvas.getGraphicsContext2D().drawImage(menuBg, 0, 0);
        for (String key : animationMap.keySet()) {
            animationMap.get(key).draw(menuCanvas.getGraphicsContext2D());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    //Network

    public void serverReady() {
        serverReady = true;
    }

    public void setPlayerTeam(String team) {
        playerTeam = team;
    }

    public void setPlayerId(int id) {
        playerId = id;
    }

    public void createPlayer(String args) {
        //Format: id, team, x, y, imageSpeed, maskOffsetX, maskOffsetY, name
        String[] a = args.split(",");
        if (a[1].equals("blue")) {
            blueTeam.add(new Player(
                    a[7],
                    Integer.parseInt(a[0]),
                    a[1],
                    Integer.parseInt(a[2]),
                    Integer.parseInt(a[3]),
                    SpriteBank.get("blue_player_idle"),
                    0,
                    Double.parseDouble(a[4]),
                    new Mask(Integer.parseInt(a[2]), Integer.parseInt(a[3]), 30, 71),
                    Integer.parseInt(a[5]),
                    Integer.parseInt(a[6])
            ));
        } else {
            orangeTeam.add(new Player(
                    a[7],
                    Integer.parseInt(a[0]),
                    a[1],
                    Integer.parseInt(a[2]),
                    Integer.parseInt(a[3]),
                    SpriteBank.get("orange_player_idle"),
                    0,
                    Double.parseDouble(a[4]),
                    new Mask(Integer.parseInt(a[2]), Integer.parseInt(a[3]), 30, 71),
                    Integer.parseInt(a[5]),
                    Integer.parseInt(a[6])
            ));
        }
    }

    public void createBullet(String args) {
        //Format: x, y, shooterId, direction
        String[] a = args.split(",");
        if (Math.abs(Integer.parseInt(a[0]) - player.x) < viewWidth) {
            audioPlayer.get("other_shoot").play();
        }
        for (Player p : blueTeam) {
            if (p.id == Integer.parseInt(a[2])) {
                if (p.team.equals(playerTeam)) {
                    bullets.add(new Bullet(
                            Integer.parseInt(a[0]),
                            Integer.parseInt(a[1]),
                            Integer.parseInt(a[3]),
                            p,
                            SpriteBank.get("blue_bullet")
                    ));
                    break;
                } else {
                    enemyBullets.add(new Bullet(
                            Integer.parseInt(a[0]),
                            Integer.parseInt(a[1]),
                            Integer.parseInt(a[3]),
                            p,
                            SpriteBank.get("blue_bullet")
                    ));
                    break;
                }
            }
        }

        for (Player p : orangeTeam) {
            if (p.id == Integer.parseInt(a[2])) {
                if (p.team.equals(playerTeam)) {
                    bullets.add(new Bullet(
                            Integer.parseInt(a[0]),
                            Integer.parseInt(a[1]),
                            Integer.parseInt(a[3]),
                            p,
                            SpriteBank.get("orange_bullet")
                    ));
                    break;
                } else {
                    enemyBullets.add(new Bullet(
                            Integer.parseInt(a[0]),
                            Integer.parseInt(a[1]),
                            Integer.parseInt(a[3]),
                            p,
                            SpriteBank.get("orange_bullet")
                    ));
                    break;
                }
            }
        }
    }

    public void createParticle(String args) {
        //Format: x, y, shooterId
        String[] a = args.split(",");
        if (Integer.parseInt(a[2]) == playerId) {
            particles.add(new Particle(
                    Integer.parseInt(a[0]),
                    Integer.parseInt(a[1]),
                    SpriteBank.get("particle"),
                    0,
                    .1,
                    new Mask(Integer.parseInt(a[0]), Integer.parseInt(a[1]), 16, 16),
                    8,
                    0
            ));
        }
    }

    public void animatePlayer(String args) {
        //Format: id, sprite, offsetX, imageIndex, imageSpeed
        String[] a = args.split(",");
        for (Player p : blueTeam) {
            if (p.id == Integer.parseInt(a[0])) {
                p.sprite = SpriteBank.get(a[1]);
                //Update offset
                p.offsetY = (int)p.sprite.getHeight();
                p.offsetX = Integer.parseInt(a[2]);
                //Update player mask
                p.mask.setHeigth((int)p.sprite.getHeight());
                p.imageIndex = Double.parseDouble(a[3]);
                p.imageSpeed = Double.parseDouble(a[4]);
                break;
            }
        }

        for (Player p : orangeTeam) {
            if (p.id == Integer.parseInt(a[0])) {
                p.sprite = SpriteBank.get(a[1]);
                //Update offset
                p.offsetY = (int)p.sprite.getHeight();
                p.offsetX = Integer.parseInt(a[2]);
                //Update player mask
                p.mask.setHeigth((int)p.sprite.getHeight());
                p.imageIndex = Double.parseDouble(a[3]);
                p.imageSpeed = Double.parseDouble(a[4]);
                break;
            }
        }
    }

    public void movePlayer(String args) {
        //TODO : rename this to updatePlayer()
        //Format: id, x, y, hp, dead, fade, particles
        String[] a = args.split(",");
        for (Player p : blueTeam) {
            if (p.id == Integer.parseInt(a[0])) {
                p.x = Integer.parseInt(a[1]);
                p.y = Integer.parseInt(a[2]);
                p.hp = Integer.parseInt(a[3]);
                p.dead = Boolean.parseBoolean(a[4]);
                p.fade = Boolean.parseBoolean(a[5]);
                p.particles = Integer.parseInt(a[6]);
                break;
            }
        }

        for (Player p : orangeTeam) {
            if (p.id == Integer.parseInt(a[0])) {
                p.x = Integer.parseInt(a[1]);
                p.y = Integer.parseInt(a[2]);
                p.y = Integer.parseInt(a[2]);
                p.hp = Integer.parseInt(a[3]);
                p.dead = Boolean.parseBoolean(a[4]);
                p.fade = Boolean.parseBoolean(a[5]);
                p.particles = Integer.parseInt(a[6]);
                break;
            }
        }
    }

    public void removePlayer(String args) {
        //Format: id
        for (Player p : blueTeam) {
            if (p.id == Integer.parseInt(args)) {
                blueTeam.remove(p);
                break;
            }
        }

        for (Player p : orangeTeam) {
            if (p.id == Integer.parseInt(args)) {
                orangeTeam.remove(p);
                break;
            }
        }
    }

    public void addScore(String args) {
        //Format: score, team
        String a[] = args.split(",");
        if (a[1].equals("blue")) {
            blueScore += Integer.parseInt(a[0]);
        } else {
            orangeScore += Integer.parseInt(a[0]);
        }
    }

    public void revert() {
        System.out.println("oops, revert!!");
        closeLoading();
        initMenu();
        gameState = 0;
        networker.terminate();
    }

    public void rollback() {
        rollback = true;
    }
}
