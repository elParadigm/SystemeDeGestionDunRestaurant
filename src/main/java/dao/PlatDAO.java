package dao;

import model.Plat; // Assuming your Plat model is in the 'model' package


import java.io.ByteArrayInputStream; // Import ByteArrayInputStream
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatDAO {

    private Connection connection;

    public PlatDAO() {
        connection = SingletonConnection.getInstance(); // Get connection via the singleton
    }

    // Add a new plat to the database
    public int addPlat(Plat plat) {
        int generatedId = -1;
        String sql = "INSERT INTO plat(nom, description, prix, idMenu, imagePlat) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, plat.getNom());
            ps.setString(2, plat.getDescription());
            ps.setDouble(3, plat.getPrix());
            ps.setInt(4, plat.getIdMenu());
            // Set image data as bytes
            if (plat.getImage() != null) {
                ps.setBytes(5, plat.getImage());
            } else {
                // Use Types.BLOB for LONGBLOB
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
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        return generatedId; // Return the generated ID or -1 on failure
    }

    // Retrieve all plats from the database
    public List<Plat> getAllPlats() {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

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
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        return plats;
    }

    // Retrieve a plat by ID
    public Plat getPlatById(int id) {
        Plat plat = null;
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat WHERE idPlat = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    plat = new Plat();
                    plat.setIdPlat(rs.getInt("idPlat"));
                    plat.setNom(rs.getString("nom")); // Assuming column name is 'nom'
                    plat.setDescription(rs.getString("description"));
                    plat.setPrix(rs.getDouble("prix"));
                    plat.setIdMenu(rs.getInt("idMenu"));
                    plat.setImage(rs.getBytes("imagePlat")); // Get image data as bytes
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        return plat;
    }


    // Update an existing plat in the database
    public boolean updatePlat(Plat plat) {
        String sql = "UPDATE plat SET nom = ?, description = ?, prix = ?, idMenu = ?, imagePlat = ? WHERE idPlat = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, plat.getNom());
            ps.setString(2, plat.getDescription());
            ps.setDouble(3, plat.getPrix());
            ps.setInt(4, plat.getIdMenu());
            // Set image data as bytes
            if (plat.getImage() != null) {
                ps.setBytes(5, plat.getImage());
            } else {
                // Use Types.BLOB for LONGBLOB
                ps.setNull(5, Types.BLOB); // Handle case with no image
            }
            ps.setInt(6, plat.getIdPlat());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
            return false;
        }
    }

    // Delete a plat from the database
    public boolean deletePlat(int id) {
        String sql = "DELETE FROM plat WHERE idPlat = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
            return false;
        }
    }

    // Obtenir les plats par menu (Assuming this method is needed)
    public List<Plat> getPlatsByMenuId(int idMenu) {
        List<Plat> plats = new ArrayList<>();
        String sql = "SELECT idPlat, nom, description, prix, idMenu, imagePlat FROM plat WHERE idMenu = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idMenu);
            try (ResultSet rs = ps.executeQuery()) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        return plats;
    }

    // The getImage method seems redundant if Plat objects already contain image data.
    // You can likely remove this method and use plat.getImage() directly in the GUI.
    // If you still need a method to get a scaled Image from ID, you can keep it,
    // but it should probably use getPlatById internally to get the byte data.
     /*
     public static Image getImage(int id) {
         Plat plat = getPlatById(id); // Call the instance method
         if (plat != null && plat.getImage() != null) {
             try {
                 ImageIcon icon = new ImageIcon(plat.getImage());
                 return icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         return null;
     }
     */
}
