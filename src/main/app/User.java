package src.main.app;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Team Project -- User
 *
 * This class implements the User Interface.
 * This class creates the User object, by instantiating an array of Users.
 *
 * @version 12/08/2024
 * @author Haiyan Xuan, Rohan Uddaraju
 */

public class User implements Runnable, Serializable, UserInterface {
    private String name;
    private String username;
    private String password;
    private String profile;
    private String picture;
    private transient FriendsDB friendsDB;
    private transient MessagingDB messagingDB;
    private transient Thread userThread;

    private static final long serialVersionUID = 1L;
    private static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
    private static final String FILE_NAME = "userDatabase.ser";
    private static final Object lock = new Object();


    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found.");
        }
    }

    public User(String name, String username, String password, String profile, String picture) throws IllegalArgumentException {
        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || profile.isEmpty() || picture.isEmpty()) {
            throw new IllegalArgumentException("All fields must be filled.");
        }
        this.name = name;
        this.username = username;
        this.password = hashPassword(password);
        this.profile = profile;
        this.picture = picture;
        this.friendsDB = new FriendsDB(this);
        this.messagingDB = new MessagingDB(this);
    }

    public boolean login(String handle, String pwd) {
        return this.username.equals(handle) && this.password.equals(hashPassword(pwd));
    }

    @Override
    public String getName() { return name; }
    @Override
    public String getUsername() { return username; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getProfile() { return profile; }
    @Override
    public String getPicture() { return picture; }
    public MessagingDB getMessagingDB() { return messagingDB; }
    public FriendsDB getFriendsDB() { return friendsDB; }

    public static CopyOnWriteArrayList<User> getAllUsers() {
        return users;
    }

    public static void loadUsersFromFile() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            synchronized (lock) {
                Object obj = in.readObject();
                if (obj instanceof CopyOnWriteArrayList) {
                    users = (CopyOnWriteArrayList<User>) obj;
                    System.out.println("Users loaded from file.");
                    for (User user : users) {
                        user.friendsDB = new FriendsDB(user);
                        user.messagingDB = new MessagingDB(user);
                        user.userThread = new Thread(user);
                        user.userThread.start();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("User database file not found. Starting with an empty user list.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    public static void saveUsersToFile() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME, false))) {
            synchronized (lock) {
                out.writeObject(users);
                System.out.println("Users saved to file.");
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public static User addUser(String name, String username, String password, String profile, String picture) {
        if (!checkUserNameAvailability(username)) {
            System.out.println("Username " + username + " is already taken.");
            return null;
        }
        try {
            User newUser = new User(name, username, password, profile, picture);
            users.add(newUser);
            saveUsersToFile();
            return newUser;
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating user: " + e.getMessage());
            return null;
        }
    }

    public static boolean checkUserNameAvailability(String username) {
        for (User user : users) {
            if (user.username.equalsIgnoreCase(username)) {
                return false;
            }
        }
        return true;
    }

    public static User getUserByUsername(String username) {
        for (User user : users) {
            if (user.username.equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    public static void displayAllUsers() {
        for (User user : users) {
            System.out.println("Name: " + user.name + ", Username: " + user.username);
        }
    }

    @Override
    public String searchUser(String handle) {
        for (User user : users) {
            if (!friendsDB.isBlocked(user) && user.getUsername().equalsIgnoreCase(handle)) {
                return user.toString();
            }
        }
        return "User Not Found";
    }


    @Override
    public String toString() {
        return "Name: " + name + "\nUsername: " + username;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.friendsDB = new FriendsDB(this);
        this.messagingDB = new MessagingDB(this);
        this.userThread = new Thread(this);
        this.userThread.start();
    }

    @Override
    public void run() {
        System.out.println("User thread for " + username + " is running.");
    }
}