package dao;

import model.Utilisateur;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    private Connection connection;

    public UtilisateurDAO() {
        connection = SingletonConnection.getInstance();
    }

    public boolean insertUtilisateur(Utilisateur utilisateur) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO utilisateur(nomUtilisateur, motDePasse, role) VALUES (?, ?, ?)"
            );
            ps.setString(1, utilisateur.getNomUtilisateur());
            ps.setString(2, utilisateur.getMotDePasse());
            ps.setString(3, utilisateur.getRole());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Utilisateur getUtilisateurById(int id) {
        Utilisateur utilisateur = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM utilisateur WHERE idUtilisateur = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                utilisateur.setNomUtilisateur(rs.getString("nomUtilisateur"));
                utilisateur.setMotDePasse(rs.getString("motDePasse"));
                utilisateur.setRole(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateur;
    }

    public Utilisateur getUtilisateurByUsername(String username) {
        Utilisateur utilisateur = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM utilisateur WHERE nomUtilisateur = ?"
            );
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                utilisateur.setNomUtilisateur(rs.getString("nomUtilisateur"));
                utilisateur.setMotDePasse(rs.getString("motDePasse"));
                utilisateur.setRole(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateur;
    }

    public List<Utilisateur> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM utilisateur");
            while (rs.next()) {
                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
                utilisateur.setNomUtilisateur(rs.getString("nomUtilisateur"));
                utilisateur.setMotDePasse(rs.getString("motDePasse"));
                utilisateur.setRole(rs.getString("role"));
                utilisateurs.add(utilisateur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    public boolean updateUtilisateur(Utilisateur utilisateur) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE utilisateur SET nomUtilisateur = ?, motDePasse = ?, role = ? WHERE idUtilisateur = ?"
            );
            ps.setString(1, utilisateur.getNomUtilisateur());
            ps.setString(2, utilisateur.getMotDePasse());
            ps.setString(3, utilisateur.getRole());
            ps.setInt(4, utilisateur.getIdUtilisateur());
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUtilisateur(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM utilisateur WHERE idUtilisateur = ?"
            );
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean authentifier(String username, String password) {
        String sql = "SELECT motDePasse FROM utilisateur WHERE nomUtilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String hashedPasswordInDB = rs.getString("motDePasse");
                return BCrypt.checkpw(password, hashedPasswordInDB);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public String obtenirRole(String nomUtilisateur) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT role FROM utilisateur WHERE nomUtilisateur = ?"
            );
            ps.setString(1, nomUtilisateur);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}