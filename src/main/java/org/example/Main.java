package org.example;

import gui.Login;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Pour s'assurer que l'interface graphique s'exécute sur le bon thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Lance la fenêtre de login ET la rend visible
                Login login = new Login();
                login.setVisible(true); // ← C'est cette ligne qui manquait
                System.out.println("jjj");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erreur lors du lancement de l'interface : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}