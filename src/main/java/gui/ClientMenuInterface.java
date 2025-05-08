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

// Import the Login class to allow returning
import gui.Login;

// Define a simple class to represent an item in the cart
class CartItem {
    private String name;
    private String price; // Storing price as String for simplicity as in DishPanel
    private int quantity;

    public CartItem(String name, String price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Override toString for easy display in a list
    @Override
    public String toString() {
        return quantity + " x " + name + " (" + price + ")";
    }
}

// Custom JPanel for displaying a single dish item for the client menu
class ClientDishPanel extends JPanel {
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel descriptionLabel;
    private JLabel priceLabel; // Label for price
    private JSpinner quantitySpinner; // Spinner for quantity selection
    private JButton addToCartButton; // Button to add to cart

    // Reference to the parent ClientMenuInterface to add items to the cart
    private ClientMenuInterface parentInterface;

    public ClientDishPanel(String imageName, String name, String description, String price, ClientMenuInterface parentInterface) { // Added parentInterface parameter
        this.parentInterface = parentInterface;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Arrange components vertically
        setBackground(new Color(250, 246, 233)); // Use a color similar to the panel background
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 200), 1), // Light border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
        ));
        setPreferredSize(new Dimension(150, 250)); // Adjusted preferred size to fit spinner and button
        setMaximumSize(new Dimension(150, 250)); // Adjusted maximum size
        setMinimumSize(new Dimension(150, 250)); // Adjusted minimum size


        // Image Label
        imageLabel = new JLabel();
        // Attempt to load the image from a file and scale it
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

        // Price Label
        priceLabel = new JLabel(price); // Set the price text
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
        addToCartButton = new JButton("Ajouter"); // Button text
        addToCartButton.setFont(new Font("Arial", Font.BOLD, 12));
        addToCartButton.setBackground(new Color(60, 179, 113)); // Medium sea green color
        addToCartButton.setForeground(Color.WHITE); // White text
        addToCartButton.setFocusPainted(false);
        addToCartButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addToCartButton.setOpaque(true);
        addToCartButton.setBorderPainted(false);
        addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Center horizontally


        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the selected quantity from the spinner
                int quantity = (int) quantitySpinner.getValue();
                // Create a CartItem and add it to the cart in the parent interface
                CartItem item = new CartItem(nameLabel.getText(), priceLabel.getText(), quantity);
                if (parentInterface != null) {
                    parentInterface.addItemToCart(item);
                    // Optionally, reset the spinner to 1 after adding
                    quantitySpinner.setValue(1);
                }
            }
        });

        // Add components to the ClientDishPanel
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        add(imageLabel);
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        add(nameLabel);
        add(Box.createRigidArea(new Dimension(0, 3))); // Spacer
        add(descriptionLabel);
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        add(priceLabel); // Add price label
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
        add(quantitySpinner); // Add quantity spinner
        add(Box.createVerticalGlue()); // Push components to the top
        add(addToCartButton); // Add add to cart button
        add(Box.createRigidArea(new Dimension(0, 5))); // Spacer
    }
}

// Dialog for displaying the shopping cart
class CartDialog extends JDialog {
    private JList<CartItem> cartList;
    private DefaultListModel<CartItem> cartListModel;
    private JButton removeButton;
    private JButton closeButton;
    private JButton checkoutButton; // Added checkout button

    // Reference to the main ClientMenuInterface to update the cart data
    private ClientMenuInterface parentInterface;

