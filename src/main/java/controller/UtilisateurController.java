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

        // Vérifier si le nom d'utilisateur existe déjà
        if (utilisateurDAO.getUtilisateurByUsername(username) != null) {
            System.out.println("Nom d'utilisateur déjà existant.");
            return false; // Utilisateur déjà existant
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
     * Méthode pour authentifier un utilisateur et retourner son objet Utilisateur.
     * Utilise BCrypt pour vérifier le mot de passe haché.
     * @param username Nom d'utilisateur
     * @param password Mot de passe en clair
     * @return L'objet Utilisateur si l'authentification réussit, null sinon.
     */
    public Utilisateur authenticateAndGetUser(String username, String password) {
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            return null; // Identifiants invalides
        }

        // Récupérer l'utilisateur par nom d'utilisateur
        Utilisateur utilisateur = utilisateurDAO.getUtilisateurByUsername(username);

        // Vérifier si l'utilisateur existe et si le mot de passe correspond au hachage stocké
        if (utilisateur != null && BCrypt.checkpw(password, utilisateur.getMotDePasse())) {
            return utilisateur; // Authentification réussie, retourner l'objet Utilisateur
        } else {
            return null; // Authentification échouée
        }
    }


    /**
     * Méthode pour connecter un utilisateur (ancienne version, peut être remplacée par authenticateAndGetUser)
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return true si les identifiants sont corrects, false sinon
     */
    // public boolean connecterUtilisateur(String username, String password) {
    //     // Cette méthode peut être remplacée par authenticateAndGetUser si vous récupérez l'utilisateur complet
    //     Utilisateur utilisateur = utilisateurDAO.getUtilisateurByUsername(username);
    //     return utilisateur != null && BCrypt.checkpw(password, utilisateur.getMotDePasse());
    // }


    /**
     * Obtenir le rôle d’un utilisateur par nom d'utilisateur
     * (Peut être remplacé par l'accès au rôle via l'objet Utilisateur retourné par authenticateAndGetUser)
     * @param username Nom d'utilisateur
     * @return rôle de l'utilisateur ou null si non trouvé
     */
    // public String obtenirRoleUtilisateur(String username) {
    //     Utilisateur utilisateur = utilisateurDAO.getUtilisateurByUsername(username);
    //     return (utilisateur != null) ? utilisateur.getRole() : null;
    // }

    // Vous pourriez vouloir ajouter d'autres méthodes ici, comme obtenirUtilisateurParId, etc.
}
