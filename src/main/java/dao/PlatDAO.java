package dao;

import model.Plat; // Assuming your Plat model is in the 'model' package


import java.io.ByteArrayInputStream; // Import ByteArrayInputStream
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatDAO {

    // The connection is obtained from the Singleton in each method,
    // but not closed here. The Singleton manages its lifecycle.

    // Add a new plat to the database
    public int addPlat(Plat plat) {
        int generatedId = -1;
        String sql = "INSERT INTO plat(nom, description, prix, idMenu, imagePlat) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Manage only statement here
                ps.setString(1, plat.getNom());
                ps.setString(2, plat.getDescription());
                ps.setDouble(3, plat.getPrix());
                ps.setInt(4, plat.getIdMenu());
                // Set image data as bytes
                if (plat.getImage() != null) {
                    ps.setBytes(5, plat.getImage());
                } else {
                    ps.setNull(5, Types.BLOB); // Handle case with no image
                }

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            generatedId = rs.getInt(1); // Get the auto-generated ID
                        }
                    }
                }
            } // PreparedStatement and ResultSet are closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        // Connection is NOT closed here. The Singleton manages it.
        return generatedId; // Return the generated ID or -1 on failure
    }

    // Retrieve all plats from the database
    public List<Plat> getAllPlats() {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (Statement stmt = conn.createStatement(); // Manage statement here
                 ResultSet rs = stmt.executeQuery(sql)) { // Manage result set here

                while (rs.next()) {
                    Plat plat = new Plat();
                    plat.setIdPlat(rs.getInt("idPlat"));
                    plat.setNom(rs.getString("nom")); // Assuming column name is 'nom'
                    plat.setDescription(rs.getString("description"));
                    plat.setPrix(rs.getDouble("prix"));
                    plat.setIdMenu(rs.getInt("idMenu"));
                    plat.setImage(rs.getBytes("imagePlat")); // Get image data as bytes
                    plats.add(plat);
                }
            } // Statement and ResultSet are closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        // Connection is NOT closed here. The Singleton manages it.
        return plats;
    }

    // Retrieve a plat by ID
    public Plat getPlatById(int id) {
        Plat plat = null;
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat WHERE idPlat = ?";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Manage statement here
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) { // Manage result set here
                    if (rs.next()) {
                        plat = new Plat();
                        plat.setIdPlat(rs.getInt("idPlat"));
                        plat.setNom(rs.getString("nom")); // Assuming column name is 'nom'
                        plat.setDescription(rs.getString("description"));
                        plat.setPrix(rs.getDouble("prix"));
                        plat.setIdMenu(rs.getInt("idMenu"));
                        plat.setImage(rs.getBytes("imagePlat")); // Get image data as bytes
                    }
                } // ResultSet is closed here automatically
            } // PreparedStatement is closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        // Connection is NOT closed here. The Singleton manages it.
        return plat;
    }


    // Update an existing plat in the database
    public boolean updatePlat(Plat plat) {
        String sql = "UPDATE plat SET nom = ?, description = ?, prix = ?, idMenu = ?, imagePlat = ? WHERE idPlat = ?";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Manage statement here
                ps.setString(1, plat.getNom());
                ps.setString(2, plat.getDescription());
                ps.setDouble(3, plat.getPrix());
                ps.setInt(4, plat.getIdMenu());
                // Set image data as bytes
                if (plat.getImage() != null) {
                    ps.setBytes(5, plat.getImage());
                } else {
                    ps.setNull(5, Types.BLOB); // Handle case with no image
                }
                ps.setInt(6, plat.getIdPlat());

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            } // PreparedStatement is closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
            return false;
        }
        // Connection is NOT closed here. The Singleton manages it.
    }

    // Delete a plat from the database
    public boolean deletePlat(int id) {
        String sql = "DELETE FROM plat WHERE idPlat = ?";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Manage statement here
                ps.setInt(1, id);
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            } // PreparedStatement is closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
            return false;
        }
        // Connection is NOT closed here. The Singleton manages it.
    }

    // Obtenir les plats par menu
    public List<Plat> getPlatsByMenuId(int idMenu) {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat WHERE idMenu = ?";
        Connection conn = null; // Declare connection outside try-with-resources
        try { // Start try block
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) { // Manage statement here
                ps.setInt(1, idMenu);
                try (ResultSet rs = ps.executeQuery()) { // Manage result set here
                    while (rs.next()) {
                        Plat plat = new Plat();
                        plat.setIdPlat(rs.getInt("idPlat"));
                        plat.setNom(rs.getString("nom")); // Assuming column name is 'nom'
                        plat.setDescription(rs.getString("description"));
                        plat.setPrix(rs.getDouble("prix"));
                        plat.setIdMenu(rs.getInt("idMenu"));
                        plat.setImage(rs.getBytes("imagePlat")); // Get image data as bytes
                        plats.add(plat);
                    }
                } // ResultSet is closed here automatically
            } // PreparedStatement is closed here automatically
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        // Connection is NOT closed here. The Singleton manages it.
        return plats;
    }
}