package src.main.app;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * AppGUI
 *
 * Main application interface after user logs in.
 * Provides functionalities such as searching users, managing friends,
 * viewing blocked users, handling pending friend requests, and messaging.
 *
 * @version 12/08/2024
 * @author Madhavan Prasanna
 */
public class AppGUI extends JFrame implements AppGUIInterface {
    private User currentUser;
    private JPanel userListPanel;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;

    private JPanel mainContentPanel;
    private JTextArea chatArea;
    private JTextField inputField;
    private JPanel userInfoPanel;

    private User selectedChatUser;

    private JLabel currentChatLabel;

    private JSplitPane chatInfoSplitPane;

    public AppGUI(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Application - Logged in as " + (currentUser != null ? currentUser.getUsername() : "Guest"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLayout(new BorderLayout());

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton searchUsersBtn = new JButton("Search Users");
        JButton viewFriendsBtn = new JButton("View Friends");
        JButton viewBlockedBtn = new JButton("View Blocked");
        JButton viewPendingBtn = new JButton("View Pending");
        JButton logoutBtn = new JButton("Logout");

        searchUsersBtn.addActionListener(e -> searchUsers());
        viewFriendsBtn.addActionListener(e -> viewFriends());
        viewBlockedBtn.addActionListener(e -> viewBlockedUsers());
        viewPendingBtn.addActionListener(e -> viewPendingRequests());
        logoutBtn.addActionListener(e -> logout());

        toolBar.add(searchUsersBtn);
        toolBar.add(viewFriendsBtn);
        toolBar.add(viewBlockedBtn);
        toolBar.add(viewPendingBtn);
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(logoutBtn);

        add(toolBar, BorderLayout.NORTH);

        userListPanel = new JPanel(new BorderLayout());
        userListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        JLabel userListLabel = new JLabel("Users", SwingConstants.CENTER);
        userListPanel.add(userListLabel, BorderLayout.NORTH);

        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setCellRenderer(new UserCellRenderer());
        refreshUserList();

        userList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = userList.locationToIndex(e.getPoint());
                if (index < 0) {
                    return;
                }

                String selected = userListModel.get(index);
                String username = selected.trim();
                User selectedUser = getUserByUsername(username);

                if (selectedUser == null) {
                    JOptionPane.showMessageDialog(userList, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    if (currentUser.getFriendsDB().isFriend(selectedUser)) {
                        initiateChat(selectedUser);
                    } else {
                        JOptionPane.showMessageDialog(userList,
                                selectedUser.getUsername() + " is not your friend. View their profile to interact.",
                                "Not a Friend", JOptionPane.INFORMATION_MESSAGE);
                        showUserInfo(selectedUser);
                    }
                }

                if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
                    userList.setSelectedIndex(index);
                    showContextMenu(e.getComponent(), e.getX(), e.getY(), selectedUser);
                }
            }
        });


        JScrollPane userListScroll = new JScrollPane(userList);
        userListPanel.add(userListScroll, BorderLayout.CENTER);

        mainContentPanel = new JPanel(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);

        inputField = new JTextField();
        inputField.addActionListener(e -> sendMessage());

        currentChatLabel = new JLabel("No active chat", SwingConstants.CENTER);
        currentChatLabel.setFont(new Font("Arial", Font.BOLD, 14));
        currentChatLabel.setBorder(new EmptyBorder(5, 0, 5, 0));

        userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        userInfoPanel.setPreferredSize(new Dimension(300, 0));

        chatInfoSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatScroll, userInfoPanel);
        chatInfoSplitPane.setDividerLocation(700);
        chatInfoSplitPane.setResizeWeight(1.0);
        chatInfoSplitPane.setOneTouchExpandable(true);

        JPanel chatTopPanel = new JPanel(new BorderLayout());
        chatTopPanel.add(currentChatLabel, BorderLayout.NORTH);
        chatTopPanel.add(chatInfoSplitPane, BorderLayout.CENTER);

        mainContentPanel.add(chatTopPanel, BorderLayout.CENTER);
        mainContentPanel.add(inputField, BorderLayout.SOUTH);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, userListPanel, mainContentPanel);
        mainSplit.setDividerLocation(250);
        mainSplit.setResizeWeight(0.25);
        mainSplit.setOneTouchExpandable(true);

        add(mainSplit, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Refreshes the user list by populating it with all users except the current user and blocked users.
     */
    private void refreshUserList() {
        userListModel.clear();
        for (User u : User.getAllUsers()) {
            if (!u.equals(currentUser) && !currentUser.getFriendsDB().isBlocked(u)) {
                userListModel.addElement(u.getUsername());
            }
        }
    }

    /**
     * Retrieves a User object by username.
     *
     * @param username The username to search for.
     * @return The User object if found; otherwise, null.
     */
    private User getUserByUsername(String username) {
        return User.getUserByUsername(username);
    }

    /**
     * Displays the user information panel for the selected user.
     *
     * @param user The user whose information is to be displayed.
     */
    private void showUserInfo(User user) {
        userInfoPanel.removeAll();

        JLabel pictureLabel = new JLabel();
        String picturePath = user.getPicture();
        ImageIcon profileIcon;

        try {
            if (picturePath != null && !picturePath.trim().isEmpty()) {
                File imgFile = new File(picturePath);
                if (imgFile.exists()) {
                    profileIcon = new ImageIcon(picturePath);
                } else {
                    throw new Exception("Profile picture file not found.");
                }
            } else {
                throw new Exception("No profile picture path provided.");
            }
        } catch (Exception e) {
            String defaultPath = "C:\\Users\\its1g\\Downloads\\ezgif-7-d251ec8798.png";
            try {
                File defaultImgFile = new File(defaultPath);
                if (defaultImgFile.exists()) {
                    profileIcon = new ImageIcon(defaultPath);
                } else {
                    throw new Exception("Default profile picture file not found.");
                }
            } catch (Exception ex) {
                profileIcon = new ImageIcon();
            }
        }

        Image scaledImage = profileIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        pictureLabel.setIcon(new ImageIcon(scaledImage));
        pictureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Name: " + (user.getName() != null ? user.getName() : "N/A"));
        JLabel usernameLabel = new JLabel("Username: " + (user.getUsername() != null ? user.getUsername() : "N/A"));
        JLabel profileLabel = new JLabel("<html>Profile: " + (user.getProfile() != null ? user.getProfile() : "N/A") + "</html>");

        JButton addFriendButton = new JButton("Add Friend");
        JButton blockUserButton = new JButton("Block User");
        JButton backButton = new JButton("Back");

        if (currentUser.getFriendsDB().isFriend(user)) {
            addFriendButton.setEnabled(false);
            addFriendButton.setText("Already Friends");
        } else if (hasSentFriendRequest(user)) {
            addFriendButton.setEnabled(false);
            addFriendButton.setText("Request Sent");
        }

        addFriendButton.addActionListener(e -> addFriend(user));

        blockUserButton.addActionListener(e -> blockUser(user));

        backButton.addActionListener(e -> {
            userInfoPanel.removeAll();
            userInfoPanel.revalidate();
            userInfoPanel.repaint();
        });

        userInfoPanel.add(pictureLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(profileLabel);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userInfoPanel.add(addFriendButton);
        userInfoPanel.add(blockUserButton);
        userInfoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        userInfoPanel.add(backButton);

        userInfoPanel.revalidate();
        userInfoPanel.repaint();
    }

    /**
     * Checks if the current user has sent a friend request to the specified user.
     *
     * @param user The user to check.
     * @return True if a friend request has been sent; otherwise, false.
     */
    private boolean hasSentFriendRequest(User user) {
        return user.getFriendsDB().getPendingRequests().contains(currentUser);
    }

    /**
     * Sends a friend request to the specified user.
     *
     * @param friend The user to send a friend request to.
     */
    private void addFriend(User friend) {
        if (currentUser.getFriendsDB().isFriend(friend)) {
            JOptionPane.showMessageDialog(this, friend.getUsername() + " is already your friend.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (hasSentFriendRequest(friend)) {
            JOptionPane.showMessageDialog(this, "Friend request already sent to " + friend.getUsername() + ".", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        currentUser.getFriendsDB().sendFriendRequest(friend);
        JOptionPane.showMessageDialog(this, "Friend request sent to " + friend.getUsername() + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
        showUserInfo(friend);
    }

    /**
     * Blocks the specified user, preventing further interactions.
     *
     * @param user The user to block.
     */
    private void blockUser(User user) {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to block " + user.getUsername() + "?", "Block User", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            currentUser.getFriendsDB().blockUser(user);
            JOptionPane.showMessageDialog(this, user.getUsername() + " has been blocked.", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshUserList();
            userInfoPanel.removeAll();
            userInfoPanel.revalidate();
            userInfoPanel.repaint();
            chatArea.setText("");
            currentChatLabel.setText("No active chat");

            if (selectedChatUser != null && selectedChatUser.equals(user)) {
                selectedChatUser = null;
                chatArea.setText("");
                setTitle("Application - Logged in as " + (currentUser != null ? currentUser.getUsername() : "Guest"));
            }
        }
    }

    /**
     * Sends a message to the currently selected chat user.
     */
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty() && selectedChatUser != null) {
            if (currentUser.getFriendsDB().isBlocked(selectedChatUser) || selectedChatUser.getFriendsDB().isBlocked(currentUser)) {
                JOptionPane.showMessageDialog(this, "Cannot send message. You have blocked this user or they have blocked you.", "Blocked", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = currentUser.getMessagingDB().addMessage(text, selectedChatUser);
            if (success) {
                chatArea.append(currentUser.getUsername() + ": " + text + "\n");
                inputField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to send message.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (selectedChatUser == null) {
            JOptionPane.showMessageDialog(this, "Please select a user to chat with.", "No User Selected", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Initiates a chat with the specified user by loading the message history.
     *
     * @param chatUser The user to chat with.
     */
    private void initiateChat(User chatUser) {
        if (chatUser == null) {
            JOptionPane.showMessageDialog(this, "Invalid user selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (currentUser.getFriendsDB().isBlocked(chatUser) || chatUser.getFriendsDB().isBlocked(currentUser)) {
            JOptionPane.showMessageDialog(this, "Cannot initiate chat. You have blocked this user or they have blocked you.", "Blocked", JOptionPane.WARNING_MESSAGE);
            return;
        }

        this.selectedChatUser = chatUser;
        chatArea.setText("");

        List<String> messages = currentUser.getMessagingDB().getMessages(chatUser);
        for (String msg : messages) {
            chatArea.append(msg + "\n");
        }

        currentChatLabel.setText("Chatting with: " + chatUser.getUsername());

        setTitle("Chatting with " + chatUser.getUsername() + " - Logged in as " + currentUser.getUsername());

        System.out.println("Initiated chat with: " + chatUser.getUsername());
    }


    /**
     * Logs out the current user after confirmation.
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new WelcomeGUI();
        }
    }

    /**
     * Searches for users based on a query and displays the results.
     */
    private void searchUsers() {
        String query = JOptionPane.showInputDialog(this, "Enter username or name to search:", "Search Users", JOptionPane.QUESTION_MESSAGE);
        if (query != null && !query.trim().isEmpty()) {
            List<User> results = new ArrayList<>();
            for (User u : User.getAllUsers()) {
                if (!u.equals(currentUser) &&
                        !currentUser.getFriendsDB().isBlocked(u) &&
                        (u.getUsername().toLowerCase().contains(query.toLowerCase()) ||
                                (u.getName() != null && u.getName().toLowerCase().contains(query.toLowerCase())))) {
                    results.add(u);
                }
            }

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No users found matching the query.", "Search Results", JOptionPane.INFORMATION_MESSAGE);
            } else {
                DefaultListModel<String> resultModel = new DefaultListModel<>();
                for (User u : results) {
                    resultModel.addElement(u.getUsername());
                }

                JList<String> resultList = new JList<>(resultModel);
                resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                resultList.setCellRenderer(new UserCellRenderer());

                resultList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                            int index = resultList.locationToIndex(e.getPoint());
                            if (index >= 0) {
                                String selectedUsername = resultList.getModel().getElementAt(index);
                                User selectedUser = User.getUserByUsername(selectedUsername);
                                if (selectedUser != null) {
                                    if (currentUser.getFriendsDB().isFriend(selectedUser)) {
                                        initiateChat(selectedUser);
                                    } else {
                                        showUserInfo(selectedUser);
                                    }
                                }
                            }
                        }
                    }
                });

                JScrollPane scrollPane = new JScrollPane(resultList);
                scrollPane.setPreferredSize(new Dimension(300, 200));

                JOptionPane.showMessageDialog(this, scrollPane, "Search Results (Double-click to interact)", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    /**
     * Displays the list of friends and allows initiating chats.
     */
    private void viewFriends() {
        List<User> friends = currentUser.getFriendsDB().getFriends();
        if (friends.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no friends added.", "View Friends", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultListModel<String> friendsModel = new DefaultListModel<>();
        for (User u : friends) {
            friendsModel.addElement(u.getUsername());
        }

        JList<String> friendsList = new JList<>(friendsModel);
        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsList.setCellRenderer(new UserCellRenderer());

        friendsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    int index = friendsList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedUsername = friendsList.getModel().getElementAt(index);
                        User selectedUser = User.getUserByUsername(selectedUsername);
                        if (selectedUser != null) {
                            initiateChat(selectedUser);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(friendsList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Your Friends (Double-click to chat)", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the list of blocked users and allows unblocking.
     */
    private void viewBlockedUsers() {
        List<User> blocked = currentUser.getFriendsDB().getBlockedUsers();
        if (blocked.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no blocked users.", "View Blocked Users", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultListModel<String> blockedModel = new DefaultListModel<>();
        for (User u : blocked) {
            blockedModel.addElement(u.getUsername());
        }

        JList<String> blockedList = new JList<>(blockedModel);
        blockedList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        blockedList.setCellRenderer(new UserCellRenderer());

        blockedList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    int index = blockedList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedUsername = blockedList.getModel().getElementAt(index);
                        User selectedUser = User.getUserByUsername(selectedUsername);
                        if (selectedUser != null) {
                            int confirm = JOptionPane.showConfirmDialog(AppGUI.this, "Unblock " + selectedUser.getUsername() + "?", "Unblock User", JOptionPane.YES_NO_OPTION);
                            if (confirm == JOptionPane.YES_OPTION) {
                                currentUser.getFriendsDB().unblockUser(selectedUser);
                                JOptionPane.showMessageDialog(AppGUI.this, selectedUser.getUsername() + " has been unblocked.", "Success", JOptionPane.INFORMATION_MESSAGE);
                                viewBlockedUsers();
                                refreshUserList();
                            }
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(blockedList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Blocked Users (Double-click to unblock)", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the list of pending friend requests and allows accepting or rejecting them.
     */
    private void viewPendingRequests() {
        List<User> pending = currentUser.getFriendsDB().getPendingRequests();
        if (pending.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no pending friend requests.", "View Pending Requests", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        DefaultListModel<String> pendingModel = new DefaultListModel<>();
        for (User u : pending) {
            pendingModel.addElement(u.getUsername());
        }

        JList<String> pendingList = new JList<>(pendingModel);
        pendingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        pendingList.setCellRenderer(new UserCellRenderer());

        pendingList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    int index = pendingList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedUsername = pendingList.getModel().getElementAt(index);
                        User requester = User.getUserByUsername(selectedUsername);
                        if (requester != null) {
                            Object[] options = {"Accept", "Reject"};
                            int choice = JOptionPane.showOptionDialog(AppGUI.this, "Accept friend request from " + requester.getUsername() + "?",
                                    "Friend Request", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            if (choice == JOptionPane.YES_OPTION) {
                                currentUser.getFriendsDB().approveFriendRequest(requester);
                                JOptionPane.showMessageDialog(AppGUI.this, "Friend request accepted.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            } else if (choice == JOptionPane.NO_OPTION) {
                                currentUser.getFriendsDB().rejectFriendRequest(requester);
                                JOptionPane.showMessageDialog(AppGUI.this, "Friend request rejected.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
                            }
                            viewPendingRequests();
                            refreshUserList();
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(pendingList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        JOptionPane.showMessageDialog(this, scrollPane, "Pending Friend Requests (Double-click to respond)", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows a context menu for the selected user with options to View Profile or Chat.
     *
     * @param invoker The component invoking the context menu.
     * @param x       The x-coordinate for the menu.
     * @param y       The y-coordinate for the menu.
     * @param user    The user associated with the context menu.
     */
    private void showContextMenu(Component invoker, int x, int y, User user) {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem viewProfileItem = new JMenuItem("View Profile");
        JMenuItem chatItem = new JMenuItem("Chat");

        if (!currentUser.getFriendsDB().isFriend(user)) {
            chatItem.setEnabled(false);
        }

        viewProfileItem.addActionListener(e -> showUserInfo(user));
        chatItem.addActionListener(e -> initiateChat(user));

        contextMenu.add(viewProfileItem);
        contextMenu.add(chatItem);

        contextMenu.show(invoker, x, y);
    }

    /**
     * Custom cell renderer for user lists to allow further customization if needed.
     */
    class UserCellRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String text = (String) value;
            return label;
        }
    }

    /**
     * The main method to launch the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        User.loadUsersFromFile();

        User someUser = User.getUserByUsername("john_doe");

        SwingUtilities.invokeLater(() -> {
            if (someUser != null) {
                new AppGUI(someUser);
            } else {
                new WelcomeGUI();
            }
        });
    }
}
