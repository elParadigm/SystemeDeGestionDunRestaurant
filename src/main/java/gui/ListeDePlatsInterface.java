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

// Custom JDialog for adding or modifying dish details
class DishDetailsDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JButton selectImageButton;
    private JButton saveButton;
    private JButton cancelButton;

    private boolean isModification = false; // Flag to indicate if it's a modification
    private DishPanel dishPanelToModify; // Reference to the panel being modified
    private ListeDePlatsInterface parentInterface; // Reference to the main interface

    // Constructor for adding a new dish
    public DishDetailsDialog(JFrame parent, ListeDePlatsInterface parentInterface) {
        super(parent, "Ajouter un nouveau plat", true); // Modal dialog
        this.parentInterface = parentInterface;
        setupDialog();
    }

    // Constructor for modifying an existing dish
    public DishDetailsDialog(JFrame parent, DishPanel dishPanelToModify, ListeDePlatsInterface parentInterface) {
        super(parent, "Modifier le plat", true); // Modal dialog
        this.dishPanelToModify = dishPanelToModify;
        this.parentInterface = parentInterface;
        isModification = true;
        setupDialog();
        populateFields(); // Populate fields with existing dish data
    }

    private void setupDialog() {
        setSize(400, 350);
        setLocationRelativeTo(getParent());
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(250, 246, 233)); // Match color scheme

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally

        // Name Label and Field
        JLabel nameLabel = new JLabel("Nom du plat:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; add(nameField, gbc);

        // Description Label and Area
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descriptionLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.weightx = 0.0; add(descriptionLabel, gbc);

        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; add(descriptionScrollPane, gbc);

        // Image Path Label and Field
        JLabel imagePathLabel = new JLabel("Chemin de l'image:");
        imagePathLabel.setFont(new Font("Arial", Font.BOLD, 14));
        imagePathLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weighty = 0.0; gbc.fill = GridBagConstraints.NONE; add(imagePathLabel, gbc);

        imagePathField = new JTextField(20);
        imagePathField.setFont(new Font("Arial", Font.PLAIN, 14));
        imagePathField.setEditable(false); // Make it non-editable, only selectable via button
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; add(imagePathField, gbc);

        // Select Image Button
        selectImageButton = new JButton("Choisir une image");
        selectImageButton.setFont(new Font("Arial", Font.BOLD, 12));
        selectImageButton.setBackground(new Color(160, 200, 120)); // Light green
        selectImageButton.setForeground(Color.WHITE);
        selectImageButton.setFocusPainted(false);
        selectImageButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        selectImageButton.setOpaque(true);
        selectImageButton.setBorderPainted(false);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST; add(selectImageButton, gbc);

        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choisir une image pour le plat");
                // Optional: Set file filters for image types
                // fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
                int userSelection = fileChooser.showOpenDialog(DishDetailsDialog.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });


        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(250, 246, 233)); // Match color scheme
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; add(buttonPanel, gbc);

        // Save Button
        saveButton = new JButton(isModification ? "Sauvegarder les modifications" : "Ajouter le plat");
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setBackground(new Color(60, 179, 113)); // Medium sea green color
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                String imagePath = imagePathField.getText().trim();

                if (name.isEmpty() || description.isEmpty() || imagePath.isEmpty()) {
                    JOptionPane.showMessageDialog(DishDetailsDialog.this, "Veuillez remplir tous les champs.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (isModification) {
                    // Update the existing dish panel
                    if (dishPanelToModify != null) {
                        dishPanelToModify.updateDishDetails(imagePath, name, description);
                    }
                } else {
                    // Add a new dish panel by calling the public method in the parent interface
                    parentInterface.addDish(new DishPanel(imagePath, name, description, parentInterface)); // Pass parent interface reference
                }

                dispose(); // Close the dialog
            }
        });
        buttonPanel.add(saveButton);

        // Cancel Button
        cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });
        buttonPanel.add(cancelButton);
    }

    // Populate fields when modifying an existing dish
    private void populateFields() {
        if (dishPanelToModify != null) {
            nameField.setText(dishPanelToModify.getDishName());
            descriptionArea.setText(dishPanelToModify.getDishDescription());
            imagePathField.setText(dishPanelToModify.getImagePath()); // Assuming DishPanel stores image path
        }
    }
}


// Custom JPanel for displaying a single dish item
class DishPanel extends JPanel {
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel descriptionLabel;
    private JButton removeButton;
    private JButton modifyButton; // Added Modify button
    // Changed parentPanel to parentInterface reference
    private ListeDePlatsInterface parentInterface;

    // Store dish details locally for modification
    private String currentImagePath;
    private String currentDishName;
    private String currentDishDescription;

    // Updated constructor to accept ListeDePlatsInterface
    public DishPanel(String imageName, String name, String description, ListeDePlatsInterface parentInterface) {
        this.parentInterface = parentInterface;
        this.currentImagePath = imageName; // Store initial values
        this.currentDishName = name;
        this.currentDishDescription = description;

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
        updateImage(currentImagePath); // Load and set the initial image
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Name Label
        nameLabel = new JLabel(currentDishName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Description Label
        descriptionLabel = new JLabel("<html><body style='text-align:center;'>" + currentDishDescription + "</body></html>"); // Use HTML for centering text
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionLabel.setForeground(new Color(80, 80, 80));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Button Panel for Remove and Modify
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0)); // FlowLayout for buttons
        buttonPanel.setOpaque(false); // Make panel transparent

