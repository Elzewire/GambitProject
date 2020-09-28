package core.networking;

import core.Game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Networker {
    //All networking in this class
    private int PORT;
    private String HOST;
    private Game game;
    private Thread thread;

    private Socket server;

    public Networker(Game game) throws IOException {
        this.game = game;
        this.HOST = game.serverAddress.split(":")[0];
        this.PORT = Integer.parseInt(game.serverAddress.split(":")[1]);
        this.server = new Socket(HOST, PORT);
        this.thread = new Thread(this::receiveMessage);
        this.thread.start();
    }

    public void sendMessage(String string) {
        try {
            PrintWriter writer = new PrintWriter(server.getOutputStream());
            writer.println(string);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void receiveMessage() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
            String string = reader.readLine();
            while (string != null) {
                //Decode string
                //System.out.println(string);
                String message = string.split("/")[0];
                switch (message) {
                    case Message.CREATE_PLAYER:
                        game.createPlayer(string.split("/")[1]);
                        break;

                    case Message.CREATE_BULLET:
                        game.createBullet(string.split("/")[1]);
                        break;

                    case Message.CREATE_PARTICLE:
                        game.createParticle(string.split("/")[1]);
                        break;

                    case Message.ANIMATE_PLAYER:
                        game.animatePlayer(string.split("/")[1]);
                        break;

                    case Message.MOVE_PLAYER:
                        game.movePlayer(string.split("/")[1]);
                        break;

                    case Message.REMOVE_PLAYER:
                        game.removePlayer(string.split("/")[1]);
                        break;

                    case Message.ADD_SCORE:
                        game.addScore(string.split("/")[1]);
                        break;

                    case Message.ID:
                        game.setPlayerId(Integer.parseInt(string.split("/")[1]));
                        break;

                    case Message.TEAM_BLUE:
                        game.setPlayerTeam("blue");
                        break;

                    case Message.TEAM_ORANGE:
                        game.setPlayerTeam("orange");
                        break;

                    case Message.READY:
                        game.serverReady();
                        break;

                    case Message.LOBBY_FULL:
                        game.rollback();
                        break;
                }
                string = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void terminate() {
        System.out.println("terminating");
        sendMessage(Message.TERMINATE);
        thread.stop();
    }
}
