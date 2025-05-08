package gui; // Declare the package

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File; // Keep File import for file-based loading
import javax.imageio.ImageIO; // Keep ImageIO import for image loading

import java.util.ArrayList; // To manage the list of orders
import java.util.List; // To manage the list of orders

// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // You might need this import depending on where BackgroundPanel is defined

// Import the CuisinierInterface class to allow returning


// Define an enum for order status
enum OrderStatus {
    PENDING("En attente"),
    PREPARING("En préparation"),
    FINISHED("Terminée"),
    CANCELLED("Annulée");

    private String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}

// Define a simple class to represent an Order
class Order {
    private int orderId;
    private List<String> items; // Simple list of item names for this example
    private OrderStatus status;
    private String tableNumber; // Added table number for context

    public Order(int orderId, List<String> items, String tableNumber) {
        this.orderId = orderId;
        this.items = items;
        this.tableNumber = tableNumber;
        this.status = OrderStatus.PENDING; // Default status is PENDING
    }

    public int getOrderId() {
        return orderId;
    }

    public List<String> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getTableNumber() {
        return tableNumber;
    }
}

// Custom JPanel for displaying a single Order item
class OrderPanel extends JPanel {
    private JLabel orderInfoLabel;
    private JLabel statusLabel;
    private JComboBox<OrderStatus> statusComboBox;
    private JButton cancelButton; // Button to quickly cancel

    private Order order; // The order this panel represents
    private ChefOrderInterface parentInterface; // Reference to the parent interface

    public OrderPanel(Order order, ChefOrderInterface parentInterface) {
        this.order = order;
        this.parentInterface = parentInterface;

        setLayout(new GridBagLayout()); // Use GridBagLayout for flexible layout
        setBackground(new Color(250, 246, 233)); // Use a color similar to the panel background
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 200), 1), // Light border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
        ));
        // Set preferred size, allowing height to be determined by content
        setPreferredSize(new Dimension(400, 150));
        setMaximumSize(new Dimension(600, 200)); // Limit maximum width


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.anchor = GridBagConstraints.WEST; // Align components to the west

        // Order Info Label (Displays ID, Table, and Items)
        StringBuilder infoText = new StringBuilder("<html><b>Commande #" + order.getOrderId() + "</b> (Table: " + order.getTableNumber() + ")<br>");
        for (String item : order.getItems()) {
            infoText.append("- ").append(item).append("<br>");
        }
        infoText.append("</html>");
        orderInfoLabel = new JLabel(infoText.toString());
        orderInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        orderInfoLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        gbc.gridwidth = 2; // Span across two columns
        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        add(orderInfoLabel, gbc);

        // Status Label
        statusLabel = new JLabel("Statut:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Row 1
        gbc.gridwidth = 1; // Reset gridwidth
        gbc.weightx = 0.0; // Do not take extra horizontal space
        gbc.fill = GridBagConstraints.NONE; // Do not fill
        add(statusLabel, gbc);

        // Status ComboBox
        statusComboBox = new JComboBox<>(OrderStatus.values()); // Use enum values
        statusComboBox.setSelectedItem(order.getStatus()); // Set initial status
        statusComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        statusComboBox.setBackground(new Color(240, 230, 210)); // Light background
        statusComboBox.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1; // Column 1
        gbc.gridy = 1; // Row 1
        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        statusComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderStatus selectedStatus = (OrderStatus) statusComboBox.getSelectedItem();
                order.setStatus(selectedStatus); // Update the order object's status
                System.out.println("Order #" + order.getOrderId() + " status changed to: " + selectedStatus);
                // In a real application, you would save this status change to your backend/data store
            }
        });
        add(statusComboBox, gbc);

        // Cancel Button
        cancelButton = new JButton("Annuler la commande");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setBackground(Color.RED); // Red background
        cancelButton.setForeground(Color.WHITE); // White text
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        gbc.gridx = 0; // Column 0
        gbc.gridy = 2; // Row 2
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.EAST; // Align to the east (right)
        gbc.weightx = 0.0; // Do not take extra horizontal space
        gbc.fill = GridBagConstraints.NONE; // Do not fill
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(parentInterface,
                        "Êtes-vous sûr de vouloir annuler la commande #" + order.getOrderId() + "?",
                        "Confirmer l'annulation",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    order.setStatus(OrderStatus.CANCELLED); // Set status to cancelled
                    statusComboBox.setSelectedItem(OrderStatus.CANCELLED); // Update the combo box
                    // In a real application, you would persist this change
                    System.out.println("Order #" + order.getOrderId() + " has been cancelled.");
                    // Optionally, remove the order from the display or grey it out
                    // parentInterface.removeOrderPanel(OrderPanel.this); // Example of removing the panel
                }
            }
        });
        add(cancelButton, gbc);

        // Add a vertical glue to push components to the top
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0; // Take up extra vertical space
        gbc.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), gbc);
    }
}


public class ChefOrderInterface extends JFrame { // Changed class name

