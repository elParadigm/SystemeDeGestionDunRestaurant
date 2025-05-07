package gui; // Declare the package

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File; // Keep File import for file-based loading
import javax.imageio.ImageIO; // Keep ImageIO import for image loading

import java.util.ArrayList; // To manage the list of dishes
import java.util.List; // To manage the list of dishes

// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // You might need this import depending on where BackgroundPanel is defined

// Import the CuisinierInterface class to allow returning
import gui.CuisinierInterface;

// Custom JPanel for displaying a single dish item
class DishPanel extends JPanel {
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel descriptionLabel;
    private JButton removeButton;
    private JPanel parentPanel; // Reference to the panel containing this dish

    public DishPanel(String imageName, String name, String description, JPanel parentPanel) {
        this.parentPanel = parentPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Arrange components vertically
        setBackground(new Color(250, 246, 233)); // Use a color similar to the panel background
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 200), 1), // Light border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
        ));
        setPreferredSize(new Dimension(150, 200)); // Set a preferred size for each dish panel
        setMaximumSize(new Dimension(150, 200)); // Set maximum size
        setMinimumSize(new Dimension(150, 200)); // Set minimum size


        // Image Label
        imageLabel = new JLabel();
        // Attempt to load the image from a file and scale it
        ImageIcon dishIcon = null;
        try {
            // Load from a file:
            // IMPORTANT: Update this path to where your dish images are located
            Image img = ImageIO.read(new File(imageName)); // Load image from file
            // Re-introduced scaling to fit the image within the panel
            Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Scale image to 100x100
            dishIcon = new ImageIcon(scaledImg); // Use the scaled image

        } catch (Exception e) {
            System.err.println("Error loading dish image: " + e.getMessage());
            // Fallback to text if loading fails
            imageLabel.setText("Image");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        if (dishIcon != null) {
            imageLabel.setIcon(dishIcon);
        }
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Name Label
        nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Description Label
        descriptionLabel = new JLabel("<html><body style='text-align:center;'>" + description + "</body></html>"); // Use HTML for centering text
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionLabel.setForeground(new Color(80, 80, 80));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Remove Button
        removeButton = new JButton("Supprimer");
        removeButton.setFont(new Font("Arial", Font.BOLD, 12));
        removeButton.setBackground(Color.RED); // Red background
        removeButton.setForeground(Color.WHITE); // White text
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action to remove this dish panel
                if (parentPanel != null) {
                    parentPanel.remove(DishPanel.this); // Remove this panel from its parent
                    parentPanel.revalidate(); // Re-layout the parent panel
                    parentPanel.repaint(); // Repaint the parent panel
                }
            }
        });

        // Add components to the DishPanel
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        add(imageLabel);
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        add(nameLabel);
        add(Box.createRigidArea(new Dimension(0, 3))); // Spacer
        add(descriptionLabel);
        add(Box.createVerticalGlue()); // Push components to the top
        add(removeButton);
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
    }
}


public class ListeDePlatsInterface extends JFrame { // Changed class name

    // Define the colors used in the GUI (reusing from other interfaces)
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    private JPanel dishesContainerPanel; // Panel to hold the dish items

