package gui; // Declare the package

import model.Plat; // Assuming your Plat model is in the 'model' package
import model.Menu; // Assuming your Menu model is in the 'model' package
import dao.PlatDAO; // For fetching menu items
import dao.MenuDAO; // For fetching menu list
import gui.CartItem; // Assuming you have a CartItem class
import model.Commande; // Import Commande model
import dao.CommandeDAO; // Import CommandeDAO

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter; // For back/cart icon listeners
import java.awt.event.MouseEvent;    // For back/cart icon listeners
import java.awt.image.BufferedImage; // For ImageIO
import java.io.File;                // For File operations
import javax.imageio.ImageIO;       // For ImageIO
import java.io.IOException;         // For IOException
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp; // For dateCommande (java.sql.Timestamp)
import java.util.Date; // Import Date class
import java.sql.SQLException; // Import SQLException for potential handling
import java.lang.ClassNotFoundException; // Import ClassNotFoundException


// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // Already handled by the project structure

// Import the Login class to allow returning to the login page (as per ClientMenuInterface's back button)
import gui.Login;
import model.Utilisateur; // Import Utilisateur model to potentially store logged-in user


import gui.CartItem;

// Dialog for displaying the shopping cart (Now contains cart display logic)
class CartDialog extends JDialog {
    private JList<CartItem> cartList;
    private DefaultListModel<CartItem> cartListModel;
    private JButton removeButton;
    private JButton closeButton;
    private JButton checkoutButton;
    private JLabel totalLabel;
    // Removed: private JTextField tableNumberField; // Moved table number field here

    private ClientMenuInterface parentInterface; // Reference to the main interface

