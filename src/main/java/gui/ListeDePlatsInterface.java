package gui; // Declare the package

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException; // Import IOException for image handling
import java.io.ByteArrayInputStream; // Import ByteArrayInputStream
import java.io.FileInputStream; // Import FileInputStream for reading image file

import java.util.ArrayList;
import java.util.List;

// Import your Plat model class and PlatDAO class
import model.Plat; // Assuming your Plat model is in a 'model' package
import dao.PlatDAO; // Assuming your PlatDAO is in a 'dao' package

// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // You might need this import depending on where BackgroundPanel is defined

// Import the CuisinierInterface class to allow returning
import gui.CuisinierInterface;


// Custom JDialog for adding or modifying dish details
class DishDetailsDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField imagePathField; // Still used for selecting file path initially
    private JButton selectImageButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField priceField; // Added field for price
    private JTextField menuIdField; // Added field for menu ID

    private byte[] selectedImageData = null; // To hold selected image data before saving
    private boolean isModification = false; // Flag to indicate if it's a modification
    private DishPanel dishPanelToModify; // Reference to the panel being modified
    private ListeDePlatsInterface parentInterface; // Reference to the main interface

    // Constructor for adding a new dish
    public DishDetailsDialog(JFrame parent, ListeDePlatsInterface parentInterface) {
        super(parent, "Ajouter un nouveau plat", true); // Modal dialog
        this.parentInterface = parentInterface;
        setupDialog();
        // Set default state for adding
        saveButton.setText("Ajouter le plat");
    }

    // Constructor for modifying an existing dish
    public DishDetailsDialog(JFrame parent, DishPanel dishPanelToModify, ListeDePlatsInterface parentInterface) {
        super(parent, "Modifier le plat", true); // Modal dialog
        this.dishPanelToModify = dishPanelToModify;
        this.parentInterface = parentInterface;
        isModification = true;
        setupDialog();
        // Set default state for modification
        saveButton.setText("Sauvegarder les modifications");
        populateFields(); // Populate fields with existing dish data
    }

    private void setupDialog() {
        setSize(400, 450); // Increased dialog size to accommodate new fields
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

        // Price Label and Field
        JLabel priceLabel = new JLabel("Prix:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weighty = 0.0; add(priceLabel, gbc);

        priceField = new JTextField(10);
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; add(priceField, gbc);

        // Menu ID Label and Field
        JLabel menuIdLabel = new JLabel("ID Menu:");
        menuIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        menuIdLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weighty = 0.0; add(menuIdLabel, gbc);

        menuIdField = new JTextField(10);
        menuIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; add(menuIdField, gbc);


        // Image Path Label and Field
        JLabel imagePathLabel = new JLabel("Image (laissez vide pour ne pas changer):"); // Updated text
        imagePathLabel.setFont(new Font("Arial", Font.BOLD, 14));
        imagePathLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.weighty = 0.0; gbc.fill = GridBagConstraints.NONE; add(imagePathLabel, gbc);

        imagePathField = new JTextField(20);
        imagePathField.setFont(new Font("Arial", Font.PLAIN, 14));
        imagePathField.setEditable(false); // Make it non-editable, only selectable via button
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; add(imagePathField, gbc);

        // Select Image Button
        selectImageButton = new JButton("Choisir une image");
        selectImageButton.setFont(new Font("Arial", Font.BOLD, 12));
        selectImageButton.setBackground(new Color(160, 200, 120)); // Light green
        selectImageButton.setForeground(Color.WHITE);
        selectImageButton.setFocusPainted(false);
        selectImageButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        selectImageButton.setOpaque(true);
        selectImageButton.setBorderPainted(false);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST; add(selectImageButton, gbc);

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
                    try (FileInputStream fis = new FileInputStream(selectedFile)) {
                        selectedImageData = new byte[(int) selectedFile.length()];
                        fis.read(selectedImageData);
                    } catch (IOException ex) {
                        selectedImageData = null; // Clear data on error
                        JOptionPane.showMessageDialog(DishDetailsDialog.this, "Erreur de lecture de l'image: " + ex.getMessage(), "Erreur d'image", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });


        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(250, 246, 233)); // Match color scheme
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; add(buttonPanel, gbc);

        // Save Button
        // Text is set in constructors based on isModification flag
        saveButton = new JButton();
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
                String priceText = priceField.getText().trim(); // Get price text
                String menuIdText = menuIdField.getText().trim(); // Get menu ID text

                if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || menuIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(DishDetailsDialog.this, "Veuillez remplir tous les champs (excepté le chemin de l'image si vous ne la changez pas).", "Champs manquants", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double price;
                int menuId;
                try {
                    price = Double.parseDouble(priceText);
                    menuId = Integer.parseInt(menuIdText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(DishDetailsDialog.this, "Le prix et l'ID du menu doivent être des nombres valides.", "Erreur de format", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // --- DATABASE INTEGRATION: Save/Update Plat ---
                PlatDAO platDAO = new PlatDAO(); // Assuming you have a PlatDAO class

                // Create or update the Plat object
                Plat platToSave;
                if (isModification) {
                    // Get the existing Plat object from the panel being modified
                    platToSave = dishPanelToModify.getPlat();
                } else {
                    // Create a new Plat object for adding
                    platToSave = new Plat(); // Assuming your Plat model has a no-arg constructor
                }

                // Update the Plat object's properties from the fields
                platToSave.setNom(name);
                platToSave.setDescription(description);
                platToSave.setPrix(price);
                platToSave.setIdMenu(menuId);

                // Only update image data if a new image was selected
                if (selectedImageData != null) {
                    platToSave.setImage(selectedImageData);
                } else if (!isModification) {
                    // For adding, image is mandatory (based on initial check),
                    // but if selectedImageData is null here, it means selection failed.
                    // This case should ideally be handled by the initial check.
                    // Add a fallback or re-check image presence if needed.
                    if(platToSave.getImage() == null || platToSave.getImage().length == 0){
                        JOptionPane.showMessageDialog(DishDetailsDialog.this, "Veuillez choisir une image pour un nouveau plat.", "Image manquante", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                // If modifying and selectedImageData is null, the existing image is kept.


                boolean success = false;
                try {
                    if (isModification) {
                        // Call DAO method to update the existing plat
                        success = platDAO.updatePlat(platToSave); // Assuming updatePlat returns boolean
                    } else {
                        // Call DAO method to add the new plat
                        int generatedId = platDAO.addPlat(platToSave); // Assuming addPlat returns generated ID
                        if (generatedId != -1) {
                            platToSave.setIdPlat(generatedId); // Set the generated ID
                            success = true; // Mark as success if ID was generated
                        } else {
                            success = false;
                        }
                    }

                    if (success) {
                        if (isModification) {
                            // Update the displayed dish panel in the main list
                            dishPanelToModify.updateDishDetails(platToSave);
                            JOptionPane.showMessageDialog(DishDetailsDialog.this, "Plat modifié avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            // Add a new dish panel to the GUI for the newly added plat
                            parentInterface.addDish(new DishPanel(platToSave, parentInterface)); // Add the new panel
                            JOptionPane.showMessageDialog(DishDetailsDialog.this, "Plat ajouté avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        }
                        dispose(); // Close the dialog on success
                    } else {
                        // Specific error message if DAO operation failed
                        JOptionPane.showMessageDialog(DishDetailsDialog.this,
                                (isModification ? "Échec de la mise à jour" : "Échec de l'ajout") + " du plat dans la base de données.",
                                "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) { // Catch general exceptions from DAO operations
                    JOptionPane.showMessageDialog(DishDetailsDialog.this, "Erreur lors de l'opération sur le plat: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
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
            // Get the Plat object from the panel
            Plat plat = dishPanelToModify.getPlat(); // Assuming DishPanel stores the Plat object
            if (plat != null) {
                nameField.setText(plat.getNom());
                descriptionArea.setText(plat.getDescription());
                priceField.setText(String.valueOf(plat.getPrix())); // Populate price
                menuIdField.setText(String.valueOf(plat.getIdMenu())); // Populate menu ID
                // Image path is not stored; the user can select a new image if needed.
                imagePathField.setText("");
                selectedImageData = plat.getImage(); // Store existing image data in case no new image is selected
            }
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
    private ListeDePlatsInterface parentInterface;

    // Store the associated Plat object
    private Plat plat;

    // Updated constructor to accept a Plat object and ListeDePlatsInterface
    public DishPanel(Plat plat, ListeDePlatsInterface parentInterface) {
        this.plat = plat; // Store the Plat object
        this.parentInterface = parentInterface;

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
        updateImage(plat.getImage()); // Load and set the image from Plat object's byte array
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Name Label
        nameLabel = new JLabel(plat.getNom());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

        // Description Label
        descriptionLabel = new JLabel("<html><body style='text-align:center;'>" + plat.getDescription() + "</body></html>"); // Use HTML for centering text
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
                // Open the modification dialog, passing this DishPanel and the parent interface
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
                // --- DATABASE INTEGRATION: Delete Plat ---
                int confirm = JOptionPane.showConfirmDialog(parentInterface,
                        "Êtes-vous sûr de vouloir supprimer le plat '" + plat.getNom() + "'?",
                        "Confirmer la suppression",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    PlatDAO platDAO = new PlatDAO(); // Assuming you have a PlatDAO class
                    // Assuming deletePlat returns boolean indicating success
                    boolean success = platDAO.deletePlat(plat.getIdPlat());

                    if (success) {
                        // Action to remove this dish panel from the GUI
                        if (parentInterface != null) {
                            parentInterface.removeDishPanel(DishPanel.this); // Call remove method in parent interface
                            JOptionPane.showMessageDialog(parentInterface, "Plat supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(parentInterface, "Échec de la suppression du plat dans la base de données.", "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                    }
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

    // Method to update the dish details displayed in the panel from a Plat object
    public void updateDishDetails(Plat updatedPlat) {
        this.plat = updatedPlat; // Update the stored Plat object

        updateImage(this.plat.getImage()); // Update the image from the updated Plat object's byte array
        nameLabel.setText(this.plat.getNom()); // Update name label
        descriptionLabel.setText("<html><body style='text-align:center;'>" + this.plat.getDescription() + "</body></html>"); // Update description label

        revalidate(); // Re-layout the panel
        repaint(); // Repaint the panel
    }

    // Helper method to load and scale the image from byte array
    private void updateImage(byte[] imageData) {
        ImageIcon dishIcon = null;
        if (imageData != null && imageData.length > 0) { // Check if image data is not null and not empty
            try {
                // Load image from byte array
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
                if (originalImage != null) {
                    // Scaling to fit the image within the panel
                    Image scaledImg = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Scale image to 100x100
                    dishIcon = new ImageIcon(scaledImg); // Use the scaled image
                } else {
                    System.err.println("Could not read image from byte data.");
                }

            } catch (IOException e) {
                System.err.println("Error loading dish image from byte data: " + e.getMessage());
                // Fallback to text if loading fails
                imageLabel.setText("Image Load Error"); // More specific error message
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dishIcon = null; // Ensure icon is null on failure
            }
        } else {
            // Fallback if image data is null or empty
            imageLabel.setText("No Image");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dishIcon = null;
        }


        imageLabel.setIcon(dishIcon); // Set the icon (or null if loading failed)
    }

    // Getter for the associated Plat object
    public Plat getPlat() {
        return plat;
    }

    // Getters for current dish details (used by the modification dialog) - kept for compatibility
    // These now retrieve data from the stored Plat object
    public String getImagePath() {
        // We don't store the file path in the Plat object when loaded from DB.
        // This getter is less relevant now. Returning an empty string or null.
        return ""; // Indicate no path is available
    }

    public String getDishName() {
        return plat.getNom();
    }

    public String getDishDescription() {
        return plat.getDescription();
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
            Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH); // Resize to 30x30 pixels
            backIcon = new ImageIcon(scaledImg); // Use scaled image

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
            Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH); // Resize
            plusIcon = new ImageIcon(scaledImg); // Use scaled image

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

        // Positioning the "+" icon in the bottom right corner
        mainGbc.gridx = 1; // Adjust column to be potentially on the right
        mainGbc.gridy = 2; // Below the scroll pane
        mainGbc.gridwidth = 1; // Spans only 1 column
        mainGbc.anchor = GridBagConstraints.SOUTHEAST; // Position at bottom-right
        mainGbc.weighty = 0.0; // Don't stretch vertically
        mainGbc.insets = new Insets(10, 0, 10, 10); // Padding
        mainPanel.add(addPlateLabel, mainGbc);


        // Add the main panel to the frame
        add(mainPanel);

        // Load existing plates from the database when the interface starts
        loadAllPlats();
    }

    /**
     * Loads all plates from the database and displays them in the GUI.
     */
    private void loadAllPlats() {
        dishesContainerPanel.removeAll(); // Clear existing dish panels
        PlatDAO platDAO = new PlatDAO();
        List<Plat> allPlats = platDAO.getAllPlats(); // Assuming getAllPlats works with Singleton

        if (allPlats != null && !allPlats.isEmpty()) {
            // Restore GridLayout if items are loaded
            dishesContainerPanel.setLayout(new GridLayout(0, 4, 20, 20));
            for (Plat plat : allPlats) {
                dishesContainerPanel.add(new DishPanel(plat, this)); // Create and add a panel for each plat
            }
        } else {
            // Use GridBagLayout to center the message when no items are found
            dishesContainerPanel.setLayout(new GridBagLayout()); // Set layout for centering
            JLabel noPlatsLabel = new JLabel("Aucun plat disponible dans la base de données.");
            noPlatsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noPlatsLabel.setForeground(COLOR_TEXT_DARK);
            JPanel centeredPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for centering
            centeredPanel.setOpaque(false);
            centeredPanel.add(noPlatsLabel);
            dishesContainerPanel.add(centeredPanel, new GridBagConstraints()); // Add centered panel
        }

        dishesContainerPanel.revalidate(); // Re-layout the panel
        dishesContainerPanel.repaint(); // Repaint the panel
    }

    /**
     * Adds a new DishPanel to the display after a dish is added to the database.
     *
     * @param dishPanel The DishPanel to add.
     */
    public void addDish(DishPanel dishPanel) {
        // If currently showing "no plats" message, remove it and switch layout
        if (dishesContainerPanel.getLayout() instanceof GridBagLayout) {
            dishesContainerPanel.removeAll();
            dishesContainerPanel.setLayout(new GridLayout(0, 4, 20, 20));
        }
        dishesContainerPanel.add(dishPanel);
        dishesContainerPanel.revalidate();
        dishesContainerPanel.repaint();
    }

    /**
     * Removes a DishPanel from the display after a dish is deleted from the database.
     *
     * @param dishPanel The DishPanel to remove.
     */
    public void removeDishPanel(DishPanel dishPanel) {
        dishesContainerPanel.remove(dishPanel);
        // If the panel is now empty, show the "no plats" message
        if (dishesContainerPanel.getComponentCount() == 0) {
            dishesContainerPanel.setLayout(new GridBagLayout()); // Set layout for centering
            JLabel noPlatsLabel = new JLabel("Aucun plat disponible dans la base de données.");
            noPlatsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noPlatsLabel.setForeground(COLOR_TEXT_DARK);
            JPanel centeredPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for centering
            centeredPanel.setOpaque(false);
            centeredPanel.add(noPlatsLabel);
            dishesContainerPanel.add(centeredPanel, new GridBagConstraints()); // Add centered panel
        }
        dishesContainerPanel.revalidate();
        dishesContainerPanel.repaint();
    }


    // Main method for standalone testing (already present)
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

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ListeDePlatsInterface().setVisible(true);
            }
        });
    }
}