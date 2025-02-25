package src.main.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server
 *
 * Main server class that listens for incoming client connections
 * and manages connected clients.
 *
 * @version 12/08/2024
 * @author Madhavan Prasanna, Rohan Uddaraju
 */

public class Server {
    private ServerSocket serverSocket;
    private ConcurrentHashMap<String, ClientHandler> userClientMap;

    public Server(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        userClientMap = new ConcurrentHashMap<>();
        System.out.println("Server started on port " + port);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());

            ClientHandler handler = new ClientHandler(clientSocket, this);
            new Thread(handler).start();
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }


    /**
     * Checks if a user is currently logged in.
     *
     * @param username The username to check.
     * @return True if the user is logged in; otherwise, false.
     */
    public boolean isUserLoggedIn(String username) {
        return userClientMap.containsKey(username);
    }

    /**
     * Adds a user and their ClientHandler to the userClientMap.
     *
     * @param username The username of the user.
     * @param handler  The ClientHandler associated with the user.
     */
    public void addUserClient(String username, ClientHandler handler) {
        userClientMap.put(username, handler);
        System.out.println("User logged in: " + username);
    }

    /**
     * Removes a user from the userClientMap.
     *
     * @param username The username of the user to remove.
     */
    public void removeUserClient(String username) {
        userClientMap.remove(username);
        System.out.println("User logged out: " + username);
    }

    /**
     * Sends a message to a specific user.
     *
     * @param username The recipient's username.
     * @param message  The message to send.
     */
    public void sendToUser(String username, String message) {
        ClientHandler handler = userClientMap.get(username);
        if (handler != null) {
            handler.sendMessage(message);
            System.out.println("Sent to " + username + ": " + message);
        } else {
            System.out.println("User " + username + " is not online.");
        }
    }

    /**
     * Broadcasts a message to all connected clients.
     *
     * @param message The message to broadcast.
     */
    public void broadcast(String message) {
        for (ClientHandler handler : userClientMap.values()) {
            handler.sendMessage(message);
        }
        System.out.println("Broadcasted message: " + message);
    }

    /**
     * Entry point for the server application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        int port = 2424;
        try {
            new Server(port);
        } catch (IOException e) {
            System.err.println("Failed to start the server: " + e.getMessage());
        }
    }
}
