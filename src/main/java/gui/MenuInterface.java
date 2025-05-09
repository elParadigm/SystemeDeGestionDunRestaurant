package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuInterface extends JFrame {

    // Couleurs personnalisées
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Fond clair
    private static final Color BUTTON_BACKGROUND = new Color(100, 179, 255); // Bleu ciel
    private static final Color BUTTON_HOVER = new Color(85, 160, 235);
    private static final Color BUTTON_TEXT = Color.WHITE;
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 18);

    public MenuInterface() {
        // Configuration de la fenêtre principale
        setTitle("Choix du Menu");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrer la fenêtre
        setLayout(new BorderLayout());

        // Panel principal avec fond coloré
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20); // Espacement entre les éléments

        // Titre
        JLabel titleLabel = new JLabel("Veuillez choisir un type de menu :", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        // Boutons pour les catégories de menus
        String[] categories = {"Pizzas", "Sandwichs", "Boissons", "Plats Principaux", "Desserts / Sucrés"};
        JButton[] categoryButtons = new JButton[categories.length];

        for (int i = 0; i < categories.length; i++) {
            gbc.gridy = i + 1;
            gbc.gridwidth = 2;
            JButton button = new JButton(categories[i]);
            styleButton(button);
            categoryButtons[i] = button;

            int index = i; // Pour l'écouteur d'événements
            button.addActionListener(e -> openCategory(index));
            mainPanel.add(button, gbc);
        }

        // Ajout du panel principal à la fenêtre
        add(mainPanel, BorderLayout.CENTER);

        // Bouton Retour en haut à gauche
        JButton backButton = new JButton("← Retour");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBackground(Color.LIGHT_GRAY);
        backButton.setForeground(Color.BLACK);
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        backButton.addActionListener(e -> {
            dispose(); // Ferme cette interface
            new CuisinierInterface().setVisible(true); // Ouvre l'interface précédente
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(backButton);
        add(topPanel, BorderLayout.NORTH);
    }

    // Appliquer le style aux boutons
    private void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setBackground(BUTTON_BACKGROUND);
        button.setForeground(BUTTON_TEXT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setOpaque(true);
        button.setBorderPainted(false);

        // Effet hover (survol)
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_BACKGROUND);
            }
        });
    }

    // Gestionnaire d'ouverture des sous-menus
    private void openCategory(int categoryIndex) {
        String categoryName = "";

        switch (categoryIndex) {
            case 0:
                categoryName = "Pizzas";
                break;
            case 1:
                categoryName = "Sandwichs";
                break;
            case 2:
                categoryName = "Boissons";
                break;
            case 3:
                categoryName = "Plats Principaux";
                break;
            case 4:
                categoryName = "Desserts / Sucrés";
                break;
            default:
                return;
        }

        // Affiche une fenêtre modale temporaire ou ouvre une autre interface
        JOptionPane.showMessageDialog(this,
                "Vous avez sélectionné le menu : " + categoryName + "\nIci vous pouvez afficher les plats associés.",
                "Menu : " + categoryName,
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Méthode principale pour tester l'interface
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuInterface().setVisible(true);
        });
    }
}