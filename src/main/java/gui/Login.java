package gui; // Declare the package

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;




import controller.UtilisateurController;
import model.Utilisateur;




public class Login extends JFrame {


    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();


    public Login() {

        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");


        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(COLOR_PANEL_BACKGROUND);
        loginPanel.setLayout(new GridBagLayout());

        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Bienvenue");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.insets = new Insets(10, 10, 25, 10);
        loginPanel.add(welcomeLabel, gbc);


        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        // Username Label
        JLabel userLabel = new JLabel("Nom d'utilisateur");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        userLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(userLabel, gbc);

        // Username Text Field
        JTextField userTextField = new JTextField(25);
        userTextField.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        userTextField.setForeground(COLOR_TEXT_DARK);
        userTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        // Create a border for the text field
        Border textFieldBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        );
        userTextField.setBorder(textFieldBorder);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(userTextField, gbc);


        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        passLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(passLabel, gbc);


        JPasswordField passTextField = new JPasswordField(25);
        passTextField.setBackground(COLOR_INPUT_FIELD_BACKGROUND);
        passTextField.setForeground(COLOR_TEXT_DARK);
        passTextField.setFont(new Font("Arial", Font.PLAIN, 16));
        passTextField.setBorder(textFieldBorder);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        loginPanel.add(passTextField, gbc);

        // Login Button
        JButton loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(COLOR_BUTTON_BACKGROUND);
        loginButton.setForeground(COLOR_BUTTON_TEXT);
        loginButton.setFocusPainted(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30)); // Add padding
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(25, 10, 10, 10);
        loginPanel.add(loginButton, gbc);


        JLabel goToSignupLabel = new JLabel("Vous n'avez pas de compte ? Inscrivez-vous");
        goToSignupLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        goToSignupLabel.setForeground(Color.blue);
        goToSignupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));


        goToSignupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                System.out.println("Go to Signup link clicked!");

                Login.this.dispose();


                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new Signup().setVisible(true);
                    }
                });
            }

            @Override
            public void mouseEntered(MouseEvent e) {

                goToSignupLabel.setText("<html><u>Vous n'avez pas de compte ? Inscrivez-vous</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {

                goToSignupLabel.setText("Vous n'avez pas de compte ? Inscrivez-vous");
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        loginPanel.add(goToSignupLabel, gbc);



        GridBagConstraints mainGbc = new GridBagConstraints();
        mainPanel.add(loginPanel, mainGbc);


        add(mainPanel);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userTextField.getText();
                String password = new String(passTextField.getPassword());


                UtilisateurController controller = new UtilisateurController();

                Utilisateur authenticatedUser = controller.authenticateAndGetUser(username, password);


                if (authenticatedUser != null) {
                    String role = authenticatedUser.getRole();
                    int userId = authenticatedUser.getIdUtilisateur();


                    dispose();


                    if ("client".equalsIgnoreCase(role)) {

                        new ClientMenuInterface(userId).setVisible(true);
                    } else if ("cuisinier".equalsIgnoreCase(role)) {

                        new CuisinierInterface().setVisible(true);
                    } else if ("serveuse".equalsIgnoreCase(role)) {

                        new ServeuseInterface(userId).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(Login.this, "Rôle utilisateur inconnu !");
                    }

                } else {
                    JOptionPane.showMessageDialog(Login.this,
                            "Nom d'utilisateur ou mot de passe incorrect.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
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

        }


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}
