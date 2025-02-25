package src.test.app;

import org.junit.jupiter.api.Test;
import src.main.app.Server;
import src.main.app.ClientHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

class ServerLocalTest {

    @Test
    void testServerInitialization() throws IOException {
        int testPort = 2424;
        Server server = new Server(testPort);

        assertNotNull(server, "Server instance should not be null");
        assertEquals(testPort, server.getPort(), "Server should listen on the correct port");
    }

    @Test
    void testAddAndRemoveUserClient() throws IOException {
        Server server = new Server(2424);

        // Add user
        String username = "testUser";
        ClientHandler handler = new ClientHandler(null, server); // Simulate a real ClientHandler
        server.addUserClient(username, handler);

        // Check if user is added
        assertTrue(server.isUserLoggedIn(username), "User should be logged in");

        // Remove user
        server.removeUserClient(username);
        assertFalse(server.isUserLoggedIn(username), "User should be logged out");
    }

    @Test
    void testSendToUser() throws IOException {
        Server server = new Server(2424);
        String username = "testUser";

        // Create a handler that captures messages
        TestClientHandler handler = new TestClientHandler();
        server.addUserClient(username, handler);

        // Send message to user
        String message = "Hello!";
        server.sendToUser(username, message);

        // Verify the message was received
        assertEquals(message, handler.getLastMessage(), "User should receive the correct message");
    }

    @Test
    void testBroadcast() throws IOException {
        Server server = new Server(2424);

        // Create handlers for multiple users
        TestClientHandler handler1 = new TestClientHandler();
        TestClientHandler handler2 = new TestClientHandler();

        server.addUserClient("user1", handler1);
        server.addUserClient("user2", handler2);

        // Broadcast a message
        String broadcastMessage = "Hello, everyone!";
        server.broadcast(broadcastMessage);

        // Verify all users received the broadcast
        assertEquals(broadcastMessage, handler1.getLastMessage(), "User1 should receive the broadcast");
        assertEquals(broadcastMessage, handler2.getLastMessage(), "User2 should receive the broadcast");
    }

    // Helper class to simulate ClientHandler
    static class TestClientHandler extends ClientHandler {
        private String lastMessage;

        public TestClientHandler() {
            super(null, null); // Pass nulls since we won't actually use sockets here
        }

        @Override
        public void sendMessage(String message) {
            this.lastMessage = message;
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }
}
