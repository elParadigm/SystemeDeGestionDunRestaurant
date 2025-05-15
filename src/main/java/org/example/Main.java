package org.example;

import dao.SingletonConnection;
import gui.Login;
import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = SingletonConnection.getInstance();
            if (connection != null) {
                JOptionPane.showMessageDialog(null, "Connexion réussie à la base de données !");
                System.out.println("Connexion réussie !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Pour s'assurer que l'interface graphique s'exécute sur le bon thread
        SwingUtilities.invokeLater(() -> {
            try {

                Login login = new Login();
                login.setVisible(true); //
                System.out.println("success");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Erreur lors du lancement de l'interface : " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}