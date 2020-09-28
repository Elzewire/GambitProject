package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server_old {
    private final int PORT = 3456;
    private ArrayList<Connection> connections;
    private int id = 117;
    private int teamCapacity = 1;
    private int teamBlue = 0;
    private int teamOrange = 0;
    private String next = "blue";
    private ServerSocket serverSocket;
    private boolean started = false;

    public Server_old(int port, int teamCapacity) throws IOException {
        this.teamCapacity = teamCapacity;
        this.serverSocket = new ServerSocket(port);
        this.connections = new ArrayList<>();
    }

    public void start() throws IOException {
        while (true) {
            if (!started) {
                Socket client = serverSocket.accept();
                //connections.add(new Connection(this, client, getId(), Message.TEAM_BLUE));
            }
        }
    }

    public String sendTeam() {
        if (next.equals("blue")) {
            if (teamBlue < teamCapacity) {
                teamBlue++;
                next = "orange";
                return Message.TEAM_BLUE;
            }
        } else {
            if (teamOrange < teamCapacity) {
                teamOrange++;
                next = "blue";
                return Message.TEAM_ORANGE;
            }
        }
        return Message.LOBBY_FULL;
    }

    public int getId() {
        return id++;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter server port: ");
        int port = sc.nextInt();
        System.out.print("Enter team capacity: ");
        Server_old server = new Server_old(port, sc.nextInt());
        server.start();
    }

    public void broadcast(String string, Connection connection) {
        System.out.println("blue" + teamBlue + ", orange" + teamOrange);
        for (Connection conn : connections) {
            if (conn != connection) {
                conn.send(string);
            }
        }
    }

    public void notifyReady(String team, int id) {
        if (lobbyIsFull() && connectionsReady()) {
            //Notify all that game is starting
            for (Connection conn : connections) {
                conn.send(Message.READY);
            }
            started = true;
        }
    }

    private boolean connectionsReady() {
        for (Connection conn : connections) {
            if (!conn.isReady()) {
                return false;
            }
        }
        return true;
    }

    private boolean lobbyIsFull() {
        return teamOrange == teamCapacity && teamBlue == teamCapacity;
    }

    public void freeBlueTeam() {
        System.out.println("blue team has a slot");
        teamBlue--;
        if (teamBlue == 0 && teamOrange == 0) {
            started = false;
        }
    }

    public void freeOrangeTeam() {
        System.out.println("orange team has a slot");
        teamOrange--;
        if (teamBlue == 0 && teamOrange == 0) {
            started = false;
        }
    }

    public void removeConn(Connection connection) {
        connections.remove(connection);
    }

}
