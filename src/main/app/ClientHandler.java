package src.main.app;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClientHandler
 *
 * Handles communication between the server and a single client.
 * Processes client commands such as login, account creation, friend management,
 * and messaging. Ensures that actions are synchronized across all connected clients.
 *
 * @version 12/08/2024
 * @author Madhavan Prasanna, Rohan Uddaraju
 */
public class ClientHandler implements Runnable, ClientInterface {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private User currentUser;
    private Server server;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.currentUser = null;
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error initializing I/O streams: " + e.getMessage());
            closeConnections();
        }
    }

    @Override
    public void run() {
        try {
            String clientRequest;
            while ((clientRequest = in.readLine()) != null) {
                System.out.println("Received from client: " + clientRequest);
                String[] requestParts = clientRequest.split(",", 3);
                if (requestParts.length < 1) {
                    sendMessage("ERROR,Invalid command");
                    continue;
                }

                String command = requestParts[0].toUpperCase();

                switch (command) {
                    case "LOGIN":
                        handleLogin(requestParts);
                        break;
                    case "CREATE_ACCOUNT":
                        handleCreateAccount(requestParts);
                        break;
                    case "SEND_MESSAGE":
                        handleSendMessage(requestParts);
                        break;
                    case "ADD_FRIEND":
                        handleAddFriend(requestParts);
                        break;
                    case "APPROVE_FRIEND_REQUEST":
                        handleApproveFriendRequest(requestParts);
                        break;
                    case "REJECT_FRIEND_REQUEST":
                        handleRejectFriendRequest(requestParts);
                        break;
                    case "REMOVE_FRIEND":
                        handleRemoveFriend(requestParts);
                        break;
                    case "BLOCK_USER":
                        handleBlockUser(requestParts);
                        break;
                    case "UNBLOCK_USER":
                        handleUnblockUser(requestParts);
                        break;
                    case "SEARCH_USER":
                        handleUserSearch(requestParts);
                        break;
                    case "VIEW_USERS":
                        handleViewUsers(requestParts);
                        break;
                    case "VIEW_FRIENDS":
                        handleViewFriends(requestParts);
                        break;
                    case "VIEW_BLOCKED":
                        handleViewBlocked(requestParts);
                        break;
                    case "GET_USER_PROFILE":
                        handleGetUserProfile(requestParts);
                        break;
                    case "GET_MESSAGES":
                        handleGetMessages(requestParts);
                        break;
                    case "LOGOUT":
                        handleLogout();
                        break;
                    case "EXIT":
                        handleExit();
                        return;
                    default:
                        sendMessage("ERROR,Unknown command");
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Connection with client lost: " + e.getMessage());
        } finally {
            if (currentUser != null) {
                server.removeUserClient(currentUser.getUsername());
                notifyFriendsStatusChange(currentUser.getUsername(), false);
            }
            closeConnections();
        }
    }

    /**
     * Sends a message to the connected client.
     *
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        out.println(message);
    }

    /**
     * Closes the socket and I/O streams.
     */
    private void closeConnections() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connections: " + e.getMessage());
        }
    }

    /**
     * Retrieves a User object by username.
     *
     * @param username The username to search for.
     * @return The User object if found; otherwise, null.
     */
    private User getUserByUsername(String username) {
        for (User user : User.getAllUsers()) {
            if (user.getUsername() != null && user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Handles the LOGIN command.
     * Usage: LOGIN,username,password
     */
    private void handleLogin(String[] requestParts) {
        if (currentUser != null) {
            sendMessage("ERROR,Already logged in");
            return;
        }

        if (requestParts.length != 3) {
            sendMessage("ERROR,Invalid LOGIN command. Usage: LOGIN,username,password");
            return;
        }

        String username = requestParts[1];
        String password = requestParts[2];

        User user = getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            if (server.isUserLoggedIn(username)) {
                sendMessage("ERROR,User already logged in");
                return;
            }

            currentUser = user;
            server.addUserClient(username, this);
            sendMessage("LOGIN_SUCCESS");
            notifyFriendsStatusChange(username, true);
            sendUserList();
        } else {
            sendMessage("LOGIN_FAILURE,Invalid credentials");
        }
    }

    /**
     * Handles the CREATE_ACCOUNT command.
     * Usage: CREATE_ACCOUNT,username,password,email,birthday,bio,privacy
     */
    private void handleCreateAccount(String[] requestParts) {
        if (currentUser != null) {
            sendMessage("ERROR,Already logged in. Please logout to create a new account.");
            return;
        }

        if (requestParts.length != 7) {
            sendMessage("ERROR,Invalid CREATE_ACCOUNT command. Usage: CREATE_ACCOUNT,username,password,email,birthday,bio,privacy");
            return;
        }

        String username = requestParts[1];
        String password = requestParts[2];
        String email = requestParts[3];
        String birthday = requestParts[4];
        String bio = requestParts[5];
        String privacy = requestParts[6];

        if (!User.checkUserNameAvailability(username)) {
            sendMessage("ERROR,Username is already taken");
            return;
        }

        String profile = "Email: " + email + ", Birthday: " + birthday + ", Bio: " + bio + ", Privacy: " + privacy;
        String picture = "default_pic.png";

        User.addUser(username, username, password, profile, picture);
        User.saveUsersToFile();

        User newUser = getUserByUsername(username);
        if (newUser != null) {
            currentUser = newUser;
            server.addUserClient(username, this);
            sendMessage("CREATE_ACCOUNT_SUCCESS");
            notifyFriendsStatusChange(username, true);
            sendUserList();
        } else {
            sendMessage("ERROR,Failed to create account");
        }
    }

    /**
     * Handles the SEND_MESSAGE command.
     * Usage: SEND_MESSAGE,recipient_username,message
     */
    private void handleSendMessage(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to send messages");
            return;
        }

        if (requestParts.length < 3) {
            sendMessage("ERROR,Invalid SEND_MESSAGE command. Usage: SEND_MESSAGE,recipient_username,message");
            return;
        }

        String recipientUsername = requestParts[1];
        String message = requestParts[2];

        User recipient = getUserByUsername(recipientUsername);
        if (recipient == null) {
            sendMessage("ERROR,Recipient not found");
            return;
        }

        if (!currentUser.getFriendsDB().isFriend(recipient)) {
            sendMessage("ERROR,You are not friends with " + recipientUsername);
            return;
        }

        boolean messageSent = currentUser.getMessagingDB().addMessage(message, recipient);
        if (messageSent) {
            User.saveUsersToFile();
            sendMessage("SEND_MESSAGE_SUCCESS");
            server.sendToUser(recipientUsername, "MESSAGE," + currentUser.getUsername() + "," + message);
        } else {
            sendMessage("ERROR,Failed to send message");
        }
    }

    /**
     * Handles the ADD_FRIEND command.
     * Usage: ADD_FRIEND,friend_username
     */
    private void handleAddFriend(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to add friends");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid ADD_FRIEND command. Usage: ADD_FRIEND,friend_username");
            return;
        }

        String friendUsername = requestParts[1];
        User friend = getUserByUsername(friendUsername);

        if (friend == null) {
            sendMessage("ERROR,User not found");
            return;
        }

        if (friend.equals(currentUser)) {
            sendMessage("ERROR,Cannot add yourself as a friend");
            return;
        }

        if (currentUser.getFriendsDB().isFriend(friend)) {
            sendMessage("ERROR,You are already friends with " + friendUsername);
            return;
        }

        if (currentUser.getFriendsDB().hasPendingRequest(friend)) {
            sendMessage("ERROR,Friend request already sent to " + friendUsername);
            return;
        }

        friend.getFriendsDB().sendFriendRequest(currentUser);
        User.saveUsersToFile();
        sendMessage("ADD_FRIEND_SUCCESS," + friendUsername);

        if (server.isUserLoggedIn(friendUsername)) {
            server.sendToUser(friendUsername, "FRIEND_REQUEST," + currentUser.getUsername());
        }
    }

    /**
     * Handles the APPROVE_FRIEND_REQUEST command.
     * Usage: APPROVE_FRIEND_REQUEST,requester_username
     */
    private void handleApproveFriendRequest(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid APPROVE_FRIEND_REQUEST command. Usage: APPROVE_FRIEND_REQUEST,requester_username");
            return;
        }

        String requesterUsername = requestParts[1];
        User requester = getUserByUsername(requesterUsername);

        if (requester == null) {
            sendMessage("ERROR,Requester not found");
            return;
        }

        if (!currentUser.getFriendsDB().hasPendingRequest(requester)) {
            sendMessage("ERROR,No pending friend request from " + requesterUsername);
            return;
        }

        currentUser.getFriendsDB().approveFriendRequest(requester);
        requester.getFriendsDB().approveFriendRequest(currentUser);
        User.saveUsersToFile();
        sendMessage("APPROVE_FRIEND_REQUEST_SUCCESS," + requesterUsername);

        if (server.isUserLoggedIn(requesterUsername)) {
            server.sendToUser(requesterUsername, "FRIEND_REQUEST_APPROVED," + currentUser.getUsername());
        }
    }

    /**
     * Handles the REJECT_FRIEND_REQUEST command.
     * Usage: REJECT_FRIEND_REQUEST,requester_username
     */
    private void handleRejectFriendRequest(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid REJECT_FRIEND_REQUEST command. Usage: REJECT_FRIEND_REQUEST,requester_username");
            return;
        }

        String requesterUsername = requestParts[1];
        User requester = getUserByUsername(requesterUsername);

        if (requester == null) {
            sendMessage("ERROR,Requester not found");
            return;
        }

        if (!currentUser.getFriendsDB().hasPendingRequest(requester)) {
            sendMessage("ERROR,No pending friend request from " + requesterUsername);
            return;
        }

        currentUser.getFriendsDB().rejectFriendRequest(requester);
        User.saveUsersToFile();
        sendMessage("REJECT_FRIEND_REQUEST_SUCCESS," + requesterUsername);

        if (server.isUserLoggedIn(requesterUsername)) {
            server.sendToUser(requesterUsername, "FRIEND_REQUEST_REJECTED," + currentUser.getUsername());
        }
    }

    /**
     * Handles the REMOVE_FRIEND command.
     * Usage: REMOVE_FRIEND,friend_username
     */
    private void handleRemoveFriend(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to remove friends");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid REMOVE_FRIEND command. Usage: REMOVE_FRIEND,friend_username");
            return;
        }

        String friendUsername = requestParts[1];
        User friend = getUserByUsername(friendUsername);

        if (friend == null) {
            sendMessage("ERROR,User not found");
            return;
        }

        if (!currentUser.getFriendsDB().isFriend(friend)) {
            sendMessage("ERROR," + friendUsername + " is not your friend");
            return;
        }

        currentUser.getFriendsDB().removeFriend(friend);
        friend.getFriendsDB().removeFriend(currentUser);
        User.saveUsersToFile();
        sendMessage("REMOVE_FRIEND_SUCCESS," + friendUsername);

        if (server.isUserLoggedIn(friendUsername)) {
            server.sendToUser(friendUsername, "FRIEND_REMOVED," + currentUser.getUsername());
        }
    }

    /**
     * Handles the BLOCK_USER command.
     * Usage: BLOCK_USER,usernameToBlock
     */
    private void handleBlockUser(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to block users");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid BLOCK_USER command. Usage: BLOCK_USER,usernameToBlock");
            return;
        }

        String blockedUsername = requestParts[1];
        User blockedUser = getUserByUsername(blockedUsername);

        if (blockedUser == null) {
            sendMessage("ERROR,User not found");
            return;
        }

        if (blockedUser.equals(currentUser)) {
            sendMessage("ERROR,Cannot block yourself");
            return;
        }

        if (currentUser.getFriendsDB().isBlocked(blockedUser)) {
            sendMessage("ERROR,User is already blocked");
            return;
        }

        currentUser.getFriendsDB().blockUser(blockedUser);
        if (currentUser.getFriendsDB().isFriend(blockedUser)) {
            currentUser.getFriendsDB().removeFriend(blockedUser);
            blockedUser.getFriendsDB().removeFriend(currentUser);
        }
        User.saveUsersToFile();
        sendMessage("BLOCK_USER_SUCCESS," + blockedUsername);

        if (server.isUserLoggedIn(blockedUsername)) {
            server.sendToUser(blockedUsername, "USER_BLOCKED," + currentUser.getUsername());
        }
    }

    /**
     * Handles the UNBLOCK_USER command.
     * Usage: UNBLOCK_USER,usernameToUnblock
     */
    private void handleUnblockUser(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to unblock users");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid UNBLOCK_USER command. Usage: UNBLOCK_USER,usernameToUnblock");
            return;
        }

        String unblockUsername = requestParts[1];
        User unblockUser = getUserByUsername(unblockUsername);

        if (unblockUser == null) {
            sendMessage("ERROR,User not found");
            return;
        }

        if (!currentUser.getFriendsDB().isBlocked(unblockUser)) {
            sendMessage("ERROR,User is not blocked");
            return;
        }

        currentUser.getFriendsDB().unblockUser(unblockUser);
        User.saveUsersToFile();
        sendMessage("UNBLOCK_USER_SUCCESS," + unblockUsername);
    }

    /**
     * Handles the SEARCH_USER command.
     * Usage: SEARCH_USER,query
     */
    private void handleUserSearch(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to search for users");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid SEARCH_USER command. Usage: SEARCH_USER,query");
            return;
        }

        String query = requestParts[1].toLowerCase();
        List<String> matchingUsers = new ArrayList<>();

        for (User user : User.getAllUsers()) {
            if (user.getUsername() != null &&
                    (user.getUsername().toLowerCase().contains(query) ||
                            user.getName().toLowerCase().contains(query))) {
                if (!currentUser.getFriendsDB().isBlocked(user) && !user.getFriendsDB().isBlocked(currentUser)) {
                    matchingUsers.add(user.getUsername());
                }
            }
        }

        if (matchingUsers.isEmpty()) {
            sendMessage("SEARCH_USER_RESULTS,No users found matching the query.");
        } else {
            sendMessage("SEARCH_USER_RESULTS," + String.join(";", matchingUsers));
        }
    }

    /**
     * Handles the VIEW_USERS command.
     * Usage: VIEW_USERS
     */
    private void handleViewUsers(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to view users");
            return;
        }

        List<String> allUsers = new ArrayList<>();
        for (User user : User.getAllUsers()) {
            String status = server.isUserLoggedIn(user.getUsername()) ? "online" : "offline";
            allUsers.add(status + ":" + user.getUsername());
        }

        if (allUsers.isEmpty()) {
            sendMessage("USER_LIST,No users found.");
        } else {
            sendMessage("USER_LIST," + String.join(";", allUsers));
        }
    }

    /**
     * Handles the VIEW_FRIENDS command.
     * Usage: VIEW_FRIENDS
     */
    private void handleViewFriends(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to view friends");
            return;
        }

        List<String> friends = new ArrayList<>();
        for (User friend : currentUser.getFriendsDB().getFriends()) {
            String status = server.isUserLoggedIn(friend.getUsername()) ? "online" : "offline";
            friends.add(status + ":" + friend.getUsername());
        }

        if (friends.isEmpty()) {
            sendMessage("FRIENDS_LIST,You have no friends.");
        } else {
            sendMessage("FRIENDS_LIST," + String.join(";", friends));
        }
    }

    /**
     * Handles the VIEW_BLOCKED command.
     * Usage: VIEW_BLOCKED
     */
    private void handleViewBlocked(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to view blocked users");
            return;
        }

        List<String> blockedUsers = new ArrayList<>();
        for (User blocked : currentUser.getFriendsDB().getBlockedUsers()) {
            blockedUsers.add(blocked.getUsername());
        }

        if (blockedUsers.isEmpty()) {
            sendMessage("BLOCKED_USERS_LIST,You have no blocked users.");
        } else {
            sendMessage("BLOCKED_USERS_LIST," + String.join(";", blockedUsers));
        }
    }

    /**
     * Handles the GET_USER_PROFILE command.
     * Usage: GET_USER_PROFILE,username
     */
    private void handleGetUserProfile(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to view user profiles");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid GET_USER_PROFILE command. Usage: GET_USER_PROFILE,username");
            return;
        }

        String targetUsername = requestParts[1];
        User targetUser = getUserByUsername(targetUsername);

        if (targetUser == null) {
            sendMessage("ERROR,User not found");
            return;
        }

        String profileInfo = targetUser.getProfile();
        String picturePath = targetUser.getPicture();

        sendMessage("USER_PROFILE," + targetUser.getUsername() + "," + profileInfo + "," + picturePath);
    }

    /**
     * Handles the GET_MESSAGES command.
     * Usage: GET_MESSAGES,friend_username
     */
    private void handleGetMessages(String[] requestParts) {
        if (currentUser == null) {
            sendMessage("ERROR,Please log in first to retrieve messages");
            return;
        }

        if (requestParts.length != 2) {
            sendMessage("ERROR,Invalid GET_MESSAGES command. Usage: GET_MESSAGES,friend_username");
            return;
        }

        String friendUsername = requestParts[1];
        User friend = getUserByUsername(friendUsername);

        if (friend == null) {
            sendMessage("ERROR,Friend not found");
            return;
        }

        if (!currentUser.getFriendsDB().isFriend(friend)) {
            sendMessage("ERROR,You are not friends with " + friendUsername);
            return;
        }

        List<String> messages = currentUser.getMessagingDB().getMessages(friend);
        if (messages.isEmpty()) {
            sendMessage("MESSAGES_LIST,No messages with " + friendUsername);
        } else {
            StringBuilder sb = new StringBuilder();
            for (String msg : messages) {
                sb.append(msg).append("|");
            }
            if (sb.length() > 0) sb.setLength(sb.length() - 1);
            sendMessage("MESSAGES_LIST," + sb.toString());
        }
    }

    /**
     * Handles the LOGOUT command.
     * Usage: LOGOUT
     */
    private void handleLogout() {
        if (currentUser == null) {
            sendMessage("ERROR,You are not logged in");
            return;
        }

        notifyFriendsStatusChange(currentUser.getUsername(), false);

        server.removeUserClient(currentUser.getUsername());

        currentUser = null;
        sendMessage("LOGOUT_SUCCESS");
    }

    /**
     * Handles the EXIT command.
     * Usage: EXIT
     */
    private void handleExit() {
        sendMessage("EXIT_SUCCESS");
        if (currentUser != null) {
            handleLogout();
        }
        closeConnections();
    }

    /**
     * Notifies all friends about the user's online/offline status change.
     *
     * @param username The username of the user whose status has changed.
     * @param isOnline True if the user is online; false if offline.
     */
    private void notifyFriendsStatusChange(String username, boolean isOnline) {
        String statusCommand = isOnline ? "USER_ONLINE," + username : "USER_OFFLINE," + username;
        for (User friend : currentUser.getFriendsDB().getFriends()) {
            if (server.isUserLoggedIn(friend.getUsername())) {
                server.sendToUser(friend.getUsername(), statusCommand);
            }
        }
    }

    /**
     * Sends the current user list to the client.
     */
    private void sendUserList() {
        List<String> allUsers = new ArrayList<>();
        for (User user : User.getAllUsers()) {
            String status = server.isUserLoggedIn(user.getUsername()) ? "online" : "offline";
            allUsers.add(status + ":" + user.getUsername());
        }

        if (allUsers.isEmpty()) {
            sendMessage("USER_LIST,No users found.");
        } else {
            sendMessage("USER_LIST," + String.join(";", allUsers));
        }
    }
}
