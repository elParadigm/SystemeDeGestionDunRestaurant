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

    /**
     * Méthode pour inscrire un nouvel utilisateur
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @param role Rôle (client, serveuse, cuisinier)
     * @return true si l'inscription réussit, false sinon
     */
    public boolean inscrireUtilisateur(String username, String password, String role) {
        if (username == null || password == null || role == null ||
                username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        // Hachage du mot de passe avant de l'enregistrer
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNomUtilisateur(username);
        utilisateur.setMotDePasse(hashedPassword); // Mot de passe sécurisé
        utilisateur.setRole(role);

        return utilisateurDAO.insertUtilisateur(utilisateur);
    }

    /**
     * Méthode pour connecter un utilisateur
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return true si les identifiants sont corrects, false sinon
     */
    public boolean connecterUtilisateur(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return false;
        }

        // Authentification via le DAO
        return utilisateurDAO.authentifier(username, password);
    }

    /**
     * Obtenir le rôle d’un utilisateur après connexion
     * @param username Nom d'utilisateur
     * @return rôle de l'utilisateur ou null si non trouvé
     */
    public String obtenirRoleUtilisateur(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        return utilisateurDAO.obtenirRole(username);
    }


}