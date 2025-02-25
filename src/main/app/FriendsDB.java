package src.main.app;

import java.util.ArrayList;
import java.util.List;

/**
 * FriendsDB
 *
 * Implements the FriendsDBInterface to manage friends, pending requests, and blocked users.
 *
 * @version 12/08/2024
 * @author Haiyan Xuan, Abhishek Bandaru
 */
public class FriendsDB implements FriendsDBInterface {
    private List<User> friends;
    private List<User> pendingRequests;
    private List<User> blockedUsers;
    private User owner;

    public FriendsDB(User owner) {
        this.owner = owner;
        this.friends = new ArrayList<>();
        this.pendingRequests = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
    }

    @Override
    public List<User> getFriends() {
        return new ArrayList<>(friends);
    }

    @Override
    public void sendFriendRequest(User user) {
        if (user == null) {
            System.out.println("Cannot send friend request to a null user.");
            return;
        }
        if (friends.contains(user)) {
            System.out.println(user.getUsername() + " is already your friend.");
            return;
        }
        if (pendingRequests.contains(user)) {
            System.out.println("Friend request already sent to " + user.getUsername() + ".");
            return;
        }
        if (blockedUsers.contains(user)) {
            System.out.println("You have blocked " + user.getUsername() + ". Unblock to send a friend request.");
            return;
        }
        pendingRequests.add(user);
        System.out.println("Friend request sent to " + user.getUsername());

        FriendsDB recipientFriendsDB = user.getFriendsDB();
        if (recipientFriendsDB != null) {
            recipientFriendsDB.receiveFriendRequest(owner);
        }
    }

    /**
     * Receives a friend request from another user.
     * This method is called internally when another user sends a friend request.
     */
    public void receiveFriendRequest(User user) {
        if (user == null) {
            System.out.println("Received friend request from a null user.");
            return;
        }
        if (!pendingRequests.contains(user) && !friends.contains(user)) {
            pendingRequests.add(user);
            System.out.println("Received a friend request from " + user.getUsername());
        }
    }

    @Override
    public void approveFriendRequest(User user) {
        if (pendingRequests.contains(user)) {
            friends.add(user);
            pendingRequests.remove(user);

            FriendsDB otherFriendsDB = user.getFriendsDB();
            if (otherFriendsDB != null && !otherFriendsDB.getFriends().contains(owner)) {
                otherFriendsDB.getFriends().add(owner);
            }

            User.saveUsersToFile();
            System.out.println("Friend request approved between " + owner.getUsername() + " and " + user.getUsername());
        } else {
            System.out.println("No pending friend request from " + user.getUsername());
        }
    }

    @Override
    public void rejectFriendRequest(User user) {
        if (pendingRequests.remove(user)) {
            System.out.println("Friend request from " + user.getUsername() + " rejected.");
        } else {
            System.out.println("No pending friend request from " + user.getUsername());
        }
    }

    @Override
    public void removeFriend(User user) {
        if (friends.remove(user)) {
            FriendsDB otherFriendsDB = user.getFriendsDB();
            if (otherFriendsDB != null) {
                otherFriendsDB.getFriends().remove(owner);
            }
            System.out.println(user.getUsername() + " has been removed from friends.");
        } else {
            System.out.println(user.getUsername() + " was not found in the friends list.");
        }
    }

    @Override
    public boolean isFriend(User user) {
        return friends.contains(user);
    }

    @Override
    public List<User> getBlockedUsers() {
        return new ArrayList<>(blockedUsers);
    }

    @Override
    public void blockUser(User user) {
        if (user == null) {
            System.out.println("Cannot block a null user.");
            return;
        }
        if (!blockedUsers.contains(user)) {
            removeFriend(user); // Remove from friends if present
            blockedUsers.add(user);

            User.saveUsersToFile();
            System.out.println(owner.getUsername() + " has blocked " + user.getUsername());
        } else {
            System.out.println(owner.getUsername() + " has already blocked " + user.getUsername());
        }
    }

    @Override
    public void unblockUser(User user) {
        if (user == null) {
            System.out.println("Cannot unblock a null user.");
            return;
        }
        if (blockedUsers.remove(user)) {
            User.saveUsersToFile();
            System.out.println(owner.getUsername() + " has unblocked " + user.getUsername());
        } else {
            System.out.println(user.getUsername() + " is not in your blocked users list.");
        }
    }

    @Override
    public boolean isBlocked(User user) {
        return blockedUsers.contains(user);
    }

    @Override
    public List<User> getPendingRequests() {
        return new ArrayList<>(pendingRequests);
    }

    public ArrayList<User> getUsers() {
        return new ArrayList<>(User.getAllUsers());
    }


    public boolean hasPendingRequest(User friend) {
        if (friend == null) {
            System.out.println("Cannot check pending request for a null user.");
            return false;
        }
        return pendingRequests.contains(friend);
    }
}
