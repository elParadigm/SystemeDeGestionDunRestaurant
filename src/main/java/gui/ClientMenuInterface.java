package gui; // Declare the package

import model.Plat;
import dao.PlatDAO; // For fetching menu items
import gui.CartItem; // Assuming you have a CartItem class
import model.Commande; // Import Commande model
import dao.CommandeDAO; // Import CommandeDAO

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // NEW: For back/cart icon listeners
import java.awt.event.MouseEvent;    // NEW: For back/cart icon listeners
import java.awt.image.BufferedImage; // NEW: For ImageIO
import java.io.File;                // NEW: For File operations
import javax.imageio.ImageIO;       // NEW: For ImageIO
import java.io.IOException;         // NEW: For IOException
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp; // NEW: For dateCommande (java.sql.Timestamp)

// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // Already handled by the project structure

// Import the Login class to allow returning to the login page (as per ClientMenuInterface's back button)
import gui.Login;

public class ClientMenuInterface extends JFrame { // Class name

    // Define the colors used in the GUI
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    private JPanel menuItemsPanel; // Panel to hold the grid of menu items
    private List<CartItem> cartItems; // This list holds the items in the cart
    private JPanel cartItemsPanel; // Panel to display cart items
    private JLabel totalPriceLabel; // Label to display total price

    private JTextField tableNumberField; // NEW: Added for table number input

