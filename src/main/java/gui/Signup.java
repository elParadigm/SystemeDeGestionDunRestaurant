package gui; // Declare the package

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;

import controller.UtilisateurController;
import gui.Login;



public class Signup extends JFrame { // Changed class name to Signup

    // Define the colors used in the GUI (copied from Login.java)
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6"); // Light beige/yellow background (Note: This color won't be fully visible with a background image)
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9"); // Slightly darker creamy beige for panels
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD"); // Very light white for input fields
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878"); // Muted green for buttons
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50); // Dark grey text
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE; // White text for buttons
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker(); // Darker shade of panel background for border

    // Constructor for the Signup class
    public Signup() { // Changed constructor name
        // Set up the main window properties
        setTitle("Sign Up"); // Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setSize(1280, 720); // Window size
        setLocationRelativeTo(null); // Center the window on the screen
        // getContentPane().setBackground(COLOR_BACKGROUND); // No need to set background here, handled by BackgroundPanel

        // Create the main panel using the custom BackgroundPanel with an image
        // Replace "path/to/your/background_image.jpg" with the actual path to your image file
        // Using a placeholder image path for now. Update this to your actual image path.
        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");
        // mainPanel.setBackground(COLOR_BACKGROUND); // Background color is handled by the image

        // Create the sign-up panel (standard JPanel)
        JPanel signupPanel = new JPanel(); // Changed panel name
        signupPanel.setBackground(COLOR_PANEL_BACKGROUND); // Set background color
        signupPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for internal layout
        // Set a compound border: a line border and an empty border for padding
        signupPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1), // Outer line border
                BorderFactory.createEmptyBorder(40, 60, 40, 60) // Inner padding
        ));

        // GridBagConstraints for controlling component placement in GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.anchor = GridBagConstraints.CENTER; // Center components by default

        // Welcome Label (changed text to reflect sign-up)
        JLabel welcomeLabel = new JLabel("S'inscrire");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Set font and size
        welcomeLabel.setForeground(COLOR_TEXT_DARK); // Set text color
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.insets = new Insets(10, 10, 25, 10); // Adjust padding below the label
        signupPanel.add(welcomeLabel, gbc); // Added to signupPanel

        // Reset gridwidth and insets for subsequent components
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridwidth = 1;

        // Username Label
        JLabel userLabel = new JLabel("Nom d'utilisateur");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        userLabel.setForeground(COLOR_TEXT_DARK); // Set text color
        gbc.gridx = 0; // Column 0
        gbc.gridy = 1; // Row 1
        gbc.anchor = GridBagConstraints.WEST; // Align to the west (left)
        signupPanel.add(userLabel, gbc); // Added to signupPanel

        // Username Text Field
        JTextField userTextField = new JTextField(25); // Create a text field with 25 columns width hint
        userTextField.setBackground(COLOR_INPUT_FIELD_BACKGROUND); // Set background color
        userTextField.setForeground(COLOR_TEXT_DARK); // Set text color
        userTextField.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        // Create a border for the text field
        Border textFieldBorder = BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1), // Outer line border
                BorderFactory.createEmptyBorder(8, 10, 8, 10) // Inner padding
        );
        userTextField.setBorder(textFieldBorder); // Set the border
        gbc.gridx = 0; // Column 0
        gbc.gridy = 2; // Row 2
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make the component fill horizontally
        signupPanel.add(userTextField, gbc); // Added to signupPanel

        // Password Label
        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        passLabel.setForeground(COLOR_TEXT_DARK); // Set text color
        gbc.gridx = 0; // Column 0
        gbc.gridy = 3; // Row 3
        gbc.gridwidth = 1; // Reset gridwidth to 1
        gbc.fill = GridBagConstraints.NONE; // Do not fill
        gbc.anchor = GridBagConstraints.WEST; // Align to the west (left)
        signupPanel.add(passLabel, gbc); // Added to signupPanel

        // Password Text Field
        JPasswordField passTextField = new JPasswordField(25); // Create a password field
        passTextField.setBackground(COLOR_INPUT_FIELD_BACKGROUND); // Set background color
        passTextField.setForeground(COLOR_TEXT_DARK); // Set text color
        passTextField.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        passTextField.setBorder(textFieldBorder); // Set the same border as the username field
        gbc.gridx = 0; // Column 0
        gbc.gridy = 4; // Row 4
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make the component fill horizontally
        signupPanel.add(passTextField, gbc); // Added to signupPanel

        // Role Label
        JLabel roleLabel = new JLabel("Rôle");
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        roleLabel.setForeground(COLOR_TEXT_DARK); // Set text color
        gbc.gridx = 0; // Column 0
        gbc.gridy = 5; // Row 5
        gbc.gridwidth = 1; // Reset gridwidth to 1
        gbc.fill = GridBagConstraints.NONE; // Do not fill
        gbc.anchor = GridBagConstraints.WEST; // Align to the west (left)
        signupPanel.add(roleLabel, gbc); // Added to signupPanel

        // Role Combo Box
        String[] roles = {"client", "cuisinier", "serveuse"}; // Array of roles
        JComboBox<String> roleComboBox = new JComboBox<>(roles); // Create the combo box
        roleComboBox.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        roleComboBox.setBackground(COLOR_INPUT_FIELD_BACKGROUND); // Set background color
        roleComboBox.setForeground(COLOR_TEXT_DARK); // Set text color
        ((JLabel)roleComboBox.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER); // Center text in combo box
        gbc.gridx = 0; // Column 0
        gbc.gridy = 6; // Row 6
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make the component fill horizontally
        signupPanel.add(roleComboBox, gbc); // Added to signupPanel


        // Sign Up Button (changed text and action)
        JButton signupButton = new JButton("S'inscrire"); // Create the sign-up button
        signupButton.setFont(new Font("Arial", Font.BOLD, 16)); // Set font and size
        signupButton.setBackground(COLOR_BUTTON_BACKGROUND); // Set background color
        signupButton.setForeground(COLOR_BUTTON_TEXT); // Set text color
        signupButton.setFocusPainted(false); // Remove focus border
        signupButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30)); // Add padding
        signupButton.setOpaque(true); // Make the background visible
        signupButton.setBorderPainted(false); // Do not paint the border
        gbc.gridx = 0; // Column 0
        gbc.gridy = 7; // Row 7 (adjusted row)
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.fill = GridBagConstraints.NONE; // Do not fill
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        gbc.insets = new Insets(25, 10, 10, 10); // Adjust padding above the button
        signupPanel.add(signupButton, gbc); // Added to signupPanel

        // Add a label that acts as a link to return to the login page
        JLabel returnToLoginLabel = new JLabel("Déjà un compte ? Connectez-vous");
        returnToLoginLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font and size
        returnToLoginLabel.setForeground(Color.blue); // Set text color to blue
        returnToLoginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover

        // Add a MouseListener to the return label
        returnToLoginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // This method is called when the mouse is clicked on the label
                System.out.println("Return to Login link clicked!");
                // Close the current Signup window
                Signup.this.dispose();

                // Open the Login window on the Event Dispatch Thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new Login().setVisible(true);
                    }
                });
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Change the label's appearance on hover (e.g., underline)
                returnToLoginLabel.setText("<html><u>Déjà un compte ? Connectez-vous</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert the appearance when the mouse leaves
                returnToLoginLabel.setText("Déjà un compte ? Connectez-vous");
            }
        });

        gbc.gridx = 0; // Column 0
        gbc.gridy = 8; // Row 8 (below the signup button)
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.insets = new Insets(20, 10, 10, 10); // Adjust padding above the label
        signupPanel.add(returnToLoginLabel, gbc);


        // Add the sign-up panel to the main panel, centered
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainPanel.add(signupPanel, mainGbc); // Added signupPanel to mainPanel

        // Add the main panel to the JFrame
        add(mainPanel);

        // Add an ActionListener to the sign-up button
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Récupérer les champs
                String username = userTextField.getText();
                String password = new String(passTextField.getPassword());
                String selectedRole = (String) roleComboBox.getSelectedItem();

                // Vérifier que tous les champs sont remplis
                if (username.isEmpty() || password.isEmpty() || selectedRole == null) {
                    JOptionPane.showMessageDialog(Signup.this,
                            "Veuillez remplir tous les champs.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Appeler le contrôleur
                UtilisateurController controller = new UtilisateurController();
                boolean success = controller.inscrireUtilisateur(username, password, selectedRole);

                // Afficher un message selon le résultat
                if (success) {
                    JOptionPane.showMessageDialog(Signup.this,
                            "Inscription réussie, bienvenue " + username + " !",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Optionnel : fermer cette fenêtre et retourner à Login
                    Signup.this.dispose();
                    new Login().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(Signup.this,
                            "Échec de l'inscription. Vérifiez vos informations.",
                            "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    // The main method is typically in your main application file,
    // but included here for standalone testing purposes.
    // In your main project, you will call this class from your main method.
    public static void main(String[] args) {
        // Set the look and feel to Nimbus if available for a more modern look
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to the default look and feel
            // e.printStackTrace(); // Optional: print stack trace for debugging
        }

        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create an instance of the Signup GUI and make it visible
                new Signup().setVisible(true);
            }
        });
    }
}