    // Constructor for the ListeDePlatsInterface class
    public ListeDePlatsInterface() {
        // Set up the main window properties
        setTitle("Liste de Plats"); // Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setSize(1280, 720); // Window size
        setLocationRelativeTo(null); // Center the window on the screen

        // Create the main panel using the custom BackgroundPanel with an image
        // Assuming BackgroundPanel is defined elsewhere and accessible.
        // IMPORTANT: Replace "path/to/your/background_image.jpg" with the actual path to your image file
        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg"); // Update path if needed
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for layout within the background panel

        // GridBagConstraints for controlling component placement in mainPanel
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        mainGbc.anchor = GridBagConstraints.CENTER; // Center components by default

        // Back Icon Label (top left)
        JLabel backIconLabel = new JLabel(); // Create a JLabel to hold the icon
        backIconLabel.setOpaque(false); // Make the label transparent

        // Load the back arrow icon from a file
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
                    ListeDePlatsInterface.this.dispose(); // Close current window
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new CuisinierInterface().setVisible(true); // Open CuisinierInterface
                        }
                    });
                }
            });

            // Add the back icon label to the mainPanel
            mainGbc.gridx = 0; // Column 0
            mainGbc.gridy = 0; // Row 0
            mainGbc.anchor = GridBagConstraints.NORTHWEST; // Position at top-left
            mainGbc.insets = new Insets(10, 10, 0, 0); // Add padding
            mainPanel.add(backIconLabel, mainGbc);
        } else {
            // Fallback text label if icon loading fails
            JLabel fallbackBackLabel = new JLabel("←");
            fallbackBackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            fallbackBackLabel.setForeground(COLOR_TEXT_DARK);
            fallbackBackLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            fallbackBackLabel.setOpaque(false);
            fallbackBackLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Back button (fallback) clicked!");
                    ListeDePlatsInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new CuisinierInterface().setVisible(true);
                        }
                    });
                }
            });
            mainGbc.gridx = 0; mainGbc.gridy = 0;
            mainGbc.anchor = GridBagConstraints.NORTHWEST;
            mainGbc.insets = new Insets(10, 10, 0, 0);
            mainPanel.add(fallbackBackLabel, mainGbc);
        }


        // Title Label
        JLabel titleLabel = new JLabel("Liste de Plats");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 0; // Row 0
        mainGbc.gridwidth = 2; // Span across columns (adjust if more columns are added)
        mainGbc.anchor = GridBagConstraints.NORTH; // Align to the top
        mainGbc.insets = new Insets(20, 10, 20, 10); // Padding
        mainPanel.add(titleLabel, mainGbc);

        // Panel to hold the grid of dishes
        dishesContainerPanel = new JPanel();
        dishesContainerPanel.setBackground(new Color(250, 246, 233, 150)); // Semi-transparent background
        // Changed to GridLayout with 0 rows (automatic) and 4 columns, with horizontal and vertical gaps
        dishesContainerPanel.setLayout(new GridLayout(0, 4, 20, 20)); // Rows=0 (automatic), Cols=4, Hgap=20, Vgap=20
        // Use a JScrollPane to make the dish list scrollable if it exceeds the panel size
        JScrollPane scrollPane = new JScrollPane(dishesContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // No horizontal scroll
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.setOpaque(false); // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make viewport transparent


        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 1; // Row 1 (below the title)
        mainGbc.gridwidth = 2; // Span across columns
        mainGbc.weightx = 1.0; // Allow horizontal expansion
        // Increased weighty to make the scroll pane take more vertical space
        mainGbc.weighty = 5.0; // Give more weight to the scroll pane's vertical expansion
        mainGbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
        // Increased horizontal insets to provide more space for FlowLayout (now GridLayout)
        mainGbc.insets = new Insets(10, 50, 10, 50); // Increased horizontal padding
        mainPanel.add(scrollPane, mainGbc);


        // Add "+" icon to add new plates
        JLabel addPlateLabel = new JLabel();
        addPlateLabel.setOpaque(false);
        addPlateLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ImageIcon plusIcon = null;
        try {
            // Load from a file:
            // IMPORTANT: Update this path to where your plus icon is located
            Image img = ImageIO.read(new File("add.png")); // Load image from file
            // Removed scaling to keep original icon size
            // Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Resize
            plusIcon = new ImageIcon(img); // Use original image

        } catch (Exception e) {
            System.err.println("Error loading plus icon: " + e.getMessage());
        }

        if (plusIcon != null) {
            addPlateLabel.setIcon(plusIcon);
        } else {
            // Fallback text if icon loading fails
            addPlateLabel.setText("+");
            addPlateLabel.setFont(new Font("Arial", Font.BOLD, 40));
            addPlateLabel.setForeground(COLOR_TEXT_DARK);
        }

        addPlateLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Add Plate icon clicked!");
                // Action to add a new plate - currently adds a placeholder dish
                addNewDish();
            }
        });

        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 2; // Row 2 (below the dish container)
        mainGbc.gridwidth = 2; // Span across columns
        mainGbc.anchor = GridBagConstraints.SOUTH; // Align to the bottom
        mainGbc.fill = GridBagConstraints.NONE; // Do not fill
        // Set weighty to 0 for the add button to ensure the scroll pane takes available space
        mainGbc.weighty = 0.0;
        mainGbc.insets = new Insets(20, 10, 20, 10); // Padding
        mainPanel.add(addPlateLabel, mainGbc);


        // Add the main background panel to the JFrame
        add(mainPanel);

        // Example: Add some initial placeholder dishes
        // IMPORTANT: Update the image paths for these placeholder dishes
        addDish(new DishPanel("row-1-column-1.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", dishesContainerPanel));
        addDish(new DishPanel("row-1-column-2.png", "Spaghetti Bolognese", "Pasta with meat sauce", dishesContainerPanel));
        addDish(new DishPanel("row-1-column-3.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", dishesContainerPanel));
        addDish(new DishPanel("row-1-column-4.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", dishesContainerPanel));
        addDish(new DishPanel("row-2-column-1.png", "Spaghetti Bolognese", "Pasta with meat sauce", dishesContainerPanel));
        addDish(new DishPanel("row-2-column-2.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", dishesContainerPanel));
        addDish(new DishPanel("row-2-column-3.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", dishesContainerPanel));
        addDish(new DishPanel("row-2-column-4.png", "Spaghetti Bolognese", "Pasta with meat sauce", dishesContainerPanel));
        addDish(new DishPanel("row-3-column-1.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", dishesContainerPanel));
        addDish(new DishPanel("row-3-column-2.png", "Nouveau Plat", "Description du nouveau plat", dishesContainerPanel)); // Add a few more to ensure scrolling is possible
        addDish(new DishPanel("row-3-column-3.png", "Nouveau Plat", "Description du nouveau plat", dishesContainerPanel));
        addDish(new DishPanel("row-3-column-4.png", "Nouveau Plat", "Description du nouveau plat", dishesContainerPanel));


    }

    // Method to add a dish panel to the container
    private void addDish(DishPanel dishPanel) {
        dishesContainerPanel.add(dishPanel);
        dishesContainerPanel.revalidate(); // Re-layout the container
        dishesContainerPanel.repaint(); // Repaint the container
    }

    // Method to add a new placeholder dish (called by the "+" icon)
    private void addNewDish() {
        // In a real application, this would open a dialog to enter dish details
        // For now, adding a generic placeholder dish
        addDish(new DishPanel("default_dish.png", "Nouveau Plat", "Description du nouveau plat", dishesContainerPanel));
    }


    // The main method is typically in your main application file,
    // but included here for standalone testing purposes.
    public static void main(String[] args) {
        // Set the look and feel to Nimbus if available
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ListeDePlatsInterface().setVisible(true);
            }
        });
    }
}