    public CartDialog(JFrame parent, ClientMenuInterface parentInterface) {
        super(parent, "Shopping Cart", true); // Modal dialog
        this.parentInterface = parentInterface;

        // Set up dialog properties
        setSize(400, 400);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 233)); // Match color scheme

        // List model to hold cart items
        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setFont(new Font("Arial", Font.PLAIN, 16));
        cartList.setBackground(new Color(250, 246, 233)); // Match color scheme
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single selection

        // Wrap the list in a scroll pane
        JScrollPane scrollPane = new JScrollPane(cartList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove border
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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
                    parentInterface.removeItemFromCart(selectedItem); // Remove from the main cart data
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
                // In a real application, this would initiate the checkout process
                if (cartListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(CartDialog.this, "Votre panier est vide.", "Panier vide", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Process the order...
                    JOptionPane.showMessageDialog(CartDialog.this, "Commande passée!", "Commande", JOptionPane.INFORMATION_MESSAGE);
                    parentInterface.clearCart(); // Clear the cart after checkout
                    updateCartDisplay(); // Update the dialog's display after clearing
                    dispose(); // Close the dialog
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


        add(buttonPanel, BorderLayout.SOUTH);

        // Populate the list with current cart items when the dialog is shown
        updateCartDisplay();
    }

    // Method to update the list display from the parent interface's cart data
    public void updateCartDisplay() {
        cartListModel.clear(); // Clear the current list model
        for (CartItem item : parentInterface.getCartItems()) {
            cartListModel.addElement(item); // Add items from the parent's cart
        }
    }
}


public class ClientMenuInterface extends JFrame { // Changed class name

    // Define the colors used in the GUI (reusing from other interfaces)
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    private JPanel dishesContainerPanel; // Panel to hold the grid of dishes
    private List<CartItem> shoppingCart; // List to hold items in the shopping cart

    // Constructor for the ClientMenuInterface class
    public ClientMenuInterface() {
        // Initialize the shopping cart list
        shoppingCart = new ArrayList<>();

        // Set up the main window properties
        setTitle("Menu Client"); // Window title
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
                    ClientMenuInterface.this.dispose(); // Close current window
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new Login().setVisible(true); // Open Login interface
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
                    ClientMenuInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new Login().setVisible(true);
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
        JLabel titleLabel = new JLabel("Menu"); // Changed title
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 0; // Row 0
        mainGbc.gridwidth = 2; // Span across columns (adjust if more columns are added)
        mainGbc.anchor = GridBagConstraints.NORTH; // Align to the top
        mainGbc.insets = new Insets(20, 10, 20, 10); // Padding
        mainPanel.add(titleLabel, mainGbc);

        // Cart Button (Top Right)
        JButton cartButton = new JButton(); // Create a JButton for the cart icon
        cartButton.setFocusPainted(false);
        cartButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding
        cartButton.setContentAreaFilled(false); // Make button transparent
        cartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor

        // Load the cart icon from a file
        ImageIcon cartIcon = null;
        try {
            // Load from a file:
            // IMPORTANT: Update this path to where your cart icon is located
            Image img = ImageIO.read(new File("shopping-cart.png")); // Load image from file
            cartIcon = new ImageIcon(img);

        } catch (Exception e) {
            System.err.println("Error loading cart icon: " + e.getMessage());
            // Fallback to text if icon loading fails
            cartButton.setText("Panier");
            cartButton.setFont(new Font("Arial", Font.BOLD, 16));
            cartButton.setBackground(COLOR_BUTTON_BACKGROUND);
            cartButton.setForeground(COLOR_BUTTON_TEXT);
            cartButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            cartButton.setOpaque(true);
            cartButton.setBorderPainted(false);
            cartButton.setContentAreaFilled(true); // Revert to opaque if using text
        }

        if (cartIcon != null) {
            cartButton.setIcon(cartIcon); // Set the loaded icon
            cartButton.setPreferredSize(new Dimension(60, 60)); // Set preferred size for icon button
            cartButton.setMaximumSize(new Dimension(60, 60));
            cartButton.setMinimumSize(new Dimension(60, 60));
        }


        cartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cart button clicked!");
                // Open the shopping cart dialog
                CartDialog cartDialog = new CartDialog(ClientMenuInterface.this, ClientMenuInterface.this);
                cartDialog.setVisible(true);
            }
        });

        mainGbc.gridx = 1; // Column 1 (right side)
        mainGbc.gridy = 0; // Row 0 (same row as title)
        mainGbc.anchor = GridBagConstraints.NORTHEAST; // Position at top-right
        mainGbc.insets = new Insets(20, 10, 10, 20); // Padding
        mainGbc.weightx = 0.0; // Do not take extra horizontal space
        mainPanel.add(cartButton, mainGbc);


        // Panel to hold the grid of dishes
        dishesContainerPanel = new JPanel();
        dishesContainerPanel.setBackground(new Color(250, 246, 233, 150)); // Semi-transparent background
        // Using GridLayout with 0 rows (automatic) and 4 columns, with horizontal and vertical gaps
        dishesContainerPanel.setLayout(new GridLayout(0, 4, 20, 20)); // Rows=0 (automatic), Cols=4, Hgap=20, Vgap=20
        // Use a JScrollPane to make the dish list scrollable
        JScrollPane scrollPane = new JScrollPane(dishesContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // No horizontal scroll
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.setOpaque(false); // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make viewport transparent


        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 1; // Row 1 (below the title/cart button)
        mainGbc.gridwidth = 2; // Span across columns
        mainGbc.weightx = 1.0; // Allow horizontal expansion
        mainGbc.weighty = 1.0; // Allow vertical expansion
        mainGbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
        mainGbc.insets = new Insets(10, 50, 10, 50); // Padding around the dish container
        mainPanel.add(scrollPane, mainGbc);


        // Removed the "+" icon and its related code


        // Add the main background panel to the JFrame
        add(mainPanel);

        // Example: Add some initial placeholder dishes with prices
        // IMPORTANT: Update the image paths for these placeholder dishes
        addDish(new ClientDishPanel("row-1-column-1.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", "8.50 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-1-column-2.png", "Spaghetti Bolognese", "Pasta with meat sauce", "10.00 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-1-column-3.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", "7.00 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-1-column-4.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", "8.50 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-2-column-1.png", "Spaghetti Bolognese", "Pasta with meat sauce", "10.00 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-2-column-2.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", "7.00 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-2-column-3.png", "Pizza Margherita", "Classic pizza with tomato and mozzarella", "8.50 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-2-column-4.png", "Spaghetti Bolognese", "Pasta with meat sauce", "10.00 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-3-column-1.png", "Salade Niçoise", "Salad with tuna, olives, and vegetables", "7.00 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-3-column-2.png", "Nouveau Plat", "Description du nouveau plat", "12.00 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-3-column-3.png", "Nouveau Plat", "Description du nouveau plat", "15.50 €", this)); // Pass reference
        addDish(new ClientDishPanel("row-3-column-4.png", "Nouveau Plat", "Description du nouveau plat", "9.00 €", this)); // Pass reference


    }

    // Method to add a dish panel to the container
    private void addDish(ClientDishPanel dishPanel) { // Changed parameter type
        dishesContainerPanel.add(dishPanel);
        dishesContainerPanel.revalidate(); // Re-layout the container
        dishesContainerPanel.repaint(); // Repaint the container
    }

    // Method to add an item to the shopping cart
    public void addItemToCart(CartItem item) {
        // Check if the item is already in the cart
        boolean found = false;
        for (CartItem existingItem : shoppingCart) {
            // Simple check based on name and price (you might need a unique ID in a real app)
            if (existingItem.getName().equals(item.getName()) && existingItem.getPrice().equals(item.getPrice())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity()); // Increase quantity
                found = true;
                break;
            }
        }
        if (!found) {
            shoppingCart.add(item); // Add new item if not found
        }
        System.out.println("Current cart size: " + shoppingCart.size()); // For debugging
        // Optional: Show a confirmation message here or in the CartDialog
    }

    // Method to remove an item from the shopping cart
    public void removeItemFromCart(CartItem item) {
        shoppingCart.remove(item);
        System.out.println("Item removed from cart: " + item.getName()); // For debugging
        // If the cart dialog is open, update its display
        // This requires a way to access the open dialog instance, or the dialog
        // could call back to update itself after removal is confirmed.
        // For now, the CartDialog's remove button directly calls this method.
        // The CartDialog will need to call updateCartDisplay() after this.
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
                new ClientMenuInterface().setVisible(true);
            }
        });
    }
}
