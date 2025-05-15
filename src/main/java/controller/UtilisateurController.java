package controller;

import org.mindrot.jbcrypt.BCrypt;
import dao.UtilisateurDAO;
import model.Utilisateur;

public class UtilisateurController {

    private UtilisateurDAO utilisateurDAO;

    public UtilisateurController() {
        // Initialisation du DAO
        utilisateurDAO = new UtilisateurDAO();
    }


    public boolean inscrireUtilisateur(String username, String password, String role) {
        if (username == null || password == null || role == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }


        if (utilisateurDAO.getUtilisateurByUsername(username) != null) {
            System.out.println("Nom d'utilisateur déjà existant.");
            return false; // Utilisateur déjà existant
        }



        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNomUtilisateur(username);
        utilisateur.setMotDePasse(hashedPassword); // Mot de passe sécurisé
        utilisateur.setRole(role);

        return utilisateurDAO.insertUtilisateur(utilisateur);
    }


    public Utilisateur authenticateAndGetUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }


        Utilisateur utilisateur = utilisateurDAO.getUtilisateurByUsername(username);


        if (utilisateur != null && BCrypt.checkpw(password, utilisateur.getMotDePasse())) {
            return utilisateur;
        } else {
            return null;
        }
    }


}
