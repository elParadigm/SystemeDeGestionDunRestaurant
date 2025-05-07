package dao;

import model.Serveuse;
import model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServeuseDAO extends UtilisateurDAO {

    // Obtenir une serveuse par ID
    public Serveuse getServeuseById(int id) {
        Utilisateur utilisateur = super.getUtilisateurById(id);
        if (utilisateur != null && "serveuse".equalsIgnoreCase(utilisateur.getRole())) {
            Serveuse serveuse = new Serveuse();
            serveuse.setIdUtilisateur(utilisateur.getIdUtilisateur());
            serveuse.setNomUtilisateur(utilisateur.getNomUtilisateur());
            serveuse.setMotDePasse(utilisateur.getMotDePasse());
            serveuse.setRole(utilisateur.getRole());
            return serveuse;
        }
        return null;
    }

    // Obtenir toutes les serveuses
    public List<Serveuse> getAllServeuses() {
        List<Utilisateur> utilisateurs = super.getAllUtilisateurs();
        List<Serveuse> serveuses = new ArrayList<>();

        for (Utilisateur utilisateur : utilisateurs) {
            if ("serveuse".equalsIgnoreCase(utilisateur.getRole())) {
                Serveuse serveuse = new Serveuse();
                serveuse.setIdUtilisateur(utilisateur.getIdUtilisateur());
                serveuse.setNomUtilisateur(utilisateur.getNomUtilisateur());
                serveuse.setMotDePasse(utilisateur.getMotDePasse());
                serveuse.setRole(utilisateur.getRole());
                serveuses.add(serveuse);
            }
        }

        return serveuses;
    }

    // Insérer une serveuse
    public boolean insertServeuse(Serveuse serveuse) {
        // Forcer le rôle à "serveuse"
        serveuse.setRole("serveuse");
        return super.insertUtilisateur(serveuse);
    }
}
