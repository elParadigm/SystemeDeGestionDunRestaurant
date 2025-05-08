package gui; // Declare the package

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File; // Keep File import for file-based loading
import javax.imageio.ImageIO; // Keep ImageIO import for image loading

import java.util.ArrayList; // To manage the list of orders
import java.util.List; // To manage the list of orders
import java.util.stream.Collectors; // For filtering orders

// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // You might need this import depending on where BackgroundPanel is defined

// Import the ServeuseInterface class to allow returning
import gui.ServeuseInterface;
// Import OrderStatus and Order classes directly from the gui package
import gui.OrderStatus;
import gui.Order;


// Custom JPanel for displaying a single Order item in the waitress interface
class ServeuseOrderPanel extends JPanel {
    private JLabel orderInfoLabel;
    private JLabel statusLabel;
    private Order order; // The order this panel represents

    public ServeuseOrderPanel(Order order) {
        this.order = order;

        setLayout(new GridBagLayout()); // Use GridBagLayout for flexible layout
        setBackground(new Color(250, 246, 233)); // Use a color similar to the panel background
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 200), 1), // Light border
                BorderFactory.createEmptyBorder(10, 10, 10, 10) // Padding
        ));
        // Set preferred size, allowing height to be determined by content
        setPreferredSize(new Dimension(350, 100)); // Adjusted size for waitress view
        setMaximumSize(new Dimension(500, 150)); // Limit maximum width


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
        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        add(orderInfoLabel, gbc);

        // Status Label
        statusLabel = new JLabel("Statut: " + order.getStatus().toString()); // Display current status
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(getStatusColor(order.getStatus())); // Set color based on status
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Row 1
        gbc.weightx = 0.0; // Do not take extra horizontal space
        gbc.anchor = GridBagConstraints.EAST; // Align to the east
        add(statusLabel, gbc);

        // Add a vertical glue to push components to the top
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0; // Take up extra vertical space
        gbc.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), gbc);
    }

    // Helper method to get color based on status
    private Color getStatusColor(OrderStatus status) {
        switch (status) {
            case PENDING:
                return Color.ORANGE;
            case PREPARING:
                return Color.BLUE;
            case FINISHED:
                return Color.GREEN.darker();
            case CANCELLED:
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }

    // Method to update the status display (if order status changes externally)
    public void updateStatusDisplay() {
        statusLabel.setText("Statut: " + order.getStatus().toString());
        statusLabel.setForeground(getStatusColor(order.getStatus()));
        revalidate();
        repaint();
    }

    public Order getOrder() {
        return order;
    }
}

// Panel to display a list of orders
class OrderListPanel extends JPanel {
    private JPanel listContainer; // Panel to hold the order panels
    private JScrollPane scrollPane;

    public OrderListPanel() {
        setLayout(new BorderLayout());
        setOpaque(false); // Make panel transparent

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS)); // Stack orders vertically
        listContainer.setOpaque(false); // Make container transparent

        scrollPane = new JScrollPane(listContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // No horizontal scroll
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove scroll pane border
        scrollPane.setOpaque(false); // Make scroll pane transparent
        scrollPane.getViewport().setOpaque(false); // Make viewport transparent

        add(scrollPane, BorderLayout.CENTER);
    }

    public void displayOrders(List<Order> orders) {
        listContainer.removeAll(); // Clear previous orders
        for (Order order : orders) {
            listContainer.add(new ServeuseOrderPanel(order)); // Add a panel for each order
            listContainer.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between orders
        }
        listContainer.revalidate();
        listContainer.repaint();
    }
}

// Panel to generate and display invoices
class InvoicePanel extends JPanel {
    private JTextArea invoiceArea;
    private JButton generateButton;
    private JComboBox<Order> finishedOrdersComboBox;
    private List<Order> allOrders; // Reference to the list of all orders

