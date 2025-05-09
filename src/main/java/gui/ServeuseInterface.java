package gui; // Declare the package

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File; // Keep File import for file-based loading
import javax.imageio.ImageIO; // Keep ImageIO import for image loading

// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // You might need this import depending on where BackgroundPanel is defined

// Import the Login class to allow returning to the login page
import gui.Login;
// Import the ClientMenuInterface class (assuming the server needs to access this)
import gui.ClientMenuInterface;
// Import the ServeuseOrderInterface class
import gui.ServeuseOrderInterface;


public class ServeuseInterface extends JFrame { // Changed class name

    // Define the colors used in the GUI (reusing from Login/Signup)
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6"); // Light beige/yellow background (Note: This color won't be fully visible with a background image)
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9"); // Slightly darker creamy beige for panels
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD"); // Very light white for input fields
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878"); // Muted green for buttons
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50); // Dark grey text
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE; // White text for buttons
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker(); // Darker shade of panel background for border

    // Constructor for the ServeuseInterface class
    public ServeuseInterface(int serveuseId) { // Changed constructor name
        // Set up the main window properties
        setTitle("Interface Serveuse"); // Changed window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setSize(1280, 720); // Window size
        setLocationRelativeTo(null); // Center the window on the screen
        // getContentPane().setBackground(COLOR_BACKGROUND); // No need to set background here, handled by BackgroundPanel

        // Create the main panel using the custom BackgroundPanel with an image
        // Assuming BackgroundPanel is defined elsewhere and accessible.
        // IMPORTANT: Replace "path/to/your/background_image.jpg" with the actual path to your image file
        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg"); // Update path if needed
        // mainPanel.setBackground(COLOR_BACKGROUND); // Background color is handled by the image
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for layout within the background panel

        // Create a panel to hold the main content (welcome label and buttons)
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(COLOR_PANEL_BACKGROUND); // Set background color
        contentPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for internal layout
        // Set a compound border: a line border and an empty border for padding
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1), // Outer line border
                BorderFactory.createEmptyBorder(40, 60, 40, 60) // Inner padding
        ));


        // GridBagConstraints for controlling component placement in contentPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.anchor = GridBagConstraints.CENTER; // Center components by default

        // Welcome Label (Updated Text)
        JLabel welcomeLabel = new JLabel("Bonjour , Serveuse"); // Changed welcome text
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Set font and size
        welcomeLabel.setForeground(COLOR_TEXT_DARK); // Set text color
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.insets = new Insets(10, 10, 40, 10); // Adjust padding below the label
        contentPanel.add(welcomeLabel, gbc);

        // Reset gridwidth and adjust insets for buttons
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 20, 10, 20); // More horizontal padding between buttons

        // "Voir le menu" Button (Text remains the same, action updated)
        JButton viewMenuButton = new JButton("voir le menu");
        viewMenuButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set font and size
        viewMenuButton.setBackground(COLOR_BUTTON_BACKGROUND); // Set background color
        viewMenuButton.setForeground(COLOR_BUTTON_TEXT); // Set text color
        viewMenuButton.setFocusPainted(false); // Remove focus border
        viewMenuButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30)); // Add padding
        viewMenuButton.setOpaque(true); // Make the background visible
        viewMenuButton.setBorderPainted(false); // Do not paint the border
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Row 1
        contentPanel.add(viewMenuButton, gbc);

        // "Voir command" Button (Text remains the same)
        JButton viewOrdersButton = new JButton("voir command"); // Text is already "voir command"
        viewOrdersButton.setFont(new Font("Arial", Font.BOLD, 18)); // Set font and size
        viewOrdersButton.setBackground(COLOR_BUTTON_BACKGROUND); // Set background color
        viewOrdersButton.setForeground(COLOR_BUTTON_TEXT); // Set text color
        viewOrdersButton.setFocusPainted(false); // Remove focus border
        viewOrdersButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30)); // Add padding
        viewOrdersButton.setOpaque(true); // Make the background visible
        viewOrdersButton.setBorderPainted(false); // Do not paint the border
        gbc.gridx = 1; // Column 1
        gbc.gridy = 1; // Row 1
        contentPanel.add(viewOrdersButton, gbc);

        // Add the central content panel to the main background panel, centered
        GridBagConstraints mainPanelGbc = new GridBagConstraints();
        mainPanelGbc.gridx = 0;
        mainPanelGbc.gridy = 0;
        mainPanelGbc.weightx = 1.0; // Allow horizontal expansion
        mainPanelGbc.weighty = 1.0; // Allow vertical expansion
        mainPanelGbc.anchor = GridBagConstraints.CENTER; // Center the content panel
        mainPanel.add(contentPanel, mainPanelGbc);

        // Back Icon Label (top left) - Now added to mainPanel
        JLabel backIconLabel = new JLabel(); // Create a JLabel to hold the icon
        backIconLabel.setOpaque(false); // Make the label transparent

        // Load the image icon
        ImageIcon backIcon = null;
        try {
            // Load from a file:
            // IMPORTANT: Update this path to where your back arrow icon is located
            Image img = ImageIO.read(new File("arrow.png")); // Load image from file
            // Removed scaling to keep original icon size
            // Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Resize to 30x30 pixels
            backIcon = new ImageIcon(img); // Use original image

        } catch (Exception e) {
            System.err.println("Error loading back arrow icon: " + e.getMessage());
        }

        if (backIcon != null) {
            backIconLabel.setIcon(backIcon); // Set the loaded icon
            backIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover
            backIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

            backIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Back icon clicked!");
                    ServeuseInterface.this.dispose(); // Changed class reference
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new Login().setVisible(true);
                        }
                    });
                }
            });

            // Add the back icon label to the mainPanel using GridBagLayout
            GridBagConstraints iconGbc = new GridBagConstraints();
            iconGbc.gridx = 0; // Column 0
            iconGbc.gridy = 0; // Row 0
            iconGbc.anchor = GridBagConstraints.NORTHWEST; // Position at top-left
            iconGbc.insets = new Insets(10, 10, 0, 0); // Add padding around the icon
            mainPanel.add(backIconLabel, iconGbc);
        } else {
            // Fallback text label if icon loading fails
            JLabel fallbackBackLabel = new JLabel("←");
            fallbackBackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            fallbackBackLabel.setForeground(COLOR_TEXT_DARK);
            fallbackBackLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            fallbackBackLabel.setOpaque(false); // Make the fallback label transparent
            fallbackBackLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Back button (fallback) clicked!");
                    ServeuseInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new Login().setVisible(true);
                        }
                    });
                }
            });
            // Add the fallback label to the mainPanel
            GridBagConstraints fallbackGbc = new GridBagConstraints();
            fallbackGbc.gridx = 0; // Column 0
            fallbackGbc.gridy = 0; // Row 0
            fallbackGbc.anchor = GridBagConstraints.NORTHWEST; // Position at top-left
            fallbackGbc.insets = new Insets(10, 10, 0, 0); // Add padding
            mainPanel.add(fallbackBackLabel, fallbackGbc);
            // Add the main background panel to the JFrame
            add(mainPanel);
            return; // Exit constructor if fallback is used
        }

        // Add the main background panel to the JFrame
        add(mainPanel);


        // Add ActionListeners to the buttons
        viewMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Voir le menu button clicked!");
                // Close the current ServeuseInterface window
                ServeuseInterface.this.dispose(); // Changed class reference
                // Open the ClientMenuInterface window
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new ClientMenuInterface(serveuseId).setVisible(true); // Changed to ClientMenuInterface
                    }
                });
            }
        });

        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Voir command button clicked!");
                // Close the current ServeuseInterface window
                ServeuseInterface.this.dispose();
                // Open the ServeuseOrderInterface window
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new ServeuseOrderInterface(serveuseId).setVisible(true);
                    }
                });
            }
        });
    }

    // The main method is typically in your main application file,
    // but included here for standalone testing purposes.
    // In your main project, you will call this class from your main method.

}
