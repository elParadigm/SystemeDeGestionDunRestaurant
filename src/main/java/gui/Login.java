package gui; // Declare the package

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
// Assuming BackgroundPanel is in the same 'gui' package or accessible
// import gui.BackgroundPanel; // You might need this import depending on where BackgroundPanel is defined

// Import the Signup class from the same package
import controller.UtilisateurController;
import gui.Signup;


public class Login extends JFrame {

    // Define the colors used in the GUI
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6"); // Light beige/yellow background (Note: This color won't be fully visible with a background image)
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9"); // Slightly darker creamy beige for panels
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD"); // Very light white for input fields
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878"); // Muted green for buttons
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50); // Dark grey text
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE; // White text for buttons
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker(); // Darker shade of panel background for border

    // Constructor for the Login class
    public Login() { // Changed constructor name
        // Set up the main window properties
        setTitle("Login"); // Window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setSize(1280, 720); // Window size
        setLocationRelativeTo(null); // Center the window on the screen
        // getContentPane().setBackground(COLOR_BACKGROUND); // No need to set background here, handled by BackgroundPanel

        // Create the main panel using the custom BackgroundPanel with an image
        // Assuming BackgroundPanel is defined elsewhere and accessible.
        // Replace "path/to/your/background_image.jpg" with the actual path to your image file
        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");
        // mainPanel.setBackground(COLOR_BACKGROUND); // Background color is handled by the image

        // Create the login panel (standard JPanel)
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(COLOR_PANEL_BACKGROUND); // Set background color
        loginPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for internal layout
        // Set a compound border: a line border and an empty border for padding
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1), // Outer line border
                BorderFactory.createEmptyBorder(40, 60, 40, 60) // Inner padding
        ));

        // GridBagConstraints for controlling component placement in GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.anchor = GridBagConstraints.CENTER; // Center components by default

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Bienvenue");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36)); // Set font and size
        welcomeLabel.setForeground(COLOR_TEXT_DARK); // Set text color
        gbc.gridx = 0; // Column 0
        gbc.gridy = 0; // Row 0
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.insets = new Insets(10, 10, 25, 10); // Adjust padding below the label
        loginPanel.add(welcomeLabel, gbc);

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
        loginPanel.add(userLabel, gbc);

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
        loginPanel.add(userTextField, gbc);

        // Password Label
        JLabel passLabel = new JLabel("Mot de passe");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Set font and size
        passLabel.setForeground(COLOR_TEXT_DARK); // Set text color
        gbc.gridx = 0; // Column 0
        gbc.gridy = 3; // Row 3
        gbc.gridwidth = 1; // Reset gridwidth to 1
        gbc.fill = GridBagConstraints.NONE; // Do not fill
        gbc.anchor = GridBagConstraints.WEST; // Align to the west (left)
        loginPanel.add(passLabel, gbc);

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
        loginPanel.add(passTextField, gbc);

        // Login Button
        JButton loginButton = new JButton("Se connecter"); // Create the login button
        loginButton.setFont(new Font("Arial", Font.BOLD, 16)); // Set font and size
        loginButton.setBackground(COLOR_BUTTON_BACKGROUND); // Set background color
        loginButton.setForeground(COLOR_BUTTON_TEXT); // Set text color
        loginButton.setFocusPainted(false); // Remove focus border
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30)); // Add padding
        loginButton.setOpaque(true); // Make the background visible
        loginButton.setBorderPainted(false); // Do not paint the border
        gbc.gridx = 0; // Column 0
        gbc.gridy = 5; // Row 5
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.fill = GridBagConstraints.NONE; // Do not fill
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        gbc.insets = new Insets(25, 10, 10, 10); // Adjust padding above the button
        loginPanel.add(loginButton, gbc);

        // Add a label that acts as a link to go to the signup page
        JLabel goToSignupLabel = new JLabel("Vous n'avez pas de compte ? Inscrivez-vous");
        goToSignupLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set font and size
        goToSignupLabel.setForeground(Color.blue); // Set text color to blue
        goToSignupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Change cursor on hover

        // Add a MouseListener to the signup label
        goToSignupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // This method is called when the mouse is clicked on the label
                System.out.println("Go to Signup link clicked!");
                // Close the current Login window
                Login.this.dispose();

                // Open the Signup window on the Event Dispatch Thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new Signup().setVisible(true);
                    }
                });
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // Change the label's appearance on hover (e.g., underline)
                goToSignupLabel.setText("<html><u>Vous n'avez pas de compte ? Inscrivez-vous</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Revert the appearance when the mouse leaves
                goToSignupLabel.setText("Vous n'avez pas de compte ? Inscrivez-vous");
            }
        });

        gbc.gridx = 0; // Column 0
        gbc.gridy = 6; // Row 6 (below the login button)
        gbc.gridwidth = 2; // Span across 2 columns
        gbc.insets = new Insets(20, 10, 10, 10); // Adjust padding above the label
        loginPanel.add(goToSignupLabel, gbc);


        // Add the login panel to the main panel, centered
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainPanel.add(loginPanel, mainGbc);

        // Add the main panel to the JFrame
        add(mainPanel);

        // Add an ActionListener to the login button
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userTextField.getText();
                String password = new String(passTextField.getPassword());

                UtilisateurController controller = new UtilisateurController();
                boolean success = controller.connecterUtilisateur(username, password);

                if (success) {
                    String role = controller.obtenirRoleUtilisateur(username);

                    // Redirection selon le rôle
                    switch (role) {
                        case "client":
                            new ClientMenuInterface().setVisible(true);
                            break;
                        case "cuisinier":
                            new CuisinierInterface().setVisible(true);
                            break;
                        case "serveuse":
                            new ServeuseInterface().setVisible(true);
                            break;
                        default:
                            JOptionPane.showMessageDialog(Login.this, "Rôle inconnu.");
                            return;
                    }

                    // Fermer la fenêtre Login
                    dispose();

                } else {
                    JOptionPane.showMessageDialog(Login.this,
                            "Nom d'utilisateur ou mot de passe incorrect.",
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
                // Create an instance of the Login GUI and make it visible
                new Login().setVisible(true);
            }
        });
    }
}
