package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    private final int PORT = 3456;
    private ArrayList<Connection> connections;
    private int id = 117;
    private int teamCapacity = 1;
    private ArrayList<Connection> teamBlue;
    private ArrayList<Connection> teamOrange;
    private ServerSocket serverSocket;
    private boolean started = false;

    public Server(int port, int teamCapacity) throws IOException {
        this.teamCapacity = teamCapacity;
        InetAddress addr1 = InetAddress.getByName("25.72.50.241");
        ServerSocket sock = new ServerSocket(port, 50, addr1);
        this.serverSocket = new ServerSocket(port);
        this.connections = new ArrayList<>();
        this.teamBlue = new ArrayList<>();
        this.teamOrange = new ArrayList<>();
    }

    public void start() throws IOException {
        while (true) {
            Socket client = serverSocket.accept();
            if (!started) {
                if (teamBlue.size() < teamCapacity) {
                    teamBlue.add(new Connection(this, client, getId(), Message.TEAM_BLUE));
                } else if (teamOrange.size() < teamCapacity) {
                    teamOrange.add(new Connection(this, client, getId(), Message.TEAM_ORANGE));
                } else {
                    new Connection(this, client, true);
                }
            } else {
                new Connection(this, client, true);
            }
        }
    }

    public int getId() {
        return id++;
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter server port: ");
        int port = sc.nextInt();
        System.out.print("Enter team capacity: ");
        Server server = new Server(port, sc.nextInt());
        server.start();
    }

    public void broadcast(String string, Connection connection) {
        System.out.println("blue" + teamBlue + ", orange" + teamOrange);
        System.out.println(string);
        for (Connection conn : teamBlue) {
            if (conn != connection) {
                conn.send(string);
            }
        }
        for (Connection conn : teamOrange) {
            if (conn != connection) {
                conn.send(string);
            }
        }
    }

    public void notifyReady(String team, int id) {
        if (lobbyIsFull() && connectionsReady()) {
            //Notify all that game is starting
            for (Connection conn : teamBlue) {
                conn.send(Message.READY);
            }

            for (Connection conn : teamOrange) {
                conn.send(Message.READY);
            }
            started = true;
        }
    }

    private boolean connectionsReady() {
        for (Connection conn : teamBlue) {
            if (!conn.isReady()) {
                return false;
            }
        }
        for (Connection conn : teamOrange) {
            if (!conn.isReady()) {
                return false;
            }
        }
        return true;
    }

    private boolean lobbyIsFull() {
        return teamOrange.size() == teamCapacity && teamBlue.size() == teamCapacity;
    }


    public void removeConn(Connection connection) {
        if (teamBlue.contains(connection)) {
            teamBlue.remove(connection);
        } else if (teamOrange.contains(connection)) {
            teamOrange.remove(connection);
        }
        if (teamBlue.size() == 0 && teamOrange.size() == 0) {
            started = false;
        }
    }

}
