package src.test.app;

import org.junit.jupiter.api.Test;
import src.main.app.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

class ClientLocalTest {

    @Test
    void testClientInitialization() throws IOException {
        String host = "localhost";
        int port = 2424;

        // Create a mock server to simulate connection
        ServerSocket serverSocket = new ServerSocket(port);
        Client.ClientListener listener = message -> {};

        Client client = new Client(host, port, listener);

        // Test if the socket is initialized
        assertNotNull(client, "Client instance should not be null");
        assertTrue(client.getSocket().isConnected(), "Client socket should be connected");

        client.close();
        serverSocket.close();
    }

    @Test
    void testRunMethod() throws IOException {
        String host = "localhost";
        int port = 2424;

        // Create a mock server to simulate connection
        ServerSocket serverSocket = new ServerSocket(port);
        Client.ClientListener listener = message -> {};

        Client client = new Client(host, port, listener);

        // Start the client thread
        Thread clientThread = new Thread(client);
        clientThread.start();

        // Verify the thread is alive
        assertTrue(clientThread.isAlive(), "Client thread should be running");

        client.close();
        serverSocket.close();
    }

    @Test
    void testSendMessage() throws IOException {
        String host = "localhost";
        int port = 2424;

        // Create a mock server to accept the connection
        ServerSocket serverSocket = new ServerSocket(port);
        Client.ClientListener listener = message -> {};

        Client client = new Client(host, port, listener);

        // Test sending a message
        client.sendMessage("Test message");

        // The actual test for receiving would require integration with a server
        // This test ensures no exceptions or errors occur during sendMessage

        client.close();
        serverSocket.close();
    }

    @Test
    void testCloseMethod() throws IOException {
        String host = "localhost";
        int port = 2424;

        // Create a mock server to simulate connection
        ServerSocket serverSocket = new ServerSocket(port);
        Client.ClientListener listener = message -> {};

        Client client = new Client(host, port, listener);
        client.close();

        // Verify the socket is closed
        assertTrue(client.getSocket().isClosed(), "Client socket should be closed");

        serverSocket.close();
    }
}
