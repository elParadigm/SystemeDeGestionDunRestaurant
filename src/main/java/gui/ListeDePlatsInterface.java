package gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import model.Plat;
import dao.PlatDAO;
import gui.CuisinierInterface;

class DishDetailsDialog extends JDialog {
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JButton selectImageButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JTextField priceField;
    private JTextField menuIdField;
    private byte[] selectedImageData = null;
    private boolean isModification = false;
    private DishPanel dishPanelToModify;
    private ListeDePlatsInterface parentInterface;

    public DishDetailsDialog(JFrame parent, ListeDePlatsInterface parentInterface) {
        super(parent, "Ajouter un nouveau plat", true);
        this.parentInterface = parentInterface;
        setupDialog();
        saveButton.setText("Ajouter le plat");
    }

    public DishDetailsDialog(JFrame parent, DishPanel dishPanelToModify, ListeDePlatsInterface parentInterface) {
        super(parent, "Modifier le plat", true);
        this.dishPanelToModify = dishPanelToModify;
        this.parentInterface = parentInterface;
        isModification = true;
        setupDialog();
        saveButton.setText("Sauvegarder les modifications");
        populateFields();
    }

    private void setupDialog() {
        setSize(400, 450);
        setLocationRelativeTo(getParent());
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(250, 246, 233));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel nameLabel = new JLabel("Nom du plat:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; add(nameLabel, gbc);
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; add(nameField, gbc);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descriptionLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.weightx = 0.0; add(descriptionLabel, gbc);
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; add(descriptionScrollPane, gbc);

        JLabel priceLabel = new JLabel("Prix:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        priceLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weighty = 0.0; add(priceLabel, gbc);
        priceField = new JTextField(10);
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; add(priceField, gbc);

        JLabel menuIdLabel = new JLabel("ID Menu:");
        menuIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
        menuIdLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weighty = 0.0; add(menuIdLabel, gbc);
        menuIdField = new JTextField(10);
        menuIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; add(menuIdField, gbc);

        JLabel imagePathLabel = new JLabel("Image (laissez vide pour ne pas changer):");
        imagePathLabel.setFont(new Font("Arial", Font.BOLD, 14));
        imagePathLabel.setForeground(new Color(50, 50, 50));
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.WEST; gbc.weighty = 0.0; gbc.fill = GridBagConstraints.NONE; add(imagePathLabel, gbc);
        imagePathField = new JTextField(20);
        imagePathField.setFont(new Font("Arial", Font.PLAIN, 14));
        imagePathField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; add(imagePathField, gbc);

        selectImageButton = new JButton("Choisir une image");
        selectImageButton.setFont(new Font("Arial", Font.BOLD, 12));
        selectImageButton.setBackground(new Color(160, 200, 120));
        selectImageButton.setForeground(Color.WHITE);
        selectImageButton.setFocusPainted(false);
        selectImageButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        selectImageButton.setOpaque(true);
        selectImageButton.setBorderPainted(false);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 0.0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST; add(selectImageButton, gbc);
        selectImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Choisir une image pour le plat");
                int userSelection = fileChooser.showOpenDialog(DishDetailsDialog.this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    imagePathField.setText(selectedFile.getAbsolutePath());
                    try (FileInputStream fis = new FileInputStream(selectedFile)) {
                        selectedImageData = new byte[(int) selectedFile.length()];
                        fis.read(selectedImageData);
                    } catch (IOException ex) {
                        selectedImageData = null;
                        JOptionPane.showMessageDialog(DishDetailsDialog.this, "Erreur de lecture de l'image: " + ex.getMessage(), "Erreur d'image", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(250, 246, 233));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.NONE; add(buttonPanel, gbc);

        saveButton = new JButton();
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setBackground(new Color(60, 179, 113));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String description = descriptionArea.getText().trim();
                String priceText = priceField.getText().trim();
                String menuIdText = menuIdField.getText().trim();

                if (name.isEmpty() || description.isEmpty() || priceText.isEmpty() || menuIdText.isEmpty()) {
                    JOptionPane.showMessageDialog(DishDetailsDialog.this, "Veuillez remplir tous les champs (excepté le chemin de l'image si vous ne la changez pas).", "Champs manquants", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                double price;
                int menuId;
                try {
                    price = Double.parseDouble(priceText);
                    menuId = Integer.parseInt(menuIdText);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(DishDetailsDialog.this, "Le prix et l'ID du menu doivent être des nombres valides.", "Erreur de format", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                PlatDAO platDAO = new PlatDAO();
                Plat platToSave;
                if (isModification) {
                    platToSave = dishPanelToModify.getPlat();
                } else {
                    platToSave = new Plat();
                }

                platToSave.setNom(name);
                platToSave.setDescription(description);
                platToSave.setPrix(price);
                platToSave.setIdMenu(menuId);

                if (selectedImageData != null) {
                    platToSave.setImage(selectedImageData);
                } else if (!isModification) {
                    if(platToSave.getImage() == null || platToSave.getImage().length == 0){
                        JOptionPane.showMessageDialog(DishDetailsDialog.this, "Veuillez choisir une image pour un nouveau plat.", "Image manquante", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                boolean success = false;
                try {
                    if (isModification) {
                        success = platDAO.updatePlat(platToSave);
                    } else {
                        int generatedId = platDAO.addPlat(platToSave);
                        if (generatedId != -1) {
                            platToSave.setIdPlat(generatedId);
                            success = true;
                        } else {
                            success = false;
                        }
                    }
                    if (success) {
                        if (isModification) {
                            dishPanelToModify.updateDishDetails(platToSave);
                            JOptionPane.showMessageDialog(DishDetailsDialog.this, "Plat modifié avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            parentInterface.addDish(new DishPanel(platToSave, parentInterface));
                            JOptionPane.showMessageDialog(DishDetailsDialog.this, "Plat ajouté avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        }
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(DishDetailsDialog.this,
                                (isModification ? "Échec de la mise à jour" : "Échec de l'ajout") + " du plat dans la base de données.",
                                "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(DishDetailsDialog.this, "Erreur lors de l'opération sur le plat: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
        buttonPanel.add(saveButton);

        cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Arial", Font.BOLD, 12));
        cancelButton.setBackground(Color.GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(cancelButton);
    }

    private void populateFields() {
        if (dishPanelToModify != null) {
            Plat plat = dishPanelToModify.getPlat();
            if (plat != null) {
                nameField.setText(plat.getNom());
                descriptionArea.setText(plat.getDescription());
                priceField.setText(String.valueOf(plat.getPrix()));
                menuIdField.setText(String.valueOf(plat.getIdMenu()));
                imagePathField.setText("");
                selectedImageData = plat.getImage();
            }
        }
    }
}

class DishPanel extends JPanel {
    private JLabel imageLabel;
    private JLabel nameLabel;
    private JLabel descriptionLabel;
    private JButton removeButton;
    private JButton modifyButton;
    private ListeDePlatsInterface parentInterface;
    private Plat plat;

    public DishPanel(Plat plat, ListeDePlatsInterface parentInterface) {
        this.plat = plat;
        this.parentInterface = parentInterface;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(250, 246, 233));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 220, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setPreferredSize(new Dimension(150, 200));
        setMaximumSize(new Dimension(150, 200));
        setMinimumSize(new Dimension(150, 200));

        imageLabel = new JLabel();
        updateImage(plat.getImage());
        imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameLabel = new JLabel(plat.getNom());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(50, 50, 50));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        descriptionLabel = new JLabel("<html><body style='text-align:center;'>" + plat.getDescription() + "</body></html>");
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionLabel.setForeground(new Color(80, 80, 80));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonPanel.setOpaque(false);

        modifyButton = new JButton("Modifier");
        modifyButton.setFont(new Font("Arial", Font.BOLD, 10));
        modifyButton.setBackground(new Color(255, 165, 0));
        modifyButton.setForeground(Color.WHITE);
        modifyButton.setFocusPainted(false);
        modifyButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        modifyButton.setOpaque(true);
        modifyButton.setBorderPainted(false);
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(DishPanel.this);
                if (parentFrame instanceof ListeDePlatsInterface) {
                    DishDetailsDialog dialog = new DishDetailsDialog(parentFrame, DishPanel.this, (ListeDePlatsInterface) parentFrame);
                    dialog.setVisible(true);
                }
            }
        });
        buttonPanel.add(modifyButton);

        removeButton = new JButton("Supprimer");
        removeButton.setFont(new Font("Arial", Font.BOLD, 10));
        removeButton.setBackground(Color.RED);
        removeButton.setForeground(Color.WHITE);
        removeButton.setFocusPainted(false);
        removeButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(parentInterface,
                        "Êtes-vous sûr de vouloir supprimer le plat '" + plat.getNom() + "'?",
                        "Confirmer la suppression",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    PlatDAO platDAO = new PlatDAO();
                    boolean success = platDAO.deletePlat(plat.getIdPlat());
                    if (success) {
                        if (parentInterface != null) {
                            parentInterface.removeDishPanel(DishPanel.this);
                            JOptionPane.showMessageDialog(parentInterface, "Plat supprimé avec succès!", "Succès", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(parentInterface, "Échec de la suppression du plat dans la base de données.", "Erreur de base de données", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        buttonPanel.add(removeButton);

        add(Box.createRigidArea(new Dimension(0, 5)));
        add(imageLabel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(nameLabel);
        add(Box.createRigidArea(new Dimension(0, 3)));
        add(descriptionLabel);
        add(Box.createVerticalGlue());
        add(buttonPanel);
        add(Box.createRigidArea(new Dimension(0, 5)));
    }

    public void updateDishDetails(Plat updatedPlat) {
        this.plat = updatedPlat;
        updateImage(this.plat.getImage());
        nameLabel.setText(this.plat.getNom());
        descriptionLabel.setText("<html><body style='text-align:center;'>" + this.plat.getDescription() + "</body></html>");
        revalidate();
        repaint();
    }

    private void updateImage(byte[] imageData) {
        ImageIcon dishIcon = null;
        if (imageData != null && imageData.length > 0) {
            try {
                BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(imageData));
                if (originalImage != null) {
                    Image scaledImg = originalImage.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    dishIcon = new ImageIcon(scaledImg);
                } else {
                    System.err.println("Could not read image from byte data.");
                }
            } catch (IOException e) {
                System.err.println("Error loading dish image from byte data: " + e.getMessage());
                imageLabel.setText("Image Load Error");
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                dishIcon = null;
            }
        } else {
            imageLabel.setText("No Image");
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            dishIcon = null;
        }
        imageLabel.setIcon(dishIcon);
    }

    public Plat getPlat() {
        return plat;
    }

    public String getImagePath() {
        return "";
    }

    public String getDishName() {
        return plat.getNom();
    }

    public String getDishDescription() {
        return plat.getDescription();
    }
}

public class ListeDePlatsInterface extends JFrame {
    private static final Color COLOR_BACKGROUND = Color.decode("#FFFDF6");
    private static final Color COLOR_PANEL_BACKGROUND = Color.decode("#FAF6E9");
    private static final Color COLOR_INPUT_FIELD_BACKGROUND = Color.decode("#FDFDFD");
    private static final Color COLOR_BUTTON_BACKGROUND = Color.decode("#A0C878");
    private static final Color COLOR_TEXT_DARK = new Color(50, 50, 50);
    private static final Color COLOR_BUTTON_TEXT = Color.WHITE;
    private static final Color COLOR_PANEL_BORDER = COLOR_PANEL_BACKGROUND.darker();
    private JPanel dishesContainerPanel;

    public ListeDePlatsInterface() {
        setTitle("Liste de Plats");
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
            Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            backIcon = new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Error loading back arrow icon: " + e.getMessage());
        }
        if (backIcon != null) {
            backIconLabel.setIcon(backIcon);
            backIconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            backIconLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            backIconLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ListeDePlatsInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new CuisinierInterface().setVisible(true);
                        }
                    });
                }
            });
            mainGbc.gridx = 0;
            mainGbc.gridy = 0;
            mainGbc.anchor = GridBagConstraints.NORTHWEST;
            mainGbc.insets = new Insets(10, 10, 0, 0);
            mainPanel.add(backIconLabel, mainGbc);
        } else {
            JLabel fallbackBackLabel = new JLabel("←");
            fallbackBackLabel.setFont(new Font("Arial", Font.BOLD, 24));
            fallbackBackLabel.setForeground(COLOR_TEXT_DARK);
            fallbackBackLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            fallbackBackLabel.setOpaque(false);
            fallbackBackLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ListeDePlatsInterface.this.dispose();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            new CuisinierInterface().setVisible(true);
                        }
                    });
                }
            });
            mainGbc.gridx = 0; mainGbc.gridy = 0;
            mainGbc.anchor = GridBagConstraints.NORTHWEST;
            mainGbc.insets = new Insets(10, 10, 0, 0);
            mainPanel.add(fallbackBackLabel, mainGbc);
        }

        JLabel titleLabel = new JLabel("Liste de Plats");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        mainGbc.gridx = 0;
        mainGbc.gridy = 0;
        mainGbc.gridwidth = 2;
        mainGbc.anchor = GridBagConstraints.NORTH;
        mainGbc.insets = new Insets(20, 10, 20, 10);
        mainPanel.add(titleLabel, mainGbc);

        dishesContainerPanel = new JPanel();
        dishesContainerPanel.setBackground(new Color(250, 246, 233, 150));
        dishesContainerPanel.setLayout(new GridLayout(0, 4, 20, 20));

        JScrollPane scrollPane = new JScrollPane(dishesContainerPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        mainGbc.gridx = 0;
        mainGbc.gridy = 1;
        mainGbc.gridwidth = 2;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 5.0;
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.insets = new Insets(10, 50, 10, 50);
        mainPanel.add(scrollPane, mainGbc);

        JLabel addPlateLabel = new JLabel();
        addPlateLabel.setOpaque(false);
        addPlateLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        ImageIcon plusIcon = null;
        try {
            Image img = ImageIO.read(new File("add.png"));
            Image scaledImg = img.getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            plusIcon = new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Error loading plus icon: " + e.getMessage());
        }
        if (plusIcon != null) {
            addPlateLabel.setIcon(plusIcon);
        } else {
            addPlateLabel.setText("+");
            addPlateLabel.setFont(new Font("Arial", Font.BOLD, 40));
            addPlateLabel.setForeground(COLOR_TEXT_DARK);
        }
        addPlateLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                DishDetailsDialog dialog = new DishDetailsDialog(ListeDePlatsInterface.this, ListeDePlatsInterface.this);
                dialog.setVisible(true);
            }
        });

        mainGbc.gridx = 1;
        mainGbc.gridy = 2;
        mainGbc.gridwidth = 1;
        mainGbc.anchor = GridBagConstraints.SOUTHEAST;
        mainGbc.weighty = 0.0;
        mainGbc.insets = new Insets(10, 0, 10, 10);
        mainPanel.add(addPlateLabel, mainGbc);

        add(mainPanel);
        loadAllPlats();
    }

    private void loadAllPlats() {
        dishesContainerPanel.removeAll();
        PlatDAO platDAO = new PlatDAO();
        List<Plat> allPlats = platDAO.getAllPlats();
        if (allPlats != null && !allPlats.isEmpty()) {
            dishesContainerPanel.setLayout(new GridLayout(0, 4, 20, 20));
            for (Plat plat : allPlats) {
                dishesContainerPanel.add(new DishPanel(plat, this));
            }
        } else {
            dishesContainerPanel.setLayout(new GridBagLayout());
            JLabel noPlatsLabel = new JLabel("Aucun plat disponible dans la base de données.");
            noPlatsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noPlatsLabel.setForeground(COLOR_TEXT_DARK);
            JPanel centeredPanel = new JPanel(new GridBagLayout());
            centeredPanel.setOpaque(false);
            centeredPanel.add(noPlatsLabel);
            dishesContainerPanel.add(centeredPanel, new GridBagConstraints());
        }
        dishesContainerPanel.revalidate();
        dishesContainerPanel.repaint();
    }

    public void addDish(DishPanel dishPanel) {
        if (dishesContainerPanel.getLayout() instanceof GridBagLayout) {
            dishesContainerPanel.removeAll();
            dishesContainerPanel.setLayout(new GridLayout(0, 4, 20, 20));
        }
        dishesContainerPanel.add(dishPanel);
        dishesContainerPanel.revalidate();
        dishesContainerPanel.repaint();
    }

    public void removeDishPanel(DishPanel dishPanel) {
        dishesContainerPanel.remove(dishPanel);
        if (dishesContainerPanel.getComponentCount() == 0) {
            dishesContainerPanel.setLayout(new GridBagLayout());
            JLabel noPlatsLabel = new JLabel("Aucun plat disponible dans la base de données.");
            noPlatsLabel.setFont(new Font("Arial", Font.PLAIN, 18));
            noPlatsLabel.setForeground(COLOR_TEXT_DARK);
            JPanel centeredPanel = new JPanel(new GridBagLayout());
            centeredPanel.setOpaque(false);
            centeredPanel.add(noPlatsLabel);
            dishesContainerPanel.add(centeredPanel, new GridBagConstraints());
        }
        dishesContainerPanel.revalidate();
        dishesContainerPanel.repaint();
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
                new ListeDePlatsInterface().setVisible(true);
            }
        });
    }
}