package gui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import gui.Login;
import gui.ListeDePlatsInterface;
import gui.ChefOrderInterface;

public class CuisinierInterface extends JFrame {
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();

    public CuisinierInterface() {
        setTitle("Interface Cuisinier");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 720);
        setLocationRelativeTo(null);

        BackgroundPanel mainPanel = new BackgroundPanel("background.jpg");
        mainPanel.setLayout(new GridBagLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(COLOR_PANEL_BACKGROUND);
        contentPanel.setLayout(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PANEL_BORDER, 1),
                BorderFactory.createEmptyBorder(40, 60, 40, 60)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel welcomeLabel = new JLabel("Bonjour , Chef");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(COLOR_TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 40, 10);
        contentPanel.add(welcomeLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 20, 10, 20);

        JButton viewMenuButton = new JButton("voir le menu");
        viewMenuButton.setFont(new Font("Arial", Font.BOLD, 18));
        viewMenuButton.setBackground(COLOR_BUTTON_BACKGROUND);
        viewMenuButton.setForeground(COLOR_BUTTON_TEXT);
        viewMenuButton.setFocusPainted(false);
        viewMenuButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        viewMenuButton.setOpaque(true);
        viewMenuButton.setBorderPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(viewMenuButton, gbc);

        JButton viewOrdersButton = new JButton("voir les ordres");
        viewOrdersButton.setFont(new Font("Arial", Font.BOLD, 18));
        viewOrdersButton.setBackground(COLOR_BUTTON_BACKGROUND);
        viewOrdersButton.setForeground(COLOR_BUTTON_TEXT);
        viewOrdersButton.setFocusPainted(false);
        viewOrdersButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        viewOrdersButton.setOpaque(true);
        viewOrdersButton.setBorderPainted(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        contentPanel.add(viewOrdersButton, gbc);

        GridBagConstraints mainPanelGbc = new GridBagConstraints();
        mainPanelGbc.gridx = 0;
        mainPanelGbc.gridy = 0;
        mainPanelGbc.weightx = 1.0;
        mainPanelGbc.weighty = 1.0;
        mainPanelGbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(contentPanel, mainPanelGbc);

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
                    CuisinierInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new Login().setVisible(true);
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
                    CuisinierInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new Login().setVisible(true);
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

        viewMenuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Voir le menu button clicked!");
                CuisinierInterface.this.dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new ListeDePlatsInterface().setVisible(true);
                    }
                });
            }
        });

        viewOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Voir les ordres button clicked!");
                CuisinierInterface.this.dispose();
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        new ChefOrderInterface().setVisible(true);
                    }
                });
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
                new CuisinierInterface().setVisible(true);
            }
        });
    }
}