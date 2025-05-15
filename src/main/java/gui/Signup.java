package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

import controller.UtilisateurController;
import gui.Login;

public class Signup extends JFrame {

    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    public Signup() {
        setTitle("Sign Up");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");

        JPanel signupPanel = new JPanel();
        signupPanel.setBackground(COLOR_PANEL_BACKGROUND);
        signupPanel.setLayout(new GridBagLayout());
        signupPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel welcomeLabel = new JLabel("S'inscrire");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 25, 10);
        signupPanel.add(welcomeLabel, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        JLabel userLabel = new JLabel("Nom d'utilisateur");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        signupPanel.add(userLabel, gbc);

        JTextField userTextField = new JTextField(25);
        userTextField.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        userTextField.setForeground(COLOR_TEXT_DARK);
        userTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        Border textFieldBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
        userTextField.setBorder(textFieldBorder);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signupPanel.add(userTextField, gbc);

        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        signupPanel.add(passLabel, gbc);

        JPasswordField passTextField = new JPasswordField(25);
        passTextField.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        passTextField.setForeground(COLOR_TEXT_DARK);
        passTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        passTextField.setBorder(textFieldBorder);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signupPanel.add(passTextField, gbc);

        JLabel roleLabel = new JLabel("Rôle");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roleLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        signupPanel.add(roleLabel, gbc);

        String[] roles = {"client", "cuisinier", "serveuse"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 16));
        roleComboBox.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        roleComboBox.setForeground(COLOR_TEXT_DARK);
        ((JLabel) roleComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signupPanel.add(roleComboBox, gbc);

        JButton signupButton = new JButton("S'inscrire");
        signupButton.setFont(new Font("Arial", Font.BOLD, 16));
        signupButton.setBackground(COLOR_BUTTON_BACKGROUND);
        signupButton.setForeground(COLOR_BUTTON_TEXT);
        signupButton.setFocusPainted(false);
        signupButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        signupButton.setOpaque(true);
        signupButton.setBorderPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 10, 10, 10);
        signupPanel.add(signupButton, gbc);

        JLabel returnToLoginLabel = new JLabel("Déjà un compte ? Connectez-vous");
        returnToLoginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        returnToLoginLabel.setForeground(Color.blue);
        returnToLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        returnToLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Signup.this.dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new Login().setVisible(true);
                    }
                });
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                returnToLoginLabel.setText("<html><u>Déjà un compte ? Connectez-vous</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                returnToLoginLabel.setText("Déjà un compte ? Connectez-vous");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        signupPanel.add(returnToLoginLabel, gbc);

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainPanel.add(signupPanel, mainGbc);

        add(mainPanel);

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userTextField.getText();
                String password = new String(passTextField.getPassword());
                String role = (String) roleComboBox.getSelectedItem();

                UtilisateurController controller = new UtilisateurController();
                boolean success = controller.inscrireUtilisateur(username, password, role);
                if (success) {
                    JOptionPane.showMessageDialog(Signup.this, "Inscription réussie !");
                    Signup.this.dispose();
                    SwingUtilities.invokeLater(() -> new Login().setVisible(true));
                } else {
                    JOptionPane.showMessageDialog(Signup.this, "Erreur lors de l'inscription.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Signup signup = new Signup();
            signup.setVisible(true);
        });
    }
}
