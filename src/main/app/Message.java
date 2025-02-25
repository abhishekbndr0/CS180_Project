package src.main.app;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message
 *
 * Represents a message between two users, which can be either text or photo.
 *
 * @version 12/08/2024
 * @author Haiyan Xuan, Rohan Uddaraju
 */
public class Message implements Serializable, MessageInterface {
    private User sender;
    private User recipient;
    private String content;
    private File photo;
    private LocalDateTime timestamp;

    private static final long serialVersionUID = 1L;

    public Message(User sender, User recipient, String content) {
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.photo = null;
        this.timestamp = LocalDateTime.now();
    }

    public Message(User sender, User recipient, File photo) {
        this.sender = sender;
        this.recipient = recipient;
        this.photo = photo;
        this.content = null;
        this.timestamp = LocalDateTime.now();
    }


    public User getSender() { return sender; }
    public User getRecipient() { return recipient; }
    public String getContent() { return content; }
    public File getPhoto() { return photo; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        if (content != null) {
            return "[" + timestamp.toString() + "] " + sender.getUsername() + ": " + content;
        } else if (photo != null) {
            return "[" + timestamp.toString() + "] " + sender.getUsername() + " sent a photo: " + photo.getName();
        } else {
            return "[" + timestamp.toString() + "] " + sender.getUsername() + " sent an unknown type of message.";
        }
    }
}
