package gui; // Declare the package

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;

import java.util.ArrayList;
import java.util.List;

// Import necessary DAO and Model classes
import dao.CommandeDAO;
import model.Commande;
// Removed: import model.LigneCommande; // No longer directly using LigneCommande in Commande model
import gui.CartItem; // Import CartItem as it's used in Commande model

// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // You might need this import depending on where BackgroundPanel is defined

// Import the CuisinierInterface class to allow returning
import gui.CuisinierInterface;


// Define an enum for order status (using the same as before, assuming it matches your Commande status)


// Custom JPanel for displaying a single Order item
// Modified to accept a model.Commande object
class OrderPanel extends JPanel {
    private JLabel orderInfoLabel;
    private JLabel statusLabel;
    private JComboBox<OrderStatus> statusComboBox;
    private JButton cancelButton;

    private Commande order; // The Commande object this panel represents
    private ChefOrderInterface parentInterface; // Reference to the parent interface
    private CommandeDAO commandeDAO; // DAO to update order status

    public OrderPanel(Commande order, ChefOrderInterface parentInterface) {
        this.order = order;
        this.parentInterface = parentInterface;
        this.commandeDAO = new CommandeDAO(); // Initialize DAO

        setLayout(new GridBagLayout());
        setBackground(new Color(250, 246, 233));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setPreferredSize(new Dimension(400, 150));
        setMaximumSize(new Dimension(600, 200));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Order Info Label (Displays ID, Table, and Items)
        // Using getTableNumber() and getItems() from the Commande model
        StringBuilder infoText = new StringBuilder("<html><b>Commande #" + order.getIdCommande() + "</b> (Table: " + order.getTableNumber() + ")<br>");
        // Assuming CartItem has getName() and getQuantity()
        if (order.getItems() != null) {
            for (CartItem item : order.getItems()) {
                infoText.append("- ").append(item.getName()).append(" x ").append(item.getQuantity()).append("<br>");
            }
        }
        infoText.append("</html>");
        orderInfoLabel = new JLabel(infoText.toString());
        orderInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        orderInfoLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(orderInfoLabel, gbc);

        // Status Label
        statusLabel = new JLabel("Statut:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        add(statusLabel, gbc);

        // Status ComboBox
        statusComboBox = new JComboBox<>(OrderStatus.values());
        // Set initial status from the Commande object, converting string to enum
        statusComboBox.setSelectedItem(OrderStatus.fromString(order.getStatut())); // Assuming Commande has getStatut()
        statusComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        statusComboBox.setBackground(new Color(240, 230, 210));
        statusComboBox.setForeground(new Color(50, 50, 50));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        statusComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                OrderStatus selectedStatus = (OrderStatus) statusComboBox.getSelectedItem();
                String statusString = selectedStatus.toString(); // Get the string representation

                // Update status in the database using the new DAO method
                boolean success = commandeDAO.updateCommandeStatus(order.getIdCommande(), statusString);
                if (success) {
                    order.setStatut(statusString); // Update the Commande object's status only if DB update successful
                    System.out.println("Order #" + order.getIdCommande() + " status updated to: " + selectedStatus + " in DB.");
                } else {
                    System.err.println("Failed to update status for Order #" + order.getIdCommande() + " in DB.");
                    JOptionPane.showMessageDialog(parentInterface, "Échec de la mise à jour du statut de la commande dans la base de données.", "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                    // Revert to the previous status if DB update fails
                    statusComboBox.setSelectedItem(OrderStatus.fromString(order.getStatut())); // Revert using the status still in the Commande object
                }
            }
        });
        add(statusComboBox, gbc);

        // Cancel Button
        cancelButton = new JButton("Annuler la commande");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setBackground(Color.RED);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(parentInterface,
                        "Êtes-vous sûr de vouloir annuler la commande #" + order.getIdCommande() + "?",
                        "Confirmer l'annulation",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    String cancelledStatusString = OrderStatus.CANCELLED.toString();
                    // Update status to cancelled in the database
                    boolean success = commandeDAO.updateCommandeStatus(order.getIdCommande(), cancelledStatusString);
                    if (success) {
                        order.setStatut(cancelledStatusString); // Update Commande object if DB update successful
                        statusComboBox.setSelectedItem(OrderStatus.CANCELLED); // Update the combo box
                        System.out.println("Order #" + order.getIdCommande() + " has been cancelled in DB.");
                        // Optionally, remove the order from the display or grey it out
                        // parentInterface.removeOrderPanel(OrderPanel.this); // Example of removing the panel
                    } else {
                        System.err.println("Failed to cancel order #" + order.getIdCommande() + " in DB.");
                        JOptionPane.showMessageDialog(parentInterface, "Échec de l'annulation de la commande dans la base de données.", "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                        // Revert status in the Commande object and ComboBox if DB update fails
                        // A more robust solution would re-fetch the status from the DB here
                        // For simplicity, we'll just rely on the status still in the 'order' object if the update failed.
                        statusComboBox.setSelectedItem(OrderStatus.fromString(order.getStatut()));
                    }
                }
            }
        });
        add(cancelButton, gbc);

        // Add a vertical glue to push components to the top
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), gbc);
    }

    // Method to get the order associated with this panel
    public Commande getOrder() {
        return this.order;
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
    private CommandeDAO commandeDAO; // DAO to fetch orders

    // Constructor for the ChefOrderInterface class
    public ChefOrderInterface() { // Changed constructor name
        // Initialize the DAO
        commandeDAO = new CommandeDAO();

        // Set up the main window properties
        setTitle("Interface Cuisinier - Commandes"); // Changed title
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
            // Call loadOrdersFromDatabase even if icon loading failed
            loadOrdersFromDatabase();
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

        // Load orders from the database on startup
        loadOrdersFromDatabase();
    }

    // Method to load orders from the database and display them
    private void loadOrdersFromDatabase() {
        ordersContainerPanel.removeAll(); // Clear existing order panels

        // Fetch orders from the database using the new method that includes items
        List<Commande> orders = commandeDAO.getAllCommandesWithItems(); // Use the new DAO method

        if (orders != null && !orders.isEmpty()) {
            for (Commande order : orders) {
                // Create and add an OrderPanel for each fetched order
                OrderPanel orderPanel = new OrderPanel(order, this);
                ordersContainerPanel.add(orderPanel);
                ordersContainerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Add vertical spacing between orders
            }
        } else {
            JLabel noOrdersLabel = new JLabel("Aucune commande en attente pour le moment.");
            noOrdersLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noOrdersLabel.setForeground(COLOR_TEXT_DARK);
            ordersContainerPanel.add(noOrdersLabel);
        }

        ordersContainerPanel.revalidate(); // Re-layout the container
        ordersContainerPanel.repaint(); // Repaint the container
    }


    // Method to remove an order panel (if needed, e.g., after completion/cancellation)
    // This method is called from OrderPanel's action listeners
    public void removeOrderPanel(OrderPanel orderPanel) {
        ordersContainerPanel.remove(orderPanel);
        ordersContainerPanel.revalidate();
        ordersContainerPanel.repaint();
        // Note: This only removes the panel from the display.
        // The actual order status update is handled within OrderPanel's action listeners
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
            // Ensure BackgroundPanel is available or handle its absence
            // Example check (optional):
            try {
                Class.forName("gui.BackgroundPanel");
            } catch (ClassNotFoundException e) {
                System.err.println("BackgroundPanel class not found. Ensure it's in the gui package.");
                // Handle this case, perhaps by using a plain JPanel instead of BackgroundPanel
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
