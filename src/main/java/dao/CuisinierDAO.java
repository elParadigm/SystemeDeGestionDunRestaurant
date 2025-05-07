package dao;

import model.Cuisinier;
import model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuisinierDAO extends UtilisateurDAO {

    // Obtenir un cuisinier par ID
    public Cuisinier getCuisinierById(int id) {
        Utilisateur utilisateur = super.getUtilisateurById(id);
        if (utilisateur != null && "cuisinier".equalsIgnoreCase(utilisateur.getRole())) {
            Cuisinier cuisinier = new Cuisinier();
            cuisinier.setIdUtilisateur(utilisateur.getIdUtilisateur());
            cuisinier.setNomUtilisateur(utilisateur.getNomUtilisateur());
            cuisinier.setMotDePasse(utilisateur.getMotDePasse());
            cuisinier.setRole(utilisateur.getRole());
            return cuisinier;
        }
        return null;
    }

    // Obtenir tous les cuisiniers
    public List<Cuisinier> getAllCuisiniers() {
        List<Utilisateur> utilisateurs = super.getAllUtilisateurs();
        List<Cuisinier> cuisiniers = new ArrayList<>();

        for (Utilisateur utilisateur : utilisateurs) {
            if ("cuisinier".equalsIgnoreCase(utilisateur.getRole())) {
                Cuisinier cuisinier = new Cuisinier();
                cuisinier.setIdUtilisateur(utilisateur.getIdUtilisateur());
                cuisinier.setNomUtilisateur(utilisateur.getNomUtilisateur());
                cuisinier.setMotDePasse(utilisateur.getMotDePasse());
                cuisinier.setRole(utilisateur.getRole());
                cuisiniers.add(cuisinier);
            }
        }

        return cuisiniers;
    }

    // Insérer un cuisinier
    public boolean insertCuisinier(Cuisinier cuisinier) {
        // Forcer le rôle à "cuisinier"
        cuisinier.setRole("cuisinier");
        return super.insertUtilisateur(cuisinier);
    }
}
