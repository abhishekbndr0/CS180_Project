package src.main.app;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Team Project -- MessagingDB
 *
 * This class implements the MessagingDBInterface.
 * It manages sending and receiving text and photo messages between users.
 *
 * @version 12/08/2024
 * @author Haiyan Xuan, Rohan Uddaraju, Abhishek Bandaru
 */
public class MessagingDB implements MessagingDBInterface, Serializable {
    private ConcurrentHashMap<String, ArrayList<Message>> messages;
    private User owner;

    private static final long serialVersionUID = 1L;

    public MessagingDB(User owner) {
        messages = new ConcurrentHashMap<>();
        this.owner = owner;
    }

    /**
     * Retrieves text messages exchanged with a specific user.
     *
     * @param recipient The user whose messages are to be retrieved.
     * @return An ArrayList of message strings.
     */
    @Override
    public ArrayList<String> getMessages(User recipient) {
        ArrayList<String> messageList = new ArrayList<>();
        ArrayList<Message> msgObjects = messages.get(recipient.getUsername());
        if (msgObjects != null) {
            for (Message msg : msgObjects) {
                if (msg.getContent() != null) {
                    messageList.add(msg.toString());
                }
            }
        }
        return messageList;
    }

    /**
     * Retrieves photo messages exchanged with a specific user.
     *
     * @param recipient The user whose photo messages are to be retrieved.
     * @return An ArrayList of photo files.
     */
    @Override
    public ArrayList<File> getPhotos(User recipient) {
        ArrayList<File> photoList = new ArrayList<>();
        ArrayList<Message> msgObjects = messages.get(recipient.getUsername());
        if (msgObjects != null) {
            for (Message msg : msgObjects) {
                if (msg.getPhoto() != null) {
                    photoList.add(msg.getPhoto());
                }
            }
        }
        return photoList;
    }

    /**
     * Adds a text message to the conversation with a recipient.
     *
     * @param text      The message content.
     * @param recipient The user to send the message to.
     * @return true if the message was sent successfully, false otherwise.
     */
    @Override
    public boolean addMessage(String text, User recipient) {
        if (recipient == null || text == null || text.isEmpty()) {
            return false;
        }
        if (canMessage(recipient)) {
            Message msg = new Message(owner, recipient, text);

            messages.computeIfAbsent(recipient.getUsername(), k -> new ArrayList<>()).add(msg);

            recipient.getMessagingDB().receiveMessage(msg);

            System.out.println("Message sent from " + owner.getUsername() + " to " + recipient.getUsername() + ": " + text);
            return true;
        }
        return false;
    }


    /**
     * Deletes a specific text message from the conversation with a recipient.
     *
     * @param text      The message content to delete.
     * @param recipient The user whose conversation the message is in.
     * @return true if the message was deleted successfully, false otherwise.
     */
    @Override
    public boolean deleteMessage(String text, User recipient) {
        if (recipient == null || text == null || text.isEmpty()) {
            return false;
        }
        ArrayList<Message> msgList = messages.get(recipient.getUsername());
        if (msgList != null) {
            return msgList.removeIf(msg -> text.equals(msg.getContent()));
        }
        return false;
    }

    /**
     * Determines if the owner can message the specified user.
     *
     * @param user The user to check messaging permission with.
     * @return true if messaging is allowed, false otherwise.
     */
    @Override
    public boolean canMessage(User user) {
        return owner.getFriendsDB().isFriend(user) && !owner.getFriendsDB().isBlocked(user);
    }

    /**
     * Adds a photo message to the conversation with a recipient.
     *
     * @param photo     The photo file to send.
     * @param recipient The user to send the photo to.
     * @return true if the photo was sent successfully, false otherwise.
     */
    @Override
    public boolean photoMessage(File photo, User recipient) {
        if (photo == null || recipient == null) {
            return false;
        }
        if (canMessage(recipient)) {
            Message msg = new Message(owner, recipient, photo);
            messages.computeIfAbsent(recipient.getUsername(), k -> new ArrayList<>()).add(msg);
            recipient.getMessagingDB().receiveMessage(msg);
            return true;
        }
        return false;
    }

    /**
     * Receives a message from another user.
     *
     * @param msg The message to receive.
     */
    @Override
    public void receiveMessage(Message msg) {
        if (msg != null) {
            messages.computeIfAbsent(msg.getSender().getUsername(), k -> new ArrayList<>()).add(msg);

            System.out.println("Message received by " + owner.getUsername() + " from " + msg.getSender().getUsername() + ": " + msg.getContent());
        }
    }

} 