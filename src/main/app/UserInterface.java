package src.main.app;

import java.util.ArrayList;

/**
 * Team Project -- User Interface
 *
 * This interface defines the essential methods for a User.
 *
 * @version 12/08/2024
 * @author Abhishek Bandaru
 */
public interface UserInterface {
    // Returns the name of the User.
    String getName();

    // Returns the username of the User.
    String getUsername();

    // Returns the password of the User.
    String getPassword();

    // Returns the profile description of the User.
    String getProfile();

    // Returns the profile picture of the User.
    String getPicture();

    // Searches for a user by username.
    String searchUser(String username);

}
