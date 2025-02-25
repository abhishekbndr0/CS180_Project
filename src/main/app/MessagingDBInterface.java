package src.main.app;

import java.io.File;
import java.util.ArrayList;

/**
 * Team Project -- MessagingDBInterface
 *
 * Interface for the MessagingDB class.
 *
 * @version 12/08/2024
 * @author Abhishek Bandaru
 */
public interface MessagingDBInterface {
    ArrayList<String> getMessages(User recipient);
    ArrayList<File> getPhotos(User recipient);
    boolean addMessage(String text, User recipient);
    boolean deleteMessage(String text, User recipient);
    boolean canMessage(User user);
    boolean photoMessage(File photo, User recipient);

    void receiveMessage(Message msg);
}

