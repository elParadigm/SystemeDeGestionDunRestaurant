package gui; // Declare the package

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Date; // Import java.util.Date

// Import necessary DAO and Model classes
import dao.CommandeDAO;
import dao.FactureDAO; // Import FactureDAO
import model.Commande;
import model.Facture; // Import Facture model
import model.Plat; // Assuming you need Plat for CartItem details
import gui.CartItem; // Assuming CartItem uses Plat

// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // You might need this import depending on where BackgroundPanel is defined

// Import the ServeuseInterface class to allow returning
import gui.ServeuseInterface;


// Assuming OrderStatus enum exists based on usage in ServeuseOrderPanel
// If you haven't defined this enum, you'll need to add it.
/*
enum OrderStatus {
    PENDING("En attente"),
    PREPARING("En préparation"),
    FINISHED("Terminée"),
    CANCELLED("Annulée"); // Add other statuses as needed

    private String statusString;

    OrderStatus(String statusString) {
        this.statusString = statusString;
    }

    @Override
    public String toString() {
        return statusString; // This is what will be displayed by default (but we use a custom renderer)
    }

    // Helper method to convert database status string to enum
    public static OrderStatus fromString(String text) {
        if (text != null) {
            for (OrderStatus status : OrderStatus.values()) {
                if (text.equalsIgnoreCase(status.statusString)) {
                    return status;
                }
            }
        }
        // Return a default status or throw an exception for unknown status strings
        return PENDING; // Defaulting to PENDING for unknown strings
    }
}
*/


// Custom JPanel for displaying a single Order item in the waitress interface
// Modified to accept a model.Commande object and remove table number display
class ServeuseOrderPanel extends JPanel {
    private JLabel orderInfoLabel;
    private JLabel statusLabel;
    private Commande order; // The Commande object this panel represents

    public ServeuseOrderPanel(Commande order ) {
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

        // Order Info Label (Displays ID and Items)
        StringBuilder infoText = new StringBuilder("<html><b>Commande #" + order.getIdCommande() + "</b><br>");
        // Assuming CartItem has getName() and getQuantity() and getPrice() (or access to Plat price)
        if (order.getItems() != null) {
            for (CartItem item : order.getItems()) {
                // Assuming CartItem stores Plat and you can access Plat's name and price
                Plat plat = item.getPlat(); // Assuming CartItem has getPlat() method
                if (plat != null) {
                    infoText.append("- ").append(plat.getNom()).append(" x ").append(item.getQuantity())
                            .append(" (").append(String.format("%.2f€", plat.getPrix())).append(" each)")
                            .append("<br>");
                } else {
                    infoText.append("- Unknown Item x ").append(item.getQuantity()).append("<br>");
                }
            }
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
        // Using getStatut() from Commande model and converting to OrderStatus enum for color
        // Assuming OrderStatus enum exists and has fromString method
        OrderStatus currentStatus = OrderStatus.fromString(order.getStatut()); // Assuming OrderStatus enum exists
        statusLabel = new JLabel("Statut: " + currentStatus.toString()); // Display current status
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(getStatusColor(currentStatus)); // Set color based on status
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Row 1
        gbc.weightx = 0.0; // Do not take extra horizontal space
        gbc.anchor = GridBagConstraints.EAST; // Align to the east
        add(statusLabel, gbc);

        // Add a vertical glue to push components to the top (Optional in GridBagLayout)
        // gbc.gridx = 0; gbc.gridy = 2; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.VERTICAL; add(Box.createVerticalGlue(), gbc);
    }

    // Helper method to get color based on status
    // Assuming OrderStatus enum exists
    private Color getStatusColor(OrderStatus status) {
        switch (status) {
            case PENDING: // Assuming "en_attente" maps to PENDING
                return Color.ORANGE;
            case PREPARING: // Assuming "en_traitement" maps to PREPARING
                return Color.BLUE;
            case FINISHED: // Assuming "terminee" maps to FINISHED
                return Color.GREEN.darker();
            case CANCELLED: // Assuming another status string maps to CANCELLED
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }

    // Method to update the status display (if order status changes externally)
    // Modified to use the Commande object's status
    public void updateStatusDisplay() {
        OrderStatus currentStatus = OrderStatus.fromString(order.getStatut()); // Assuming OrderStatus enum exists
        statusLabel.setText("Statut: " + currentStatus.toString());
        statusLabel.setForeground(getStatusColor(currentStatus));
        revalidate();
        repaint();
    }

    public Commande getOrder() {
        return order;
    }
}


// Panel to display a list of orders
// Modified to work with List<Commande>
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

    // Modified to accept List<Commande>
    public void displayOrders(List<Commande> orders) {
        listContainer.removeAll(); // Clear previous orders
        if (orders != null && !orders.isEmpty()) { // Check if the list is not null AND not empty
            // Restore BoxLayout if adding items
            listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
            for (Commande order : orders) {
                listContainer.add(new ServeuseOrderPanel(order)); // Add a panel for each order
                listContainer.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between orders
            }
        } else {
            // Use GridBagLayout to center the message when no items are found or list is null/empty
            listContainer.setLayout(new GridBagLayout()); // Set layout for centering message
            JLabel noOrdersLabel = new JLabel("Aucune commande dans cette catégorie.");
            noOrdersLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noOrdersLabel.setForeground(new Color(50, 50, 50));
            JPanel centeredPanel = new JPanel(new GridBagLayout());
            centeredPanel.setOpaque(false);
            centeredPanel.add(noOrdersLabel);
            listContainer.add(centeredPanel, new GridBagConstraints());
        }


        listContainer.revalidate();
        listContainer.repaint();
    }
}

// Panel to generate and display invoices
// Modified to work with List<Commande> and remove table number from invoice
class InvoicePanel extends JPanel {
    private JTextArea invoiceArea;
    private JButton generateButton;
    private JComboBox<Commande> finishedOrdersComboBox; // ComboBox for Commande objects
    private List<Commande> allOrders; // Reference to the list of all orders (Commande objects)

