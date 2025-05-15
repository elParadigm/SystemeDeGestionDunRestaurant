package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import dao.CommandeDAO;
import model.Commande;
import gui.CartItem;
import gui.CuisinierInterface;

class OrderPanel extends JPanel {
    private JLabel orderInfoLabel;
    private JLabel statusLabel;
    private JComboBox<OrderStatus> statusComboBox;
    private JButton cancelButton;
    private Commande order;
    private ChefOrderInterface parentInterface;
    private CommandeDAO commandeDAO;

    public OrderPanel(Commande order, ChefOrderInterface parentInterface) {
        this.order = order;
        this.parentInterface = parentInterface;
        this.commandeDAO = new CommandeDAO();

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

        StringBuilder infoText = new StringBuilder("<html><b>Commande #" + order.getIdCommande() + "</b> (Table: " + order.getTableNumber() + ")<br>");
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

        statusLabel = new JLabel("Statut:");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        add(statusLabel, gbc);

        statusComboBox = new JComboBox<>(OrderStatus.values());
        statusComboBox.setSelectedItem(OrderStatus.fromString(order.getStatut()));
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
                String statusString = selectedStatus.toString();
                boolean success = commandeDAO.updateCommandeStatus(order.getIdCommande(), statusString);
                if (success) {
                    order.setStatut(statusString);
                    System.out.println("Order #" + order.getIdCommande() + " status updated to: " + selectedStatus + " in DB.");
                } else {
                    System.err.println("Failed to update status for Order #" + order.getIdCommande() + " in DB.");
                    JOptionPane.showMessageDialog(parentInterface, "Échec de la mise à jour du statut de la commande dans la base de données.", "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                    statusComboBox.setSelectedItem(OrderStatus.fromString(order.getStatut()));
                }
            }
        });
        add(statusComboBox, gbc);

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
                    boolean success = commandeDAO.updateCommandeStatus(order.getIdCommande(), cancelledStatusString);
                    if (success) {
                        order.setStatut(cancelledStatusString);
                        statusComboBox.setSelectedItem(OrderStatus.CANCELLED);
                        System.out.println("Order #" + order.getIdCommande() + " has been cancelled in DB.");
                    } else {
                        System.err.println("Failed to cancel order #" + order.getIdCommande() + " in DB.");
                        JOptionPane.showMessageDialog(parentInterface, "Échec de l'annulation de la commande dans la base de données.", "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                        statusComboBox.setSelectedItem(OrderStatus.fromString(order.getStatut()));
                    }
                }
            }
        });
        add(cancelButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), gbc);
    }

    public Commande getOrder() {
        return this.order;
    }
}

public class ChefOrderInterface extends JFrame {
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    private JPanel ordersContainerPanel;
    private CommandeDAO commandeDAO;

    public ChefOrderInterface() {
        commandeDAO = new CommandeDAO();
        setTitle("Interface Cuisinier - Commandes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(10, 10, 10, 10);
        mainGbc.anchor = GridBagConstraints.CENTER;

        JLabel backIconLabel = new JLabel();
        backIconLabel.setOpaque(false);
        ImageIcon backIcon = null;
        try {
            Image img = ImageIO.read(new File("arrow.png"));
            backIcon = new ImageIcon(img);
        } catch (Exception e) {
            System.err.println("Error loading back arrow icon: " + e.getMessage());
            JLabel fallbackBackLabel = new JLabel("←");
            fallbackBackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            fallbackBackLabel.setForeground(COLOR_TEXT_DARK);
            fallbackBackLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            fallbackBackLabel.setOpaque(false);
            fallbackBackLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Back button (fallback) clicked!");
                    ChefOrderInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new CuisinierInterface().setVisible(true);
                        }
                    });
                }
            });
            GridBagConstraints fallbackGbc = new GridBagConstraints();
            fallbackGbc.gridx = 0;
            fallbackGbc.gridy = 0;
            fallbackGbc.anchor = GridBagConstraints.NORTHWEST;
            fallbackGbc.insets = new Insets(10, 10, 0, 0);
            mainPanel.add(fallbackBackLabel, fallbackGbc);
            add(mainPanel);
            loadOrdersFromDatabase();
            return;
        }

        if (backIcon != null) {
            backIconLabel.setIcon(backIcon);
            backIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            backIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            backIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Back icon clicked!");
                    ChefOrderInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new CuisinierInterface().setVisible(true);
                        }
                    });
                }
            });
            GridBagConstraints iconGbc = new GridBagConstraints();
            iconGbc.gridx = 0;
            iconGbc.gridy = 0;
            iconGbc.anchor = GridBagConstraints.NORTHWEST;
            iconGbc.insets = new Insets(10, 10, 0, 0);
            mainPanel.add(backIconLabel, iconGbc);
        }

        add(mainPanel);

        JLabel titleLabel = new JLabel("Liste des Commandes");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.gridwidth = 2;
        mainGbc.anchor = GridBagConstraints.NORTH;
        mainGbc.insets = new Insets(20, 10, 20, 10);
        mainPanel.add(titleLabel, mainGbc);

        ordersContainerPanel = new JPanel();
        ordersContainerPanel.setBackground(new Color(250, 246, 233, 150));
        ordersContainerPanel.setLayout(new BoxLayout(ordersContainerPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(ordersContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainGbc.gridx = 0;
        mainGbc.gridy = 1;
        mainGbc.gridwidth = 2;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(10, 50, 10, 50);
        mainPanel.add(scrollPane, mainGbc);

        add(mainPanel);
        loadOrdersFromDatabase();
    }

    private void loadOrdersFromDatabase() {
        ordersContainerPanel.removeAll();
        List<Commande> orders = commandeDAO.getAllCommandesWithItems();

        if (orders != null && !orders.isEmpty()) {
            for (Commande order : orders) {
                OrderPanel orderPanel = new OrderPanel(order, this);
                ordersContainerPanel.add(orderPanel);
                ordersContainerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } else {
            JLabel noOrdersLabel = new JLabel("Aucune commande en attente pour le moment.");
            noOrdersLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noOrdersLabel.setForeground(COLOR_TEXT_DARK);
            ordersContainerPanel.add(noOrdersLabel);
        }

        ordersContainerPanel.revalidate();
        ordersContainerPanel.repaint();
    }

    public void removeOrderPanel(OrderPanel orderPanel) {
        ordersContainerPanel.remove(orderPanel);
        ordersContainerPanel.revalidate();
        ordersContainerPanel.repaint();
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
            try {
                Class.forName("gui.BackgroundPanel");
            } catch (ClassNotFoundException e) {
                System.err.println("BackgroundPanel class not found. Ensure it's in the gui package.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ChefOrderInterface().setVisible(true);
            }
        });
    }
}