package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dao.CommandeDAO;
import model.Commande;

class ServeuseOrderPanel extends JPanel {
    private JLabel orderInfoLabel;
    private JLabel statusLabel;
    private Commande order;

    public ServeuseOrderPanel(Commande order) {
        this.order = order;

        setLayout(new GridBagLayout());
        setBackground(new Color(250, 246, 233));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setPreferredSize(new Dimension(350, 100));
        setMaximumSize(new Dimension(500, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        StringBuilder infoText = new StringBuilder("<html><b>Commande #" + order.getIdCommande() + "</b><br>");
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
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(orderInfoLabel, gbc);

        OrderStatus currentStatus = OrderStatus.fromString(order.getStatut());
        statusLabel = new JLabel("Statut: " + currentStatus.toString());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(getStatusColor(currentStatus));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.EAST;
        add(statusLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL;
        add(Box.createVerticalGlue(), gbc);
    }

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

    public void updateStatusDisplay() {
        OrderStatus currentStatus = OrderStatus.fromString(order.getStatut());
        statusLabel.setText("Statut: " + currentStatus.toString());
        statusLabel.setForeground(getStatusColor(currentStatus));
        revalidate();
        repaint();
    }

    public Commande getOrder() {
        return order;
    }
}

class OrderListPanel extends JPanel {
    private JPanel listContainer;
    private JScrollPane scrollPane;

    public OrderListPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);

        scrollPane = new JScrollPane(listContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void displayOrders(List<Commande> orders) {
        listContainer.removeAll();
        if (orders != null) {
            for (Commande order : orders) {
                listContainer.add(new ServeuseOrderPanel(order));
                listContainer.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        listContainer.revalidate();
        listContainer.repaint();
    }
}

class InvoicePanel extends JPanel {
    private JTextArea invoiceArea;
    private JButton generateButton;
    private JComboBox<Commande> finishedOrdersComboBox;
    private List<Commande> allOrders;

    public InvoicePanel(List<Commande> allOrders) {
        this.allOrders = allOrders;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setOpaque(false);

        controlPanel.add(new JLabel("Sélectionner une commande terminée:"));

        finishedOrdersComboBox = new JComboBox<>();
        finishedOrdersComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        controlPanel.add(finishedOrdersComboBox);

        generateButton = new JButton("Générer Facture");
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setBackground(new Color(60, 179, 113));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        generateButton.setOpaque(true);
        generateButton.setBorderPainted(false);
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Commande selectedOrder = (Commande) finishedOrdersComboBox.getSelectedItem();
                if (selectedOrder != null) {
                    generateInvoice(selectedOrder);
                } else {
                    invoiceArea.setText("Aucune commande terminée sélectionnée.");
                }
            }
        });
        controlPanel.add(generateButton);

        add(controlPanel, BorderLayout.NORTH);

        invoiceArea = new JTextArea();
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        invoiceArea.setEditable(false);
        invoiceArea.setBackground(new Color(255, 255, 240));
        invoiceArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane invoiceScrollPane = new JScrollPane(invoiceArea);
        add(invoiceScrollPane, BorderLayout.CENTER);

        updateFinishedOrdersComboBox();
    }

    public void updateFinishedOrdersComboBox() {
        finishedOrdersComboBox.removeAllItems();
        if (allOrders != null) {
            List<Commande> finishedOrders = allOrders.stream()
                    .filter(order -> "terminee".equalsIgnoreCase(order.getStatut()))
                    .collect(Collectors.toList());
            for (Commande order : finishedOrders) {
                finishedOrdersComboBox.addItem(order);
            }
        }
    }

    private void generateInvoice(Commande order) {
        StringBuilder invoiceText = new StringBuilder();
        invoiceText.append("----------------------------------------\n");
        invoiceText.append("           FACTURE - COMMANDE #").append(order.getIdCommande()).append("\n");
        invoiceText.append("----------------------------------------\n");
        invoiceText.append("Date: ").append(order.getHorodatage()).append("\n");
        invoiceText.append("----------------------------------------\n");
        invoiceText.append("Articles:\n");
        double total = 0;
        if (order.getItems() != null) {
            for (CartItem item : order.getItems()) {
                double itemPrice = item.getPrice();
                invoiceText.append(String.format("- %-25s x %d %.2f €\n", item.getName(), item.getQuantity(), itemPrice * item.getQuantity()));
                total += itemPrice * item.getQuantity();
            }
        }
        invoiceText.append("----------------------------------------\n");
        invoiceText.append(String.format("TOTAL: %30.2f €\n", total));
        invoiceText.append("----------------------------------------\n");

        invoiceArea.setText(invoiceText.toString());
    }
}

public class ServeuseOrderInterface extends JFrame {
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    private JPanel contentAreaPanel;
    private OrderListPanel receivedOrdersPanel;
    private OrderListPanel processingOrdersPanel;
    private InvoicePanel invoicePanel;

    private List<Commande> allOrders;
    private CommandeDAO commandeDAO;

    public ServeuseOrderInterface(int serveuseId) {
        commandeDAO = new CommandeDAO();
        allOrders = new ArrayList<>();
        setTitle("Interface Serveuse...");
    }
}
