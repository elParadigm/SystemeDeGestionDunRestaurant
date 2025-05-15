package gui;

import model.Plat;
import model.Menu;
import dao.PlatDAO;
import dao.MenuDAO;
import gui.CartItem;
import model.Commande;
import dao.CommandeDAO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.util.Date;
import java.sql.SQLException;
import java.lang.ClassNotFoundException;
import gui.Login;
import model.Utilisateur;
import gui.CartItem;

class CartDialog extends JDialog {
    private JList<CartItem> cartList;
    private DefaultListModel<CartItem> cartListModel;
    private JButton removeButton;
    private JButton closeButton;
    private JButton checkoutButton;
    private JLabel totalLabel;
    private ClientMenuInterface parentInterface;

    public CartDialog(JFrame parent, ClientMenuInterface parentInterface) {
        super(parent, "Mon Panier", true);
        this.parentInterface = parentInterface;

        setSize(400, 500);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(250, 246, 233));

        JLabel cartTitleLabel = new JLabel("Mon Panier");
        cartTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cartTitleLabel.setForeground(new Color(50, 50, 50));
        cartTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cartTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(cartTitleLabel, BorderLayout.NORTH);
        add(headerPanel, BorderLayout.NORTH);

        cartListModel = new DefaultListModel<>();
        cartList = new JList<>(cartListModel);
        cartList.setFont(new Font("Arial", Font.PLAIN, 16));
        cartList.setBackground(new Color(253, 253, 253));
        cartList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(cartList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 220, 200), 1));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);

        totalLabel = new JLabel("Total: 0.00 €");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(50, 50, 50));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 25));
        buttonPanel.setBackground(new Color(250, 246, 233));

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
                if (selectedIndex != -1) {
                    CartItem selectedItem = cartListModel.getElementAt(selectedIndex);
                    parentInterface.removeItemFromCart(selectedItem);
                    updateCartDisplay();
                } else {
                    JOptionPane.showMessageDialog(CartDialog.this, "Veuillez sélectionner un article à supprimer.", "Aucun article sélectionné", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(removeButton);

        checkoutButton = new JButton("Commander");
        checkoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        checkoutButton.setBackground(new Color(60, 179, 113));
        checkoutButton.setForeground(Color.WHITE);
        checkoutButton.setFocusPainted(false);
        checkoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        checkoutButton.setOpaque(true);
        checkoutButton.setBorderPainted(false);
        checkoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cartListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(CartDialog.this, "Votre panier est vide. Veuillez ajouter des articles avant de commander.", "Panier vide", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                CommandeDAO commandeDAO = new CommandeDAO();
                int clientId = parentInterface.getLoggedInClientId();

                if (clientId != -1) {
                    boolean orderPlaced = commandeDAO.placeOrder(clientId, parentInterface.getCartItems());
                    if (orderPlaced) {
                        JOptionPane.showMessageDialog(CartDialog.this, "Commande passée avec succès!", "Commande Réussie", JOptionPane.INFORMATION_MESSAGE);
                        parentInterface.clearCart();
                        updateCartDisplay();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(CartDialog.this, "Échec de la commande. Veuillez réessayer.", "Erreur de Commande", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(CartDialog.this, "Aucun client connecté. Veuillez vous connecter pour passer une commande.", "Erreur de connexion", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        buttonPanel.add(checkoutButton);

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
                dispose();
            }
        });
        buttonPanel.add(closeButton);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        footerPanel.setOpaque(false);
        footerPanel.add(totalLabel);
        footerPanel.add(buttonPanel);
        add(footerPanel, BorderLayout.SOUTH);

        updateCartDisplay();
    }

    public void updateCartDisplay() {
        cartListModel.clear();
        double total = 0;
        for (CartItem item : parentInterface.getCartItems()) {
            cartListModel.addElement(item);
            total += item.getPrice() * item.getQuantity();
        }
        totalLabel.setText(String.format("Total: %.2f €", total));
    }
}

public class ClientMenuInterface extends JFrame {
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    private JPanel menuItemsPanel;
    private JComboBox<Menu> menuComboBox;
    private List<CartItem> shoppingCart;
    private int loggedInClientId = -1;

    public ClientMenuInterface(int clientId) {
        this.loggedInClientId = clientId;
        shoppingCart = new ArrayList<>();

        setTitle("Menu du Restaurant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.insets = new Insets(10, 10, 10, 10);

        JLabel backIconLabel = new JLabel();
        backIconLabel.setOpaque(false);

        ImageIcon backIcon = null;
        try {
            Image img = ImageIO.read(new File("arrow.png"));
            Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            backIcon = new ImageIcon(scaledImg);
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

        JLabel cartIconLabel = new JLabel();
        cartIconLabel.setOpaque(false);
        ImageIcon cartIcon = null;
        try {
            Image img = ImageIO.read(new File("shopping-cart.png"));
            Image scaledImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            cartIcon = new ImageIcon(scaledImg);
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
                CartDialog cartDialog = new CartDialog(ClientMenuInterface.this, ClientMenuInterface.this);
                cartDialog.setVisible(true);
            }
        });
        mainGbc.gridx = 2;
        mainGbc.gridy = 0;
        mainGbc.anchor = GridBagConstraints.NORTHEAST;
        mainGbc.insets = new Insets(10, 0, 0, 10);
        mainGbc.weightx = 1.0;
        mainGbc.fill = GridBagConstraints.NONE;
        mainPanel.add(cartIconLabel, mainGbc);

        JLabel titleLabel = new JLabel("Notre Menu Délicieux");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 1;
        mainGbc.gridy = 0;
        mainGbc.anchor = GridBagConstraints.NORTH;
        mainGbc.insets = new Insets(20, 10, 20, 10);
        mainGbc.weightx = 0.0;
        mainGbc.fill = GridBagConstraints.NONE;
        mainPanel.add(titleLabel, mainGbc);

        menuComboBox = new JComboBox<>();
        menuComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        menuComboBox.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        menuComboBox.setPreferredSize(new Dimension(200, 30));

        menuComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Menu) {
                    setText(((Menu) value).getNomMenu());
                }
                return this;
            }
        });

        menuComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Menu selectedMenu = (Menu) menuComboBox.getSelectedItem();
                if (selectedMenu != null && selectedMenu.getIdMenu() != -1) {
                    loadMenuItems(selectedMenu.getIdMenu());
                } else {
                    menuItemsPanel.removeAll();
                    JLabel selectMenuLabel = new JLabel("Veuillez sélectionner un menu.");
                    selectMenuLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                    selectMenuLabel.setForeground(COLOR_TEXT_DARK);
                    JPanel centeredPanel = new JPanel(new GridBagLayout());
                    centeredPanel.setOpaque(false);
                    centeredPanel.add(selectMenuLabel);
                    menuItemsPanel.setLayout(new GridBagLayout());
                    menuItemsPanel.add(centeredPanel, new GridBagConstraints());
                    menuItemsPanel.revalidate();
                    menuItemsPanel.repaint();
                }
            }
        });

        mainGbc.gridx = 1;
        mainGbc.gridy = 1;
        mainGbc.anchor = GridBagConstraints.CENTER;
        mainGbc.insets = new Insets(0, 10, 10, 10);
        mainGbc.fill = GridBagConstraints.NONE;
        mainPanel.add(menuComboBox, mainGbc);

        menuItemsPanel = new JPanel();
        menuItemsPanel.setBackground(new Color(250, 246, 233, 150));
        menuItemsPanel.setLayout(new GridBagLayout());
        JScrollPane menuScrollPane = new JScrollPane(menuItemsPanel);
        menuScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        menuScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        menuScrollPane.setBorder(BorderFactory.createEmptyBorder());
        menuScrollPane.setOpaque(false);
        menuScrollPane.getViewport().setOpaque(false);

        mainGbc.gridx = 0;
        mainGbc.gridy = 2;
        mainGbc.gridwidth = 3;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(10, 50, 10, 50);
        mainPanel.add(menuScrollPane, mainGbc);

        add(mainPanel);
        populateMenuComboBox();
    }

    public ClientMenuInterface() {
        this(-1);
    }

    private void populateMenuComboBox() {
        MenuDAO menuDAO = new MenuDAO();
        List<Menu> menus = menuDAO.getMenuList();

        menuComboBox.removeAllItems();

        if (menus != null && !menus.isEmpty()) {
            for (Menu menu : menus) {
                menuComboBox.addItem(menu);
            }
            menuComboBox.setSelectedIndex(0);
        } else {
            menuComboBox.setEnabled(false);
            menuItemsPanel.removeAll();
            JLabel noMenusLabel = new JLabel("Aucun menu n'est disponible pour le moment.");
            noMenusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noMenusLabel.setForeground(COLOR_TEXT_DARK);
            JPanel centeredPanel = new JPanel(new GridBagLayout());
            centeredPanel.setOpaque(false);
            centeredPanel.add(noMenusLabel);
            menuItemsPanel.setLayout(new GridBagLayout());
            menuItemsPanel.add(centeredPanel, new GridBagConstraints());
            menuItemsPanel.revalidate();
            menuItemsPanel.repaint();
        }
    }

    private void loadMenuItems(int menuId) {
        menuItemsPanel.removeAll();
        menuItemsPanel.setLayout(new GridLayout(0, 4, 20, 20));

        PlatDAO platDAO = new PlatDAO();
        List<Plat> plats = platDAO.getPlatsByMenuId(menuId);

        if (plats != null && !plats.isEmpty()) {
            for (Plat plat : plats) {
                menuItemsPanel.add(new MenuItemPanel(plat));
            }
        } else {
            menuItemsPanel.setLayout(new GridBagLayout());
            JLabel noItemsLabel = new JLabel("Aucun plat disponible pour ce menu.");
            noItemsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noItemsLabel.setForeground(COLOR_TEXT_DARK);
            JPanel centeredPanel = new JPanel(new GridBagLayout());
            centeredPanel.setOpaque(false);
            centeredPanel.add(noItemsLabel);
            menuItemsPanel.add(centeredPanel, new GridBagConstraints());
        }

        menuItemsPanel.revalidate();
        menuItemsPanel.repaint();
    }

    class MenuItemPanel extends JPanel {
        private JLabel imageLabel;
        private JLabel nameLabel;
        private JLabel descriptionLabel;
        private JLabel priceLabel;
        private JSpinner quantitySpinner;
        private JButton addButton;

        public MenuItemPanel(Plat plat) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBackground(new Color(250, 246, 233));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 220, 200), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            setPreferredSize(new Dimension(180, 280));
            setMaximumSize(new Dimension(180, 280));
            setMinimumSize(new Dimension(180, 280));

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
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            nameLabel = new JLabel(plat.getNom());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setForeground(COLOR_TEXT_DARK);
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            descriptionLabel = new JLabel("<html><body style='text-align:center;'>" + plat.getDescription() + "</body></html>");
            descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            descriptionLabel.setForeground(new Color(80, 80, 80));
            descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            descriptionLabel.setVerticalAlignment(SwingConstants.TOP);
            descriptionLabel.setPreferredSize(new Dimension(160, 40));
            descriptionLabel.setMaximumSize(new Dimension(160, 60));

            priceLabel = new JLabel(String.format("%.2f €", plat.getPrix()));
            priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
            priceLabel.setForeground(new Color(0, 100, 0));
            priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            SpinnerModel quantityModel = new SpinnerNumberModel(1, 1, 10, 1);
            quantitySpinner = new JSpinner(quantityModel);
            quantitySpinner.setFont(new Font("Arial", Font.PLAIN, 14));
            quantitySpinner.setAlignmentX(Component.CENTER_ALIGNMENT);
            quantitySpinner.setPreferredSize(new Dimension(60, 25));
            quantitySpinner.setMaximumSize(new Dimension(60, 25));

            addButton = new JButton("Ajouter");
            addButton.setFont(new Font("Arial", Font.BOLD, 12));
            addButton.setBackground(new Color(60, 179, 113));
            addButton.setForeground(Color.WHITE);
            addButton.setFocusPainted(false);
            addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            addButton.setOpaque(true);
            addButton.setBorderPainted(false);
            addButton.setAlignmentX(Component.CENTER_ALIGNMENT);

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int quantity = (int) quantitySpinner.getValue();
                    if (quantity > 0) {
                        addMenuItemToCart(plat, quantity);
                        JOptionPane.showMessageDialog(ClientMenuInterface.this,
                                quantity + " x " + plat.getNom() + " ajouté au panier.",
                                "Article ajouté", JOptionPane.INFORMATION_MESSAGE);
                        quantitySpinner.setValue(1);
                    } else {
                        JOptionPane.showMessageDialog(ClientMenuInterface.this,
                                "La quantité doit être un nombre positif.",
                                "Quantité invalide", JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            add(Box.createRigidArea(new Dimension(0, 5)));
            add(imageLabel);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(nameLabel);
            add(Box.createRigidArea(new Dimension(0, 3)));
            add(descriptionLabel);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(priceLabel);
            add(Box.createRigidArea(new Dimension(0, 5)));
            add(quantitySpinner);
            add(Box.createVerticalGlue());
            add(addButton);
            add(Box.createRigidArea(new Dimension(0, 5)));
        }
    }

    public void addMenuItemToCart(Plat plat, int quantity) {
        boolean found = false;
        for (CartItem item : shoppingCart) {
            if (plat.getIdPlat() == item.getPlat().getIdPlat()) {
                item.setQuantity(item.getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            shoppingCart.add(new CartItem(plat, quantity));
        }
    }

    public void removeItemFromCart(CartItem itemToRemove) {
        shoppingCart.remove(itemToRemove);
    }

    public List<CartItem> getCartItems() {
        return shoppingCart;
    }

    public void clearCart() {
        shoppingCart.clear();
        System.out.println("Cart cleared.");
    }

    public int getLoggedInClientId() {
        return loggedInClientId;
    }

    public static void main(String[] args) {
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
                int testClientId = 1;
                new ClientMenuInterface(testClientId).setVisible(true);
            }
        });
    }
}