    // Define the colors used in the GUI (reusing from other interfaces)
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    private JPanel ordersContainerPanel; // Panel to hold the list of orders
    private List<Order> currentOrders; // List to hold current orders

    // Constructor for the ChefOrderInterface class
    public ChefOrderInterface() { // Changed constructor name
        // Initialize the orders list
        currentOrders = new ArrayList<>();

        // Set up the main window properties
        setTitle("Interface Cuisinier - Commandes"); // Changed window title
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
            // Fallback to text if icon loading fails
            JLabel fallbackBackLabel = new JLabel("←");
            fallbackBackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            fallbackBackLabel.setForeground(COLOR_TEXT_DARK);
            fallbackBackLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            fallbackBackLabel.setOpaque(false); // Make the fallback label transparent
            fallbackBackLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Back button (fallback) clicked!");
                    ChefOrderInterface.this.dispose(); // Changed class reference
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new CuisinierInterface().setVisible(true); // Return to CuisinierInterface
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

        if (backIcon != null) {
            backIconLabel.setIcon(backIcon); // Set the loaded icon
            backIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover
            // Add padding around the icon
            backIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Add a MouseListener to the back icon label
            backIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // This method is called when the mouse is clicked on the label
                    System.out.println("Back icon clicked!");
                    // Close the current ChefOrderInterface window
                    ChefOrderInterface.this.dispose(); // Changed class reference

                    // Open the CuisinierInterface window on the Event Dispatch Thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new CuisinierInterface().setVisible(true); // Return to CuisinierInterface
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
        }

        // Add the main background panel to the JFrame
        add(mainPanel);


        // Title Label (Updated Text)
        JLabel titleLabel = new JLabel("Liste des Commandes"); // Changed title
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 0; // Row 0
        mainGbc.gridwidth = 2; // Span across columns (adjust if more columns are added)
        mainGbc.anchor = GridBagConstraints.NORTH; // Align to the top
        mainGbc.insets = new Insets(20, 10, 20, 10); // Padding
        mainPanel.add(titleLabel, mainGbc);

        // Panel to hold the list of orders
        ordersContainerPanel = new JPanel();
        ordersContainerPanel.setBackground(new Color(250, 246, 233, 150)); // Semi-transparent background
        ordersContainerPanel.setLayout(new BoxLayout(ordersContainerPanel, BoxLayout.Y_AXIS)); // Arrange orders vertically
        // Use a JScrollPane to make the order list scrollable
        JScrollPane scrollPane = new JScrollPane(ordersContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // No horizontal scroll
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.setOpaque(false); // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false);


        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 1; // Row 1 (below the title)
        mainGbc.gridwidth = 2; // Span across columns
        mainGbc.weightx = 1.0; // Allow horizontal expansion
        mainGbc.weighty = 1.0; // Allow vertical expansion
        mainGbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
        mainGbc.insets = new Insets(10, 50, 10, 50); // Padding around the order container
        mainPanel.add(scrollPane, mainGbc);


        // Add the main background panel to the JFrame
        add(mainPanel);

        // Example: Add some initial placeholder orders
        addOrder(new Order(1, List.of("Pizza Margherita", "Coca-Cola"), "Table 5"));
        addOrder(new Order(2, List.of("Spaghetti Bolognese", "Salade Niçoise"), "Table 2"));
        addOrder(new Order(3, List.of("Steak Frites"), "Table 8"));
        addOrder(new Order(4, List.of("Soupe du jour", "Pain"), "Table 1"));
        addOrder(new Order(5, List.of("Pizza Margherita", "Pizza Margherita"), "Table 5")); // Another order for Table 5
        addOrder(new Order(6, List.of("Spaghetti Carbonara"), "Table 3"));
        addOrder(new Order(7, List.of("Salade César"), "Table 7"));
        addOrder(new Order(8, List.of("Burger Classique", "Frites"), "Table 4"));
        addOrder(new Order(9, List.of("Poisson Grillé", "Légumes"), "Table 6"));
        addOrder(new Order(10, List.of("Tiramisu", "Café"), "Table 2")); // Dessert order


    }

    // Method to add an order to the list and display it
    public void addOrder(Order order) {
        currentOrders.add(order);
        OrderPanel orderPanel = new OrderPanel(order, this); // Create a panel for the order
        ordersContainerPanel.add(orderPanel); // Add the panel to the container
        ordersContainerPanel.revalidate(); // Re-layout the container
        ordersContainerPanel.repaint(); // Repaint the container
    }

    // Method to remove an order panel (if needed, e.g., after completion/cancellation)
    public void removeOrderPanel(OrderPanel orderPanel) {
        ordersContainerPanel.remove(orderPanel);
        ordersContainerPanel.revalidate();
        ordersContainerPanel.repaint();
        // Note: This only removes the panel from the display, not from the currentOrders list.
        // You might want to remove it from the list as well depending on your application logic.
        // currentOrders.remove(orderPanel.getOrder()); // Assuming OrderPanel has a getOrder() method
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
                new ChefOrderInterface().setVisible(true);
            }
        });
    }
}
