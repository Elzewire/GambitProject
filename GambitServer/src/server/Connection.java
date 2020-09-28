package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection implements Runnable  {
    private boolean reject;
    private Socket socket;
    private Thread thread;
    private Server server;
    private boolean ready;
    private String team;
    private int id;

    //For rejecting
    public Connection(Server server, Socket socket, boolean reject) {
        this.socket = socket;
        this.server = server;
        this.ready = false;
        this.reject = reject;

        thread = new Thread(this);
        thread.start();
    }

    public Connection(Server server, Socket socket, int id, String team) {
        this.socket = socket;
        this.server = server;
        this.id = id;
        this.team = team;
        this.ready = false;

        thread = new Thread(this);
        thread.start();
    }

    private void sendCredentials() {
        send(Message.sendID(id));
        send(team);
    }

    @Override
    public void run() {
        if (reject) {
            reject();
        } else {
            if (!ready) {
                sendCredentials();
            }

            //ReadMessages
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String string = reader.readLine();
                //TODO : parse string into an understandable message
                while (string != null) {
                    if (string.split("/")[0].equals(Message.READY)) {
                        ready = true;
                        server.notifyReady(team, id);
                    } else if (string.split("/")[0].equals(Message.TERMINATE)) {
                        server.broadcast(Message.REMOVE_PLAYER + "/" + id, this);
                        socket.close();
                        server.removeConn(this);
                    } else {
                        if (!string.split("/")[0].equals(Message.LOBBY_FULL) &&
                                !string.split("/")[0].equals(Message.TEAM_ORANGE) &&
                                !string.split("/")[0].equals(Message.TEAM_BLUE)) {
                            notifyServer(string, this);
                        }
                    }
                    string = reader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void reject() {
        send(Message.LOBBY_FULL);
    }

    private void notifyServer(String string, Connection connection) {
        server.broadcast(string, connection);
    }

    public Socket getSocket() {
        return socket;
    }

    public void send(String string) {
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.println(string);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        return ready;
    }

}
