package src.main.app;

import java.util.List;

/**
 * FriendsDBInterface
 *
 * Defines the contract for managing friends, pending requests, and blocked users.
 *
 * @version 12/08/2024
 * @author Abhishek Bandaru
 */
public interface FriendsDBInterface {
    List<User> getFriends();
    void sendFriendRequest(User user);
    void approveFriendRequest(User user);
    void rejectFriendRequest(User user);
    void removeFriend(User user);
    boolean isFriend(User user);

    List<User> getBlockedUsers();
    void blockUser(User user);
    void unblockUser(User user);
    boolean isBlocked(User user);

    List<User> getPendingRequests();
}