    public InvoicePanel(List<Order> allOrders) {
        this.allOrders = allOrders;
        setLayout(new BorderLayout());
        setOpaque(false); // Make panel transparent
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Panel for controls (ComboBox and Button)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setOpaque(false);

        controlPanel.add(new JLabel("Sélectionner une commande terminée:"));

        // ComboBox for finished orders
        finishedOrdersComboBox = new JComboBox<>();
        finishedOrdersComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        controlPanel.add(finishedOrdersComboBox);

        // Generate Invoice Button
        generateButton = new JButton("Générer Facture");
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setBackground(new Color(60, 179, 113)); // Medium sea green color
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        generateButton.setOpaque(true);
        generateButton.setBorderPainted(false);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Order selectedOrder = (Order) finishedOrdersComboBox.getSelectedItem();
                if (selectedOrder != null) {
                    generateInvoice(selectedOrder);
                } else {
                    invoiceArea.setText("Aucune commande terminée sélectionnée.");
                }
            }
        });
        controlPanel.add(generateButton);

        add(controlPanel, BorderLayout.NORTH);

        // TextArea to display the invoice
        invoiceArea = new JTextArea();
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 14)); // Monospaced for alignment
        invoiceArea.setEditable(false);
        invoiceArea.setBackground(new Color(255, 255, 240)); // Light yellow background
        invoiceArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane invoiceScrollPane = new JScrollPane(invoiceArea);
        add(invoiceScrollPane, BorderLayout.CENTER);

        // Populate the combo box initially
        updateFinishedOrdersComboBox();
    }

    // Method to update the combo box with finished orders
    public void updateFinishedOrdersComboBox() {
        finishedOrdersComboBox.removeAllItems();
        List<Order> finishedOrders = allOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.FINISHED)
                .collect(Collectors.toList());
        for (Order order : finishedOrders) {
            finishedOrdersComboBox.addItem(order);
        }
    }

    // Method to generate and display the invoice text
    private void generateInvoice(Order order) {
        StringBuilder invoiceText = new StringBuilder();
        invoiceText.append("----------------------------------------\n");
        invoiceText.append("           FACTURE - COMMANDE #").append(order.getOrderId()).append("\n");
        invoiceText.append("----------------------------------------\n");
        invoiceText.append("Table: ").append(order.getTableNumber()).append("\n");
        invoiceText.append("Date: ").append(new java.util.Date()).append("\n"); // Simple date
        invoiceText.append("----------------------------------------\n");
        invoiceText.append("Articles:\n");
        double total = 0;
        // In a real app, you'd get price from dish data, not just item name
        // For this example, let's assume a placeholder price or retrieve from a map
        double placeholderPricePerItem = 5.0; // Example placeholder price
        for (String item : order.getItems()) {
            // Simple price calculation for demonstration
            double itemPrice = placeholderPricePerItem; // Replace with actual price lookup
            invoiceText.append(String.format("- %-25s %.2f €\n", item, itemPrice));
            total += itemPrice;
        }
        invoiceText.append("----------------------------------------\n");
        invoiceText.append(String.format("TOTAL: %30.2f €\n", total));
        invoiceText.append("----------------------------------------\n");

        invoiceArea.setText(invoiceText.toString());
    }
}


public class ServeuseOrderInterface extends JFrame { // Changed class name

    // Define the colors used in the GUI (reusing from other interfaces)
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    private JPanel contentAreaPanel; // Panel to swap different views
    private OrderListPanel receivedOrdersPanel;
    private OrderListPanel processingOrdersPanel;
    private InvoicePanel invoicePanel;

    private List<Order> allOrders; // List to hold all orders (for demonstration)

    // Constructor for the ServeuseOrderInterface class
    public ServeuseOrderInterface() { // Changed constructor name
        // Initialize the orders list with some placeholder data
        allOrders = new ArrayList<>();
        allOrders.add(new Order(1, List.of("Pizza Margherita", "Coca-Cola"), "Table 5"));
        allOrders.add(new Order(2, List.of("Spaghetti Bolognese", "Salade Niçoise"), "Table 2"));
        allOrders.add(new Order(3, List.of("Steak Frites"), "Table 8"));
        allOrders.add(new Order(4, List.of("Soupe du jour", "Pain"), "Table 1"));
        allOrders.add(new Order(5, List.of("Pizza Margherita", "Pizza Margherita"), "Table 5"));
        allOrders.add(new Order(6, List.of("Spaghetti Carbonara"), "Table 3"));
        allOrders.add(new Order(7, List.of("Salade César"), "Table 7"));
        allOrders.add(new Order(8, List.of("Burger Classique", "Frites"), "Table 4"));
        allOrders.add(new Order(9, List.of("Poisson Grillé", "Légumes"), "Table 6"));
        allOrders.add(new Order(10, List.of("Tiramisu", "Café"), "Table 2"));

        // Simulate some orders being processed or finished
        allOrders.get(1).setStatus(OrderStatus.PREPARING);
        allOrders.get(3).setStatus(OrderStatus.PREPARING);
        allOrders.get(5).setStatus(OrderStatus.FINISHED);
        allOrders.get(7).setStatus(OrderStatus.FINISHED);


        // Set up the main window properties
        setTitle("Interface Serveuse - Commandes"); // Changed window title
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
        }