        // Modify Button
        modifyButton = new JButton("Modifier");
        modifyButton.setFont(new Font("Arial", Font.BOLD, 10)); // Smaller font for buttons
        modifyButton.setBackground(new Color(255, 165, 0)); // Orange color
        modifyButton.setForeground(Color.WHITE); // White text
        modifyButton.setFocusPainted(false);
        modifyButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        modifyButton.setOpaque(true);
        modifyButton.setBorderPainted(false);
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the modification dialog, passing the parent interface
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(DishPanel.this);
                if (parentFrame instanceof ListeDePlatsInterface) {
                    DishDetailsDialog dialog = new DishDetailsDialog(parentFrame, DishPanel.this, (ListeDePlatsInterface) parentFrame);
                    dialog.setVisible(true);
                }
            }
        });
        buttonPanel.add(modifyButton);


        // Remove Button
        removeButton = new JButton("Supprimer");
        removeButton.setFont(new Font("Arial", Font.BOLD, 10)); // Smaller font for buttons
        removeButton.setBackground(Color.RED); // Red background
        removeButton.setForeground(Color.WHITE); // White text
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action to remove this dish panel by calling a method in the parent interface
                if (parentInterface != null) {
                    parentInterface.removeDishPanel(DishPanel.this); // Call remove method in parent interface
                }
            }
        });
        buttonPanel.add(removeButton);


        // Add components to the DishPanel
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        add(imageLabel);
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        add(nameLabel);
        add(Box.createRigidArea(new Dimension(0, 3))); // Spacer
        add(descriptionLabel);
        add(Box.createVerticalGlue()); // Push components to the top
        add(buttonPanel); // Add the panel containing the buttons
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
    }

    // Method to update the dish details displayed in the panel
    public void updateDishDetails(String imageName, String name, String description) {
        this.currentImagePath = imageName;
        this.currentDishName = name;
        this.currentDishDescription = description;

        updateImage(currentImagePath); // Update the image
        nameLabel.setText(currentDishName); // Update name label
        descriptionLabel.setText("<html><body style='text-align:center;'>" + currentDishDescription + "</body></html>"); // Update description label

        revalidate(); // Re-layout the panel
        repaint(); // Repaint the panel
    }

    // Helper method to load and scale the image
    private void updateImage(String imageName) {
        ImageIcon dishIcon = null;
        try {
            // Load from a file:
            // IMPORTANT: Update this path to where your dish images are located
            Image img = ImageIO.read(new File(imageName)); // Load image from file
            // Scaling to fit the image within the panel
            Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Scale image to 100x100
            dishIcon = new ImageIcon(scaledImg); // Use the scaled image

        } catch (Exception e) {
            System.err.println("Error loading dish image: " + e.getMessage());
            // Fallback to text if loading fails
            imageLabel.setText("Image");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dishIcon = null; // Ensure icon is null on failure
        }

        imageLabel.setIcon(dishIcon); // Set the icon (or null if loading failed)
    }

    // Getters for current dish details (used by the modification dialog)
    public String getImagePath() {
        return currentImagePath;
    }

    public String getDishName() {
        return currentDishName;
    }

    public String getDishDescription() {
        return currentDishDescription;
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

    private JPanel dishesContainerPanel; // Panel to hold the grid of dishes

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
                // Open the dialog to add a new dish
                DishDetailsDialog dialog = new DishDetailsDialog(ListeDePlatsInterface.this, ListeDePlatsInterface.this);
                dialog.setVisible(true);
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
        addDish(new DishPanel("row-1-column-1.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", this)); // Pass parent interface
        addDish(new DishPanel("row-1-column-2.png", "Spaghetti Bolognese", "Pasta with meat sauce", this)); // Pass parent interface
        addDish(new DishPanel("row-1-column-3.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", this)); // Pass parent interface
        addDish(new DishPanel("row-1-column-4.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", this)); // Pass parent interface
        addDish(new DishPanel("row-2-column-1.png", "Spaghetti Bolognese", "Pasta with meat sauce", this)); // Pass parent interface
        addDish(new DishPanel("row-2-column-2.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", this)); // Pass parent interface
        addDish(new DishPanel("row-2-column-3.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", this)); // Pass parent interface
        addDish(new DishPanel("row-2-column-4.png", "Spaghetti Bolognese", "Pasta with meat sauce", this)); // Pass parent interface
        addDish(new DishPanel("row-3-column-1.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", this)); // Pass parent interface
        addDish(new DishPanel("row-3-column-2.png", "Nouveau Plat", "Description du nouveau plat", this)); // Pass parent interface
        addDish(new DishPanel("row-3-column-3.png", "Nouveau Plat", "Description du nouveau plat", this)); // Pass parent interface
        addDish(new DishPanel("row-3-column-4.png", "Nouveau Plat", "Description du nouveau plat", this)); // Pass parent interface


    }

    // Method to add a dish panel to the container (called by the dialog)
    public void addDish(DishPanel dishPanel) { // Made public
        dishesContainerPanel.add(dishPanel);
        dishesContainerPanel.revalidate(); // Re-layout the container
        dishesContainerPanel.repaint(); // Repaint the container
    }

    // Method to remove a dish panel from the container (called by the DishPanel)
    public void removeDishPanel(DishPanel dishPanel) {
        dishesContainerPanel.remove(dishPanel);
        dishesContainerPanel.revalidate();
        dishesContainerPanel.repaint();
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
