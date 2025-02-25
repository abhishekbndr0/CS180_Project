package src.main.app;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

/**
 * WelcomeGUI with Pacifico Font, Icon, and GIF Background
 *
 * @version 12/08/2024
 * @author Madhavan Prasanna
 */
public class WelcomeGUI extends JFrame implements WelcomeGUIInterface {

    public WelcomeGUI() {
        // Set the window title
        setTitle("Steezagram");
        setSize(800, 600); // Adjusted for full window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set the application icon
        setIconImage(new ImageIcon("C:\\Users\\its1g\\Downloads\\ezgif-7-d251ec8798.png").getImage());

        // Load the Pacifico font
        Font pacificoFont = loadCustomFont("C:\\Users\\its1g\\Downloads\\Pacifico\\Pacifico-Regular.ttf", 48f);

        // Panel to hold the GIF background
        JPanel gifBackgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Load the GIF
                ImageIcon gifIcon = new ImageIcon("C:\\Users\\its1g\\Downloads\\200w.gif");
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

        // Overlay panel for GUI components
        JPanel overlayPanel = new JPanel(new BorderLayout());
        overlayPanel.setOpaque(false);

        // Top section: Welcome title and caption
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel welcomeLabel = new JLabel("Steezagram", SwingConstants.CENTER);
        welcomeLabel.setFont(pacificoFont);
        welcomeLabel.setForeground(new Color(255, 87, 34));

        JLabel captionLabel = createStyledLabel("Steeze Only");
        captionLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        captionLabel.setForeground(Color.LIGHT_GRAY);

        titlePanel.add(welcomeLabel, BorderLayout.NORTH);
        titlePanel.add(captionLabel, BorderLayout.SOUTH);

        overlayPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        JButton loginButton = createStyledButton("Login");
        JButton registerButton = createStyledButton("Register");

        loginButton.addActionListener(e -> {
            dispose();
            new LoginGUI();
        });

        registerButton.addActionListener(e -> {
            dispose();
            new RegisterGUI();
        });

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        overlayPanel.add(buttonPanel, BorderLayout.SOUTH);

        gifBackgroundPanel.add(overlayPanel, BorderLayout.CENTER);

        add(gifBackgroundPanel, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Loads a custom font from the given path.
     */
    private Font loadCustomFont(String fontPath, float size) {
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath));
            return customFont.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }

    /**
     * Creates a styled label with a custom font and color.
     */
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setForeground(Color.WHITE);
        return label;
    }

    /**
     * Creates a styled button with transparency and custom font.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50, 150));
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        return button;
    }

    /**
     * Main method to launch the WelcomeGUI.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Apply FlatLaf look and feel
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception e) {
                System.err.println("Failed to initialize FlatLaf.");
            }
            new WelcomeGUI();
        });
    }
}