    public CartDialog(JFrame parent, ClientMenuInterface parentInterface) {
        super(parent, "Mon Panier", true); // Modal dialog with title
        this.parentInterface = parentInterface;

        // Set up dialog properties
        setSize(400, 500); // Adjusted size - Increased height to 550
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10)); // Added layout with gaps
        getContentPane().setBackground(new Color(250, 246, 233)); // Match color scheme

        // Cart Title Label
        JLabel cartTitleLabel = new JLabel("Mon Panier");
        cartTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cartTitleLabel.setForeground(new Color(50, 50, 50));
        cartTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0)); // Add some padding

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(cartTitleLabel, BorderLayout.NORTH);
        add(headerPanel, BorderLayout.NORTH);


        // List model and JList for cart items
        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setFont(new Font("Arial", Font.PLAIN, 16));
        cartList.setBackground(new Color(253, 253, 253)); // White background for list
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single selection

        // Wrap the list in a scroll pane
        JScrollPane scrollPane = new JScrollPane(cartList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 220, 200), 1)); // Add a light border
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Total Label
        totalLabel = new JLabel("Total: 0.00 €"); // Initial text
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(50, 50, 50));


        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 25)); // Added gaps
        buttonPanel.setBackground(new Color(250, 246, 233)); // Match color scheme

        // Remove Button
        removeButton = new JButton("Supprimer");
        removeButton.setFont(new Font("Arial", Font.BOLD, 12));
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = cartList.getSelectedIndex();
                if (selectedIndex != -1) { // If an item is selected
                    CartItem selectedItem = cartListModel.getElementAt(selectedIndex);
                    parentInterface.removeItemFromCart(selectedItem); // Remove from the main cart data in parent
                    updateCartDisplay(); // Update the dialog's display after removal
                } else {
                    JOptionPane.showMessageDialog(CartDialog.this, "Veuillez sélectionner un article à supprimer.", "Aucun article sélectionné", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(removeButton);

        // Checkout Button
        checkoutButton = new JButton("Commander");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        checkoutButton.setBackground(new Color(60, 179, 113)); // Medium sea green color
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        checkoutButton.setOpaque(true);
        checkoutButton.setBorderPainted(false);
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Action for checkout
                System.out.println("Checkout button clicked!");
                if (cartListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(CartDialog.this, "Votre panier est vide. Veuillez ajouter des articles avant de commander.", "Panier vide", JOptionPane.INFORMATION_MESSAGE);
                    return; // Exit if cart is empty
                }

                // --- DATABASE INTEGRATION: Place Order ---
                CommandeDAO commandeDAO = new CommandeDAO();
                // Use the stored client ID from the parent interface
                int clientId = parentInterface.getLoggedInClientId(); // Get the client ID from the parent frame

                if (clientId != -1) { // Ensure a client is "logged in"
                    // This method call passes the clientId to the DAO
                    boolean orderPlaced = commandeDAO.placeOrder(clientId, parentInterface.getCartItems());

                    if (orderPlaced) {
                        JOptionPane.showMessageDialog(CartDialog.this, "Commande passée avec succès!", "Commande Réussie", JOptionPane.INFORMATION_MESSAGE);
                        parentInterface.clearCart(); // Clear the cart in the parent interface
                        updateCartDisplay(); // Update the dialog's display after clearing
                        dispose(); // Close the dialog
                    } else {
                        JOptionPane.showMessageDialog(CartDialog.this, "Échec de la commande. Veuillez réessayer.", "Erreur de Commande", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(CartDialog.this, "Aucun client connecté. Veuillez vous connecter pour passer une commande.", "Erreur de connexion", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(checkoutButton);


        // Close Button
        closeButton = new JButton("Fermer");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setBackground(Color.GRAY);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        closeButton.setOpaque(true);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });
        buttonPanel.add(closeButton);

        // Use FlowLayout for the footer panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Align right, with gaps
        footerPanel.setOpaque(false);
        footerPanel.add(totalLabel); // Add total label
        footerPanel.add(buttonPanel); // Add button panel
        add(footerPanel, BorderLayout.SOUTH);


        // Populate the list with current cart items and update total when the dialog is shown
        updateCartDisplay();
    }

    // Method to update the list display from the parent interface's cart data
    public void updateCartDisplay() {
        cartListModel.clear(); // Clear the current list model
        double total = 0;
        for (CartItem item : parentInterface.getCartItems()) { // Get items from parent
            total += item.getPrice() * item.getQuantity(); // Calculate total (Assuming CartItem has getPrice)
            cartListModel.addElement(item); // Add items from the parent's cart
        }
        totalLabel.setText(String.format("Total: %.2f €", total)); // Update total label
    }
}


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
    private JComboBox<Menu> menuComboBox; // Combo box to select menus
    private List<CartItem> shoppingCart; // This list holds the items in the cart
    private int loggedInClientId = -1; // Field to store the logged-in client's ID


    /**
     * Constructs the ClientMenuInterface.
     *
     * @param clientId The ID of the currently logged-in client.
     * If login is handled elsewhere, this might be passed from there.
     * If no login is needed, you could potentially remove this parameter.
     */
    public ClientMenuInterface(int clientId) {
        this.loggedInClientId = clientId; // Store the client ID

        // Initialize cart items list
        shoppingCart = new ArrayList<>();

        // Set up the main window properties
        setTitle("Menu du Restaurant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        // Create the main panel using the custom BackgroundPanel
        // Make sure "background.jpg" exists in the correct location or provide a full path
        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg"); // Assuming BackgroundPanel exists
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(10, 10, 10, 10);


        // Back Icon Label (top left)
        JLabel backIconLabel = new JLabel();
        backIconLabel.setOpaque(false);

        ImageIcon backIcon = null;
        try {
            // Make sure "arrow.png" exists in the correct location or provide a full path
            Image img = ImageIO.read(new File("arrow.png"));
             // Scale the image
            backIcon = new ImageIcon(img);

        } catch (Exception e) {
            System.err.println("Error loading back arrow icon: " + e.getMessage());
            backIconLabel.setText("Back");
            backIconLabel.setForeground(COLOR_TEXT_DARK);
        }

        if (backIcon != null) {
            backIconLabel.setIcon(backIcon);
        }

        backIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        backIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ClientMenuInterface.this.dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        // Assuming you return to the Login screen.
                        // Change this if you have a different previous screen.
                        new Login().setVisible(true); // Assuming Login class exists
                    }
                });
            }
        });
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.anchor = GridBagConstraints.NORTHWEST;
        mainGbc.insets = new Insets(10, 10, 0, 0);
        mainPanel.add(backIconLabel, mainGbc);


        // Shopping Cart Icon (top right)
        JLabel cartIconLabel = new JLabel();
        cartIconLabel.setOpaque(false);
        ImageIcon cartIcon = null;
        try {
            // Make sure "shopping-cart.png" exists in the correct location or provide a full path
            Image img = ImageIO.read(new File("shopping-cart.png"));

            cartIcon = new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Error loading shopping cart icon: " + e.getMessage());
            cartIconLabel.setText("Cart");
            cartIconLabel.setForeground(COLOR_TEXT_DARK);
        }

        if (cartIcon != null) {
            cartIconLabel.setIcon(cartIcon);
        }

        cartIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        cartIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cartIconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Show the CartDialog when the icon is clicked
                CartDialog cartDialog = new CartDialog(ClientMenuInterface.this, ClientMenuInterface.this);
                cartDialog.setVisible(true);
            }
        });
        mainGbc.gridx = 2; // Positioned to the right
        mainGbc.gridy = 0;
        mainGbc.anchor = GridBagConstraints.NORTHEAST;
        mainGbc.insets = new Insets(10, 0, 0, 10);
        mainGbc.weightx = 0.0; // Do not take extra horizontal space here
        mainGbc.fill = GridBagConstraints.NONE; // Do not fill
        mainPanel.add(cartIconLabel, mainGbc);


        // Title Label (Centered)
        JLabel titleLabel = new JLabel("Notre Menu Délicieux");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 1; // Center column
        mainGbc.gridy = 0;
        mainGbc.anchor = GridBagConstraints.NORTH; // Anchor to the top within the cell
        mainGbc.insets = new Insets(20, 10, 20, 10);
        mainGbc.weightx = 1.0; // *** Give this column weight to help centering ***
        mainGbc.fill = GridBagConstraints.NONE; // Do not fill the cell
        mainPanel.add(titleLabel, mainGbc);

        // Menu Selection ComboBox
        menuComboBox = new JComboBox<>();
        menuComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        menuComboBox.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        menuComboBox.setPreferredSize(new Dimension(200, 40));


        // Set a custom renderer to display Menu objects by name
        menuComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Menu) {
                    // Use getNomMenu() as per your Menu class
                    setText(((Menu) value).getNomMenu());
                }
                return this;
            }
        });

        // Add action listener to the combo box
        menuComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu selectedMenu = (Menu) menuComboBox.getSelectedItem();
                // Use getIdMenu() as per your Menu class
                if (selectedMenu != null && selectedMenu.getIdMenu() != -1) { // Check if a valid menu is selected (ID -1 used for placeholder if applicable)
                    loadMenuItems(selectedMenu.getIdMenu()); // Load plates for the selected menu
                } else {
                    // Handle the case where nothing is selected or a placeholder item is used
                    // Clear displayed items and show a message
                    menuItemsPanel.removeAll();
                    JLabel selectMenuLabel = new JLabel("Veuillez sélectionner un menu.");
                    selectMenuLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    selectMenuLabel.setForeground(COLOR_TEXT_DARK);
                    JPanel centeredPanel = new JPanel(new GridBagLayout());
                    centeredPanel.setOpaque(false);
                    centeredPanel.add(selectMenuLabel);
                    menuItemsPanel.setLayout(new GridBagLayout()); // Set layout for centering
                    menuItemsPanel.add(centeredPanel, new GridBagConstraints());
                    menuItemsPanel.revalidate();
                    menuItemsPanel.repaint();
                }
            }
        });


        mainGbc.gridx = 1; // Center column, below title
        mainGbc.gridy = 1;
        mainGbc.anchor = GridBagConstraints.CENTER; // Center the combo box horizontally and vertically within its cell
        mainGbc.insets = new Insets(0, 10, 10, 10); // Padding below combo box
        mainGbc.weightx = 0.0; // Combo box takes minimal horizontal space
        mainGbc.fill = GridBagConstraints.NONE; // Don't stretch the combo box
        mainPanel.add(menuComboBox, mainGbc);


        // Menu Items Panel (Main content area)
        menuItemsPanel = new JPanel();
        menuItemsPanel.setBackground(new Color(250, 246, 233, 150)); // Semi-transparent background
        // Initial layout might be GridBagLayout to center a "Select Menu" message
        menuItemsPanel.setLayout(new GridBagLayout());
        JScrollPane menuScrollPane = new JScrollPane(menuItemsPanel);
        menuScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder());
        menuScrollPane.setOpaque(false); // Fixed typo here in previous versions
        menuScrollPane.getViewport().setOpaque(false); // Fixed typo here in previous versions


        mainGbc.gridx = 0;
        mainGbc.gridy = 2; // Below the combo box
        mainGbc.gridwidth = 3; // Span across all columns
        mainGbc.weightx = 1.0; // Take all horizontal space
        mainGbc.weighty = 1.0; // Take all vertical space
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(10, 50, 10, 50); // Padding around the menu items
        mainPanel.add(menuScrollPane, mainGbc);

        add(mainPanel);

        // Populate the menu combo box when the interface is created
        populateMenuComboBox();

        // The action listener of the combo box will handle the initial load
        // after populateMenuComboBox selects the first item (if any).
    }

    // Default constructor (used for testing or if client ID is set later)
    // Calls the main constructor with a default invalid client ID
    public ClientMenuInterface() {
        this(-1);
    }


    /**
     * Fetches all menus from the database and populates the menu combo box.
     */
    private void populateMenuComboBox() {
        MenuDAO menuDAO = new MenuDAO(); // Assuming MenuDAO exists
        List<Menu> menus = menuDAO.getMenuList(); // Using your getMenuList() method

        menuComboBox.removeAllItems(); // Clear existing items

        if (menus != null && !menus.isEmpty()) {
            // Add actual menu objects to the combo box
            for (Menu menu : menus) {
                menuComboBox.addItem(menu);
            }
            // Select the first item to trigger the initial load via listener
            menuComboBox.setSelectedIndex(0);

        } else {
            // Handle case where no menus are found
            menuComboBox.setEnabled(false); // Disable combo box
            // Display a message in the menu items panel
            menuItemsPanel.removeAll();
            JLabel noMenusLabel = new JLabel("Aucun menu n'est disponible pour le moment.");
            noMenusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noMenusLabel.setForeground(COLOR_TEXT_DARK);
            JPanel centeredPanel = new JPanel(new GridBagLayout());
            centeredPanel.setOpaque(false);
            centeredPanel.add(noMenusLabel);
            menuItemsPanel.setLayout(new GridBagLayout()); // Set layout for centering
            menuItemsPanel.add(centeredPanel, new GridBagConstraints());

            menuItemsPanel.revalidate();
            menuItemsPanel.repaint();
        }
    }


    /**
     * Loads menu items from the database filtered by the given menu ID.
     * Updates the menuItemsPanel display.
     *
     * @param menuId The ID of the menu to display plates for.
     */
    private void loadMenuItems(int menuId) {
        menuItemsPanel.removeAll(); // Clear existing items
        // Restore GridLayout if items are expected
        menuItemsPanel.setLayout(new GridLayout(0, 4, 20, 20));

        PlatDAO platDAO = new PlatDAO(); // Assuming PlatDAO exists
        List<Plat> plats = platDAO.getPlatsByMenuId(menuId); // Assuming getPlatsByMenuId exists

        if (plats != null && !plats.isEmpty()) {
            for (Plat plat : plats) {
                menuItemsPanel.add(new MenuItemPanel(plat)); // Add each plat to the menu display (Assuming MenuItemPanel exists)
            }
        } else {
            // Use GridBagLayout to center the message when no items are found
            menuItemsPanel.setLayout(new GridBagLayout()); // Set layout for centering
            JLabel noItemsLabel = new JLabel("Aucun plat disponible pour ce menu.");
            noItemsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noItemsLabel.setForeground(COLOR_TEXT_DARK);
            JPanel centeredPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for centering
            centeredPanel.setOpaque(false);
            centeredPanel.add(noItemsLabel);
            menuItemsPanel.add(centeredPanel, new GridBagConstraints()); // Add centered panel
        }

        menuItemsPanel.revalidate();
        menuItemsPanel.repaint();
    }


    // Inner class MenuItemPanel - Represents a single menu item display
    class MenuItemPanel extends JPanel {
        private JLabel imageLabel;
        private JLabel nameLabel;
        private JLabel descriptionLabel; // Added description label
        private JLabel priceLabel;
        private JSpinner quantitySpinner; // Added quantity spinner
        private JButton addButton;

        public MenuItemPanel(Plat plat) {
            // Use BoxLayout for vertical arrangement
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(250, 246, 233)); // Use a color similar to the panel background
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 220, 200), 1), // Light border
                    BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
            ));
            setPreferredSize(new Dimension(180, 280)); // Adjusted preferred size
            setMaximumSize(new Dimension(180, 280)); // Adjusted maximum size
            setMinimumSize(new Dimension(180, 280)); // Adjusted minimum size


            // Image Label
            imageLabel = new JLabel();
            ImageIcon dishIcon = null;
            if (plat.getImage() != null && plat.getImage().length > 0) {
                try {
                    BufferedImage originalImage = ImageIO.read(new java.io.ByteArrayInputStream(plat.getImage()));
                    if (originalImage != null) {
                        // Scaling to fit the image within the panel
                        Image scaledImg = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Scale image to 100x100
                        dishIcon = new ImageIcon(scaledImg); // Use the scaled image
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
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally


            // Name Label
            nameLabel = new JLabel(plat.getNom());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setForeground(COLOR_TEXT_DARK);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

            // Description Label - Use HTML for potential word wrapping and centering
            descriptionLabel = new JLabel("<html><body style='text-align:center;'>" + plat.getDescription() + "</body></html>");
            descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            descriptionLabel.setForeground(new Color(80, 80, 80));
            descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
            descriptionLabel.setVerticalAlignment(SwingConstants.TOP); // Align text to top if multiple lines
            descriptionLabel.setPreferredSize(new Dimension(160, 40)); // Give it a preferred size for description
            descriptionLabel.setMaximumSize(new Dimension(160, 60));


            // Price Label
            priceLabel = new JLabel(String.format("%.2f €", plat.getPrix())); // Format price with €
            priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
            priceLabel.setForeground(new Color(0, 100, 0)); // Green color for price
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally

            // Quantity Spinner
            SpinnerModel quantityModel = new SpinnerNumberModel(1, 1, 10, 1); // Default=1, Min=1, Max=10, Step=1
            quantitySpinner = new JSpinner(quantityModel);
            quantitySpinner.setFont(new Font("Arial", Font.PLAIN, 14));
            quantitySpinner.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally
            // Set preferred size to make it a bit smaller
            quantitySpinner.setPreferredSize(new Dimension(60, 25));
            quantitySpinner.setMaximumSize(new Dimension(60, 25));


            // Add to Cart Button
            addButton = new JButton("Ajouter"); // Button text
            addButton.setFont(new Font("Arial", Font.BOLD, 12));
            addButton.setBackground(new Color(60, 179, 113)); // Medium sea green color
            addButton.setForeground(Color.WHITE); // White text
            addButton.setFocusPainted(false);
            addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            addButton.setOpaque(true);
            addButton.setBorderPainted(false);
            addButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally


            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Get the selected quantity from the spinner
                    int quantity = (int) quantitySpinner.getValue();
                    if (quantity > 0) {
                        addMenuItemToCart(plat, quantity);
                        // Optionally, show a confirmation message here
                        JOptionPane.showMessageDialog(ClientMenuInterface.this,
                                quantity + " x " + plat.getNom() + " ajouté au panier.",
                                "Article ajouté", JOptionPane.INFORMATION_MESSAGE);
                        // Reset spinner after adding
                        quantitySpinner.setValue(1);
                    } else {
                        JOptionPane.showMessageDialog(ClientMenuInterface.this,
                                "La quantité doit être un nombre positif.",
                                "Quantité invalide", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            // Add components to the MenuItemPanel in vertical order
            add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
            add(imageLabel);
            add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
            add(nameLabel);
            add(Box.createRigidArea(new Dimension(0, 3))); // Spacer
            add(descriptionLabel); // Add description label
            add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
            add(priceLabel); // Add price label
            add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
            add(quantitySpinner); // Add quantity spinner
            add(Box.createVerticalGlue()); // Push components upwards
            add(addButton); // Add add to cart button
            add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        }
    }


    // Method to add an item to the cart or update its quantity
    public void addMenuItemToCart(Plat plat, int quantity) {
        boolean found = false;
        for (CartItem item : shoppingCart) {
            if (plat.getIdPlat() == item.getPlat().getIdPlat()) { // Assuming Plat has an ID and CartItem stores Plat
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            shoppingCart.add(new CartItem(plat, quantity)); // Assuming CartItem constructor takes Plat and quantity
        }
        // Cart display and total price are updated when the dialog is opened
    }

    // Method to remove an item from the cart
    public void removeItemFromCart(CartItem itemToRemove) {
        shoppingCart.remove(itemToRemove);
        // Cart display and total price are updated by the dialog after removal
    }

    // Method to get the current cart items (for the dialog)
    public List<CartItem> getCartItems() {
        return shoppingCart;
    }

    // Method to clear the cart (e.g., after checkout)
    public void clearCart() {
        shoppingCart.clear();
        System.out.println("Cart cleared."); // For debugging
    }

    // Getter for the logged-in client ID
    public int getLoggedInClientId() {
        return loggedInClientId;
    }


    // Main method for standalone testing
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
                // For testing purposes:
                // Assuming a client with ID 1 is logged in.
                // The menu selection will happen within the GUI.
                int testClientId = 1; // Replace with actual logged-in client ID

                new ClientMenuInterface(testClientId).setVisible(true); // Pass only client ID
            }
        });
    }
}