        if (backIcon != null) {
            backIconLabel.setIcon(backIcon); // Set the loaded icon
            backIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover
            backIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding

            backIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Back icon clicked!");
                    ServeuseOrderInterface.this.dispose(); // Close current window
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new ServeuseInterface().setVisible(true); // Return to ServeuseInterface
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
                    ServeuseOrderInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new ServeuseInterface().setVisible(true);
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
        JLabel titleLabel = new JLabel("Gestion des Commandes"); // Changed title
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 0; // Row 0
        mainGbc.gridwidth = 3; // Span across columns
        mainGbc.anchor = GridBagConstraints.NORTH; // Align to the top
        mainGbc.insets = new Insets(20, 10, 20, 10); // Padding
        mainPanel.add(titleLabel, mainGbc);

        // Button Panel for View Switching
        JPanel viewButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        viewButtonPanel.setOpaque(false); // Make transparent

        JButton receivedOrdersButton = new JButton("Commandes Reçues");
        styleButton(receivedOrdersButton);
        receivedOrdersButton.addActionListener(e -> showView("received"));
        viewButtonPanel.add(receivedOrdersButton);

        JButton processingOrdersButton = new JButton("Commandes en Cours");
        styleButton(processingOrdersButton);
        processingOrdersButton.addActionListener(e -> showView("processing"));
        viewButtonPanel.add(processingOrdersButton);

        JButton invoiceButton = new JButton("Générer Facture");
        styleButton(invoiceButton);
        invoiceButton.addActionListener(e -> showView("invoice"));
        viewButtonPanel.add(invoiceButton);


        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 1; // Row 1 (below title)
        mainGbc.gridwidth = 3; // Span across columns
        mainGbc.anchor = GridBagConstraints.CENTER; // Center the button panel
        mainGbc.fill = GridBagConstraints.NONE; // Do not fill
        mainGbc.weighty = 0.0; // Do not take extra vertical space
        mainPanel.add(viewButtonPanel, mainGbc);


        // Content Area Panel (where different views will be displayed)
        contentAreaPanel = new JPanel(new CardLayout()); // Use CardLayout to swap panels
        contentAreaPanel.setOpaque(false); // Make transparent
        contentAreaPanel.setBorder(BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1)); // Add a border

        // Initialize the view panels
        receivedOrdersPanel = new OrderListPanel();
        processingOrdersPanel = new OrderListPanel();
        invoicePanel = new InvoicePanel(allOrders); // Pass the list of all orders

        // Add panels to the CardLayout
        contentAreaPanel.add(receivedOrdersPanel, "received");
        contentAreaPanel.add(processingOrdersPanel, "processing");
        contentAreaPanel.add(invoicePanel, "invoice");


        mainGbc.gridx = 0; // Column 0
        mainGbc.gridy = 2; // Row 2 (below button panel)
        mainGbc.gridwidth = 3; // Span across columns
        mainGbc.weightx = 1.0; // Allow horizontal expansion
        mainGbc.weighty = 1.0; // Allow vertical expansion
        mainGbc.fill = GridBagConstraints.BOTH; // Fill both horizontally and vertically
        mainGbc.insets = new Insets(10, 50, 10, 50); // Padding around the content area
        mainPanel.add(contentAreaPanel, mainGbc);


        // Add the main background panel to the JFrame
        add(mainPanel);

        // Show the default view (e.g., received orders)
        showView("received");
    }

    // Helper method to style buttons
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(COLOR_BUTTON_BACKGROUND);
        button.setForeground(COLOR_BUTTON_TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setOpaque(true);
        button.setBorderPainted(false);
    }

    // Method to switch between different views in the content area
    private void showView(String viewName) {
        CardLayout cl = (CardLayout) (contentAreaPanel.getLayout());
        cl.show(contentAreaPanel, viewName);

        // Update the content of the visible panel
        if ("received".equals(viewName)) {
            List<Order> receivedOrders = allOrders.stream()
                    .filter(order -> order.getStatus() == OrderStatus.PENDING)
                    .collect(Collectors.toList());
            receivedOrdersPanel.displayOrders(receivedOrders);
        } else if ("processing".equals(viewName)) {
            List<Order> processingOrders = allOrders.stream()
                    .filter(order -> order.getStatus() == OrderStatus.PREPARING)
                    .collect(Collectors.toList());
            processingOrdersPanel.displayOrders(processingOrders);
        } else if ("invoice".equals(viewName)) {
            invoicePanel.updateFinishedOrdersComboBox(); // Refresh the list of finished orders
        }
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
                new ServeuseOrderInterface().setVisible(true);
            }
        });
    }
}
