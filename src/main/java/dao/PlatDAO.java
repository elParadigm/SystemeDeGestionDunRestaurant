package dao;

import model.Plat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatDAO {

    private Connection connection;

    public PlatDAO() {
        connection = SingletonConnection.getInstance(); // ← Connexion via le singleton
    }

    // Créer un plat
    public boolean insertPlat(Plat plat) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO plat(nomPlat, description, prix, idMenu) VALUES (?, ?, ?, ?)"
            );
            ps.setString(1, plat.getNom());
            ps.setString(2, plat.getDescription());
            ps.setDouble(3, plat.getPrix());
            ps.setInt(4, plat.getIdMenu());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lire un plat par ID
    public Plat getPlatById(int id) {
        Plat plat = null;
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM plat WHERE idPlat = ?"
            );
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                plat = new Plat();
                plat.setIdPlat(rs.getInt("idPlat"));
                plat.setNom(rs.getString("nomPlat"));
                plat.setDescription(rs.getString("description"));
                plat.setPrix(rs.getDouble("prix"));
                plat.setIdMenu(rs.getInt("idMenu"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plat;
    }

    // Lire tous les plats
    public List<Plat> getAllPlats() {
        List<Plat> plats = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM plat");

            while (rs.next()) {
                Plat plat = new Plat();
                plat.setIdPlat(rs.getInt("idPlat"));
                plat.setNom(rs.getString("nomPlat"));
                plat.setDescription(rs.getString("description"));
                plat.setPrix(rs.getDouble("prix"));
                plat.setIdMenu(rs.getInt("idMenu"));
                plats.add(plat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plats;
    }

    // Mettre à jour un plat
    public boolean updatePlat(Plat plat) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "UPDATE plat SET nomPlat = ?, description = ?, prix = ?, idMenu = ? WHERE idPlat = ?"
            );
            ps.setString(1, plat.getNom());
            ps.setString(2, plat.getDescription());
            ps.setDouble(3, plat.getPrix());
            ps.setInt(4, plat.getIdMenu());
            ps.setInt(5, plat.getIdPlat());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un plat
    public boolean deletePlat(int id) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM plat WHERE idPlat = ?"
            );
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtenir les plats par menu
    public List<Plat> getPlatsByMenuId(int idMenu) {
        List<Plat> plats = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "SELECT * FROM plat WHERE idMenu = ?"
            );
            ps.setInt(1, idMenu);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Plat plat = new Plat();
                plat.setIdPlat(rs.getInt("idPlat"));
                plat.setNom(rs.getString("nomPlat"));
                plat.setDescription(rs.getString("description"));
                plat.setPrix(rs.getDouble("prix"));
                plat.setIdMenu(rs.getInt("idMenu"));
                plats.add(plat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return plats;
    }
}