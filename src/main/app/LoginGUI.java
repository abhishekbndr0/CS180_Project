package src.main.app;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;

/**
 * LoginGUI
 *
 * Allows existing users to log in.
 *
 * @version 12/08/2024
 * @author Madhavan Prasanna
 */
public class LoginGUI extends JFrame implements LoginGUIInterface {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginGUI() {
        FlatLightLaf.setup();

        setTitle("Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        String gifPath = "C:\\Users\\its1g\\Downloads\\200w (2).gif";
        ImageIcon gifIcon = new ImageIcon(gifPath);
        Image scaledGif = gifIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(scaledGif));
        background.setLayout(new GridBagLayout());
        add(background);

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                Image resizedGif = gifIcon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT);
                background.setIcon(new ImageIcon(resizedGif));
            }
        });

        JLabel userLabel = new JLabel("Username:");
        JLabel passLabel = new JLabel("Password:");
        userLabel.setForeground(Color.WHITE);
        passLabel.setForeground(Color.WHITE);

        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        loginButton.addActionListener(e -> doLogin());
        backButton.addActionListener(e -> {
            dispose();
            new WelcomeGUI();
        });

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(userLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(passLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(passwordField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        background.add(inputPanel, gbc);

        gbc.gridy = 1;
        background.add(buttonPanel, gbc);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Handles the login logic when the Login button is clicked.
     */
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Both fields must be filled.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = User.getUserByUsername(username);
        if (user != null && user.login(username, password)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new AppGUI(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Main method to launch the LoginGUI independently for testing purposes.
     * You can remove or comment this out if LoginGUI is launched from another part of your application.
     */
    public static void main(String[] args) throws FileNotFoundException {
        User.loadUsersFromFile();

        SwingUtilities.invokeLater(() -> new LoginGUI());
    }
}