    public InvoicePanel(List<Commande> allOrders) {
        this.allOrders = allOrders;
        setLayout(new BorderLayout());
        setOpaque(false); // Make panel transparent
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        // Panel for controls (ComboBox and Button)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setOpaque(false);

        controlPanel.add(new JLabel("Sélectionner une commande terminée:"));

        // ComboBox for finished orders - now holds Commande objects
        finishedOrdersComboBox = new JComboBox<>();
        finishedOrdersComboBox.setFont(new Font("Arial", Font.PLAIN, 14));

        // *** Add custom renderer to display Command ID ***
        finishedOrdersComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Commande) {
                    Commande order = (Commande) value;
                    setText("Commande # " + order.getIdCommande()); // Display Command ID
                } else if (value == null) {
                    // Handle the case where no item is selected or list is empty
                    setText("--- Aucune commande terminée ---"); // More informative placeholder
                }
                return this;
            }
        });
        // **************************************************


        controlPanel.add(finishedOrdersComboBox);

        // Generate Invoice Button
        generateButton = new JButton("Générer Facture & Sauvegarder"); // Updated button text
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
                Commande selectedOrder = (Commande) finishedOrdersComboBox.getSelectedItem();
                // Check if a valid order is selected (null check and ID check)
                if (selectedOrder != null && selectedOrder.getIdCommande() > 0) { // Assuming IDs are positive
                    // Calculate total BEFORE generating text
                    double totalAmount = calculateTotal(selectedOrder);

                    // --- DATABASE INTEGRATION: Save Facture ---
                    FactureDAO factureDAO = new FactureDAO();
                    // Assuming Facture model has a constructor or setters for date, command ID, and total
                    // Using java.util.Date for the current date
                    Facture newFacture = new Facture(
                            new Date(), // Current date/time for dateFacture (java.util.Date)
                            selectedOrder.getIdCommande(),
                            0, // idFacture will be auto-generated by DB, use 0 or default
                            totalAmount
                    );
                    // If your Facture model only has the 4-arg constructor:
                    // Facture newFacture = new Facture(new Date(), selectedOrder.getIdCommande(), 0, totalAmount);

                    boolean success = factureDAO.insertFacture(newFacture); // Save to database

                    if (success) {
                        generateInvoice(selectedOrder, totalAmount); // Generate and display invoice text with calculated total
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Facture générée et enregistrée avec succès! ID Facture: " + newFacture.getIdFacture(), "Succès Facture", JOptionPane.INFORMATION_MESSAGE);
                        // Optionally, update the order status to "Facturée" or similar if needed
                        // You would need a method in CommandeDAO for this.
                        // Example: commandeDAO.updateOrderStatus(selectedOrder.getIdCommande(), "Facturée");
                    } else {
                        invoiceArea.setText("Échec de la génération et de l'enregistrement de la facture.");
                        JOptionPane.showMessageDialog(InvoicePanel.this, "Échec de l'enregistrement de la facture dans la base de données.", "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    invoiceArea.setText("Veuillez sélectionner une commande terminée valide.");
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

    // Helper method to calculate the total amount of an order
    private double calculateTotal(Commande order) {
        double total = 0;
        if (order != null && order.getItems() != null) {
            for (CartItem item : order.getItems()) {
                Plat plat = item.getPlat(); // Assuming CartItem has getPlat() method
                if (plat != null) {
                    total += plat.getPrix() * item.getQuantity(); // Use price from Plat
                }
            }
        }
        return total;
    }


    // Method to update the combo box with finished orders
    // Modified to filter Commande objects based on status
    public void updateFinishedOrdersComboBox() {
        finishedOrdersComboBox.removeAllItems();
        // Add a default "select" item if desired
        // finishedOrdersComboBox.addItem(null); // Add null to represent no selection


        if (allOrders != null) {
            // Assuming Commande model has getIdCommande() and getStatut()
            List<Commande> finishedOrders = allOrders.stream()
                    .filter(order -> {
                        String status = order.getStatut();
                        // Check for null status defensively
                        return status != null && "terminee".equalsIgnoreCase(status); // Filter by status string
                    })
                    .collect(Collectors.toList());
            for (Commande order : finishedOrders) {
                // Only add orders with valid IDs to avoid issues with the placeholder ID logic
                if (order.getIdCommande() > 0) { // Assuming 0 is not a valid command ID
                    finishedOrdersComboBox.addItem(order);
                }
            }
        }
        // Add a placeholder item if no *valid* finished orders were added
        if (finishedOrdersComboBox.getItemCount() == 0) {
            // Add a null item handled by the renderer for the placeholder text
            finishedOrdersComboBox.addItem(null);
            finishedOrdersComboBox.setSelectedItem(null); // Select the null item initially
        } else {
            finishedOrdersComboBox.setSelectedIndex(0); // Select the first finished order if available
        }
    }

    // Method to generate and display the invoice text - Removed Table Number
    // Modified to accept pre-calculated total
    private void generateInvoice(Commande order, double total) {
        StringBuilder invoiceText = new StringBuilder();
        invoiceText.append("----------------------------------------\n");
        invoiceText.append("           FACTURE - COMMANDE #").append(order.getIdCommande()).append("\n");
        invoiceText.append("----------------------------------------\n");
        // Removed: invoiceText.append("Table: ").append(order.getTableNumber()).append("\n"); // Table number removed
        invoiceText.append("Date: ").append(order.getHorodatage()).append("\n"); // Use order timestamp (java.util.Date)
        invoiceText.append("Client ID: ").append(order.getIdUtilisateur()).append("\n"); // Display Client ID (assuming getIdClient exists)
        invoiceText.append("----------------------------------------\n");
        invoiceText.append("Articles:\n");
        if (order.getItems() != null) {
            for (CartItem item : order.getItems()) {
                // Use item name and quantity from CartItem, and price from Plat within CartItem
                Plat plat = item.getPlat(); // Assuming CartItem has getPlat() method
                if (plat != null) {
                    double itemPrice = plat.getPrix(); // Use price from Plat
                    invoiceText.append(String.format("- %-25s x %d %.2f €\n", plat.getNom(), item.getQuantity(), itemPrice * item.getQuantity()));
                }
            }
        }
        invoiceText.append("----------------------------------------\n");
        invoiceText.append(String.format("TOTAL: %30.2f €\n", total)); // Use pre-calculated total
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

    private List<Commande> allOrders; // List to hold all orders (Commande objects)
    private CommandeDAO commandeDAO; // DAO to fetch orders

    private int serveuseId; // Field to store the logged-in serveuse ID


    // Constructor for the ServeuseOrderInterface class
    public ServeuseOrderInterface(int serveuseId) { // Changed constructor name
        this.serveuseId = serveuseId; // Store serveuse ID

        // Initialize the DAO
        commandeDAO = new CommandeDAO();

        // Initialize the orders list (will be populated from DB)
        allOrders = new ArrayList<>();


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
                    ServeuseOrderInterface.this.dispose(); // Close current window
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            // Assuming you have a ServeuseInterface class to return to
                            // Pass the serveuseId back if needed by that interface
                            new ServeuseInterface(serveuseId).setVisible(true);
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
                            new ServeuseInterface(serveuseId).setVisible(true);
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

        // Initialize the view panels - Pass the list of all orders to InvoicePanel
        receivedOrdersPanel = new OrderListPanel();
        processingOrdersPanel = new OrderListPanel();
        invoicePanel = new InvoicePanel(allOrders); // Pass the reference to allOrders list


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

        // Load orders from the database and show the default view
        loadOrdersFromDatabase();
        showView("received"); // Show the received orders view after loading
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
        // Re-fetch orders when changing view to get the latest status changes from DB
        loadOrdersFromDatabase(); // Refresh the allOrders list

        if ("received".equals(viewName)) {
            // Filter orders with "En attente" status
            List<Commande> receivedOrders = allOrders.stream()
                    .filter(order -> {
                        String status = order.getStatut();
                        return status != null && "en_attente".equalsIgnoreCase(status);
                    })
                    .collect(Collectors.toList());
            receivedOrdersPanel.displayOrders(receivedOrders);
        } else if ("processing".equals(viewName)) {
            // Filter orders with "En préparation" status
            List<Commande> processingOrders = allOrders.stream()
                    .filter(order -> {
                        String status = order.getStatut();
                        return status != null && "en_traitement".equalsIgnoreCase(status);
                    })
                    .collect(Collectors.toList());
            processingOrdersPanel.displayOrders(processingOrders);
        } else if ("invoice".equals(viewName)) {
            // The InvoicePanel already has a reference to allOrders and filters internally
            invoicePanel.updateFinishedOrdersComboBox(); // Refresh the list of finished orders
        }
    }

    // Method to load orders from the database and populate the allOrders list
    // This method is called on startup and when switching views
    private void loadOrdersFromDatabase() {
        allOrders.clear(); // Clear the existing list
        // Fetch orders from the database using the method that includes items
        // Assuming CommandeDAO has getAllCommandesWithItems() and it works with Singleton
        List<Commande> fetchedOrders = commandeDAO.getAllCommandesWithItems();

        if (fetchedOrders != null) {
            allOrders.addAll(fetchedOrders); // Add fetched orders to the list
            System.out.println("Loaded " + allOrders.size() + " orders from the database.");
        } else {
            System.out.println("No orders found in the database.");
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

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // For testing purposes, pass a dummy serveuse ID (e.g., -1 or 1)
                new ServeuseOrderInterface(-1).setVisible(true);
            }
        });
    }
}