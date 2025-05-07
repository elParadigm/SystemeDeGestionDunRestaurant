package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

// Custom JPanel class to draw a background image (copied from Login.java)
class BackgroundPanel extends JPanel {
    private BufferedImage backgroundImage;

    // Constructor that takes the image path
    public BackgroundPanel(String imagePath) {
        try {
            // Load the image from the specified path
            // Consider using getClass().getResourceAsStream() for resources bundled with your application
            backgroundImage = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            // Print an error message if the image cannot be loaded
            System.err.println("Error loading background image: " + e.getMessage());
            backgroundImage = null; // Set image to null if loading fails
        }
        // Set the panel to be non-opaque so the background is visible
        setOpaque(false);
        // Use GridBagLayout to center the content panel
        setLayout(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Paint the standard JPanel components first

        // If the background image was loaded successfully, draw it
        if (backgroundImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            // Draw the image scaled to the size of the panel
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}