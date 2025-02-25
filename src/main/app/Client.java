package src.main.app;

import java.io.*;
import java.net.Socket;

/**
 * Client
 *
 * Manages the connection to the server, sends messages, and listens for incoming messages.
 *
 * @version 12/08/2024
 * @author Madhavan Prasanna, Rohan Uddaraju
 */
public class Client implements Runnable {
    private String host;
    private int port;
    private ClientListener listener;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private volatile boolean running = true;

    public Client(String host, int port, ClientListener listener) throws IOException {
        this.host = host;
        this.port = port;
        this.listener = listener;
        connect();
    }

    public Socket getSocket() {
        return socket;
    }


    private void connect() throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                if (listener != null) {
                    listener.onMessageReceived(message);
                }
            }
        } catch (IOException e) {
            if (running) {
                e.printStackTrace();
                if (listener != null) {
                    listener.onMessageReceived("ERROR,Connection lost.");
                }
            }
        } finally {
            close();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    public void close() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface ClientListener {
        void onMessageReceived(String message);
    }

    public static void main(String[] args) {

    }
}