    // Constructor
    public ClientMenuInterface() {
        // Initialize cart items list
        cartItems = new ArrayList<>();

        // Set up the main window properties
        setTitle("Menu du Restaurant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        // Create the main panel using the custom BackgroundPanel
        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg"); // Ensure this path is correct
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(10, 10, 10, 10);
        mainGbc.anchor = GridBagConstraints.CENTER;

        // Back Icon Label (top left)
        JLabel backIconLabel = new JLabel();
        backIconLabel.setOpaque(false);

        ImageIcon backIcon = null;
        try {
            Image img = ImageIO.read(new File("image_281e3d.png")); // Using the name you provided for arrow.png
            Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            backIcon = new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Error loading back arrow icon: " + e.getMessage());
        }

        if (backIcon != null) {
            backIconLabel.setIcon(backIcon);
            backIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            backIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            backIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ClientMenuInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            // Assuming you want to return to the Login screen for now,
                            // or a different interface depending on your flow.
                            new Login().setVisible(true);
                        }
                    });
                }
            });
            mainGbc.gridx = 0;
            mainGbc.gridy = 0;
            mainGbc.anchor = GridBagConstraints.NORTHWEST;
            mainGbc.insets = new Insets(10, 10, 0, 0);
            mainPanel.add(backIconLabel, mainGbc);
        }

        // Shopping Cart Icon (top right)
        JLabel cartIconLabel = new JLabel();
        cartIconLabel.setOpaque(false);
        ImageIcon cartIcon = null;
        try {
            Image img = ImageIO.read(new File("add-cart.png")); // Using the name you provided for shopping-cart.png
            Image scaledImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH); // Resize to 40x40 pixels
            cartIcon = new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Error loading shopping cart icon: " + e.getMessage());
        }

        if (cartIcon != null) {
            cartIconLabel.setIcon(cartIcon);
            cartIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cartIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            cartIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Scroll to the cart panel
                    JScrollPane scrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, cartItemsPanel);
                    if (scrollPane != null) {
                        // Scroll to the top of the cart display within the scroll pane
                        scrollPane.getVerticalScrollBar().setValue(0);
                    }
                }
            });
            mainGbc.gridx = 2; // Adjusted gridx to the right
            mainGbc.gridy = 0;
            mainGbc.anchor = GridBagConstraints.NORTHEAST;
            mainGbc.insets = new Insets(10, 0, 0, 10);
            mainPanel.add(cartIconLabel, mainGbc);
        }


        // Title Label
        JLabel titleLabel = new JLabel("Notre Menu Délicieux");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.gridwidth = 3; // Span across all columns
        mainGbc.anchor = GridBagConstraints.NORTH;
        mainGbc.insets = new Insets(20, 10, 20, 10);
        mainPanel.add(titleLabel, mainGbc);


        // Menu Items Panel (Left side)
        menuItemsPanel = new JPanel();
        menuItemsPanel.setBackground(new Color(250, 246, 233, 150)); // Semi-transparent background
        menuItemsPanel.setLayout(new GridLayout(0, 3, 20, 20)); // Rows=0 (automatic), Cols=3, Hgap=20, Vgap=20
        JScrollPane menuScrollPane = new JScrollPane(menuItemsPanel);
        menuScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder());
        menuScrollPane.setOpaque(false);
        menuScrollPane.getViewport().setOpaque(false);

        mainGbc.gridx = 0;
        mainGbc.gridy = 1;
        mainGbc.gridwidth = 2; // Span 2 columns for menu
        mainGbc.weightx = 0.7; // Take 70% of horizontal space
        mainGbc.weighty = 1.0; // Take all vertical space
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(10, 20, 10, 10); // Padding
        mainPanel.add(menuScrollPane, mainGbc);

        // Cart Panel (Right side)
        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBackground(COLOR_PANEL_BACKGROUND);
        cartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel cartTitleLabel = new JLabel("Mon Panier");
        cartTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cartTitleLabel.setForeground(COLOR_TEXT_DARK);
        cartTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // NEW: Panel for table number input
        JPanel tableNumberPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        tableNumberPanel.setOpaque(false);
        JLabel tableLabel = new JLabel("Numéro de Table:");
        tableLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tableLabel.setForeground(COLOR_TEXT_DARK);
        tableNumberField = new JTextField(5); // Field for table number
        tableNumberField.setFont(new Font("Arial", Font.PLAIN, 14));
        tableNumberField.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        tableNumberPanel.add(tableLabel);
        tableNumberPanel.add(tableNumberField);


        cartItemsPanel = new JPanel();
        cartItemsPanel.setLayout(new BoxLayout(cartItemsPanel, BoxLayout.Y_AXIS));
        cartItemsPanel.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        JScrollPane cartScrollPane = new JScrollPane(cartItemsPanel);
        cartScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        cartScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        cartScrollPane.setBorder(BorderFactory.createEmptyBorder());

        totalPriceLabel = new JLabel("Total: 0.00 DT");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        totalPriceLabel.setForeground(COLOR_TEXT_DARK);
        totalPriceLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton checkoutButton = new JButton("Commander"); // This is the "Ajouter Commande" button
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 18));
        checkoutButton.setBackground(COLOR_BUTTON_BACKGROUND);
        checkoutButton.setForeground(COLOR_BUTTON_TEXT);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        checkoutButton.setOpaque(true);
        checkoutButton.setBorderPainted(false);

        // MODIFIED: Add ActionListener to the "Commander" button
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder(); // Call the new method to handle order placement
            }
        });

        JPanel checkoutPanel = new JPanel(new BorderLayout());
        checkoutPanel.setOpaque(false);
        checkoutPanel.add(totalPriceLabel, BorderLayout.WEST);
        checkoutPanel.add(checkoutButton, BorderLayout.EAST);

        // MODIFIED: Layout for cartPanel to include table number
        JPanel cartHeaderPanel = new JPanel(new BorderLayout());
        cartHeaderPanel.setOpaque(false);
        cartHeaderPanel.add(cartTitleLabel, BorderLayout.NORTH);
        cartHeaderPanel.add(tableNumberPanel, BorderLayout.SOUTH); // Add table number input below title

        cartPanel.add(cartHeaderPanel, BorderLayout.NORTH); // Add the header panel to the cart
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        cartPanel.add(checkoutPanel, BorderLayout.SOUTH);

        mainGbc.gridx = 2;
        mainGbc.gridy = 1;
        mainGbc.gridwidth = 1; // Span 1 column for cart
        mainGbc.weightx = 0.3; // Take 30% of horizontal space
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(10, 10, 10, 20);
        mainPanel.add(cartPanel, mainGbc);

        add(mainPanel);

        // Load menu items from the database on startup
        loadMenuItems();
        updateCartDisplay(); // Initialize cart display
        updateTotalPrice(); // Initialize total price
    }

    // Inner class MenuItemPanel (already in your file)
    class MenuItemPanel extends JPanel {
        private JLabel imageLabel;
        private JLabel nameLabel;
        private JLabel priceLabel;
        private JButton addButton;

        public MenuItemPanel(Plat plat) {
            setLayout(new BorderLayout(5, 5));
            setBackground(new Color(250, 246, 233));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 220, 200), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            setPreferredSize(new Dimension(180, 220)); // Slightly larger for better content display

            // Image Label
            imageLabel = new JLabel();
            ImageIcon dishIcon = null;
            if (plat.getImage() != null && plat.getImage().length > 0) {
                try {
                    BufferedImage originalImage = ImageIO.read(new java.io.ByteArrayInputStream(plat.getImage()));
                    if (originalImage != null) {
                        Image scaledImg = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                        dishIcon = new ImageIcon(scaledImg);
                    } else {
                        System.err.println("Could not read image from byte data for plat: " + plat.getNom());
                    }
                } catch (IOException e) {
                    System.err.println("Error loading dish image from byte data for plat " + plat.getNom() + ": " + e.getMessage());
                }
            }
            if (dishIcon != null) {
                imageLabel.setIcon(dishIcon);
            } else {
                imageLabel.setText("No Image");
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            }
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Name Label
            nameLabel = new JLabel(plat.getNom());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            nameLabel.setForeground(COLOR_TEXT_DARK);
            nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Price Label
            priceLabel = new JLabel(String.format("%.2f DT", plat.getPrix()));
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            priceLabel.setForeground(new Color(80, 80, 80));
            priceLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Add to Cart Button
            addButton = new JButton("Ajouter au panier");
            addButton.setFont(new Font("Arial", Font.BOLD, 12));
            addButton.setBackground(COLOR_BUTTON_BACKGROUND);
            addButton.setForeground(COLOR_BUTTON_TEXT);
            addButton.setFocusPainted(false);
            addButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
            addButton.setOpaque(true);
            addButton.setBorderPainted(false);

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Ask for quantity
                    String quantityStr = JOptionPane.showInputDialog(ClientMenuInterface.this,
                            "Entrez la quantité pour " + plat.getNom() + ":",
                            "Quantité", JOptionPane.QUESTION_MESSAGE);

                    if (quantityStr != null && !quantityStr.trim().isEmpty()) {
                        try {
                            int quantity = Integer.parseInt(quantityStr.trim());
                            if (quantity > 0) {
                                addMenuItemToCart(plat, quantity);
                            } else {
                                JOptionPane.showMessageDialog(ClientMenuInterface.this,
                                        "La quantité doit être un nombre positif.",
                                        "Quantité invalide", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(ClientMenuInterface.this,
                                    "Veuillez entrer un nombre valide pour la quantité.",
                                    "Erreur de format", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            JPanel textPanel = new JPanel(new GridLayout(2, 1));
            textPanel.setOpaque(false);
            textPanel.add(nameLabel);
            textPanel.add(priceLabel);

            add(imageLabel, BorderLayout.NORTH);
            add(textPanel, BorderLayout.CENTER);
            add(addButton, BorderLayout.SOUTH);
        }
    }

    // This method loads menu items from the database
    private void loadMenuItems() {
        menuItemsPanel.removeAll(); // Clear existing items
        PlatDAO platDAO = new PlatDAO();
        List<Plat> plats = platDAO.getAllPlats(); // Assuming PlatDAO has getAllPlats

        if (plats != null && !plats.isEmpty()) {
            for (Plat plat : plats) {
                menuItemsPanel.add(new MenuItemPanel(plat)); // Add each plat to the menu display
            }
        } else {
            JLabel noItemsLabel = new JLabel("Aucun plat disponible pour le moment.");
            noItemsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noItemsLabel.setForeground(COLOR_TEXT_DARK);
            menuItemsPanel.add(noItemsLabel);
        }

        menuItemsPanel.revalidate();
        menuItemsPanel.repaint();
    }


    // Method to add an item to the cart or update its quantity
    public void addMenuItemToCart(Plat plat, int quantity) {
        boolean found = false;
        for (CartItem item : cartItems) {
            if (plat.getIdPlat() == item.getPlat().getIdPlat()) { // Assuming Plat has an ID
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            cartItems.add(new CartItem(plat, quantity));
        }
        updateCartDisplay();
        updateTotalPrice();
    }

    // Method to remove an item from the cart
    public void removeItemFromCart(CartItem itemToRemove) {
        cartItems.remove(itemToRemove);
        updateCartDisplay();
        updateTotalPrice();
    }

    // Method to update the display of cart items
    private void updateCartDisplay() {
        cartItemsPanel.removeAll(); // Clear current display
        if (cartItems.isEmpty()) {
            JLabel emptyCartLabel = new JLabel("Votre panier est vide.");
            emptyCartLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyCartLabel.setForeground(new Color(150, 150, 150));
            cartItemsPanel.add(emptyCartLabel);
        } else {
            for (CartItem item : cartItems) {
                JPanel itemPanel = new JPanel(new BorderLayout(5, 5));
                itemPanel.setOpaque(false); // Make it transparent
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                JLabel itemLabel = new JLabel(item.getPlat().getNom() + " x " + item.getQuantity());
                itemLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                itemLabel.setForeground(COLOR_TEXT_DARK);

                JButton removeButton = new JButton("X"); // Remove button
                removeButton.setFont(new Font("Arial", Font.BOLD, 10));
                removeButton.setBackground(Color.RED);
                removeButton.setForeground(Color.WHITE);
                removeButton.setFocusPainted(false);
                removeButton.setBorder(BorderFactory.createEmptyBorder(3, 6, 3, 6));
                removeButton.setOpaque(true);
                removeButton.setBorderPainted(false);

                removeButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removeItemFromCart(item);
                    }
                });

                itemPanel.add(itemLabel, BorderLayout.CENTER);
                itemPanel.add(removeButton, BorderLayout.EAST);
                cartItemsPanel.add(itemPanel);
            }
        }
        cartItemsPanel.revalidate();
        cartItemsPanel.repaint();
    }

    // Method to update the total price display
    private void updateTotalPrice() {
        double total = 0.0;
        for (CartItem item : cartItems) {
            total += item.getPlat().getPrix() * item.getQuantity();
        }
        totalPriceLabel.setText(String.format("Total: %.2f DT", total));
    }


    // NEW METHOD: placeOrder() - Handles inserting the order into the database
    private void placeOrder() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Votre panier est vide. Veuillez ajouter des articles avant de commander.", "Panier Vide", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String tableNumberText = tableNumberField.getText().trim();
        if (tableNumberText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer le numéro de table.", "Numéro de Table Manquant", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Optional: Validate table number (e.g., must be an integer)
        int tableNumber;
        try {
            tableNumber = Integer.parseInt(tableNumberText);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Le numéro de table doit être un nombre valide.", "Erreur de format", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Calculate total price (although CommandeDAO calculates this too,
        // it's good to have it here for potential display/validation before sending)
        double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPlat().getPrix() * item.getQuantity();
        }

        // --- IMPORTANT ---
        // Currently, idUtilisateur is hardcoded to 1 as there's no active user session management
        // in this ClientMenuInterface. In a complete system, this ID should come from the
        // logged-in user's session (e.g., passed from the Login screen or retrieved from a global session object).
        int loggedInUserId = 1; // Placeholder User ID (e.g., a guest user or first registered user)

        // Use CommandeDAO to add the order to the database
        CommandeDAO commandeDAO = new CommandeDAO();
        try {
            // MODIFIED: Call the existing placeOrder method in CommandeDAO
            // Pass the user ID and the list of cart items
            boolean success = commandeDAO.placeOrder(loggedInUserId, cartItems);

            if (success) {
                JOptionPane.showMessageDialog(this, "Commande passée avec succès!", "Succès de la commande", JOptionPane.INFORMATION_MESSAGE);
                // Clear the cart after successful order
                cartItems.clear();
                updateCartDisplay(); // Update the GUI to show an empty cart
                updateTotalPrice(); // Reset total price label
                tableNumberField.setText(""); // Clear table number field
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la commande. Veuillez réessayer.", "Erreur de commande", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Une erreur s'est produite lors du traitement de la commande: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }



    // Main method for standalone testing (already in the file)
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
                new ClientMenuInterface().setVisible(true);
            }
        });
    }
}