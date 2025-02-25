package src.main.app;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * RegisterGUI
 *
 * Allows new users to register by providing necessary details.
 *
 * @version 12/08/2024
 * @author Madhavan Prasanna
 */
public class RegisterGUI extends JFrame implements RegisterGUIInterface {
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextArea profileArea;
    private JTextField pictureField;

    public RegisterGUI() {
        setTitle("Register");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel gifBackgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                ImageIcon gifIcon = new ImageIcon("C:\\Users\\its1g\\Downloads\\200w (1).gif");
                Image gifImage = gifIcon.getImage();

                int panelWidth = getWidth();
                int panelHeight = getHeight();
                int imageWidth = gifIcon.getIconWidth();
                int imageHeight = gifIcon.getIconHeight();

                double scaleX = (double) panelWidth / imageWidth;
                double scaleY = (double) panelHeight / imageHeight;
                double scale = Math.max(scaleX, scaleY);

                AffineTransform transform = new AffineTransform();
                transform.scale(scale, scale);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.drawImage(gifImage, transform, this);
            }
        };
        gifBackgroundPanel.setLayout(new BorderLayout());

        JLabel nameLabel = createStyledLabel("Name:");
        JLabel usernameLabel = createStyledLabel("Username:");
        JLabel passwordLabel = createStyledLabel("Password:");
        JLabel profileLabel = createStyledLabel("Profile Description:");
        JLabel pictureLabel = createStyledLabel("Profile Picture URL:");

        nameField = createStyledTextField();
        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();
        profileArea = createStyledTextArea();
        JScrollPane profileScroll = new JScrollPane(profileArea);
        profileScroll.setOpaque(false);
        profileScroll.getViewport().setOpaque(false);
        pictureField = createStyledTextField();

        JButton registerButton = createStyledButton("Register");
        JButton backButton = createStyledButton("Back");

        registerButton.addActionListener(e -> doRegister());
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
        inputPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(usernameField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(passwordField, gbc);


        gbc.gridx = 0;
        gbc.gridy = 3;
        inputPanel.add(profileLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(profileScroll, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        inputPanel.add(pictureLabel, gbc);

        gbc.gridx = 1;
        inputPanel.add(pictureField, gbc);

        gifBackgroundPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gifBackgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(gifBackgroundPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Creates a styled label with a custom font and color.
     */
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        return label;
    }

    /**
     * Creates a styled text field with transparency and custom font.
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(0, 0, 0, 100));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        return field;
    }

    /**
     * Creates a styled password field.
     */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setForeground(Color.WHITE);
        field.setBackground(new Color(0, 0, 0, 100));
        field.setOpaque(false);
        field.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        return field;
    }

    /**
     * Creates a styled text area.
     */
    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(5, 20);
        area.setFont(new Font("Arial", Font.PLAIN, 14));
        area.setForeground(Color.WHITE);
        area.setBackground(new Color(0, 0, 0, 100));
        area.setOpaque(false);
        area.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        return area;
    }

    /**
     * Creates a styled button.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50, 150));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        return button;
    }

    /**
     * Handles the registration logic when the Register button is clicked.
     */
    private void doRegister() {
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String profile = profileArea.getText().trim();
        String picture = pictureField.getText().trim();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || profile.isEmpty() || picture.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters long.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!User.checkUserNameAvailability(username)) {
            JOptionPane.showMessageDialog(this, "Username is already taken. Please choose another.", "Registration Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = User.addUser(name, username, password, profile, picture);

        if (newUser != null) {
            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

            dispose();
            new WelcomeGUI();
        } else {
            JOptionPane.showMessageDialog(this, "An error occurred during registration. Please try again.", "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegisterGUI::new);
    }
}
