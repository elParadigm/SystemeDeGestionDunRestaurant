package dao;

import model.Facture;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date; // Import java.util.Date

public class FactureDAO {

    // Removed the 'connection' instance variable.
    // Connection is obtained from the Singleton in each method.

    // Insert a new facture into the database
    public boolean insertFacture(Facture facture) {
        String sql = "INSERT INTO facture(idCommande, dateFacture, montantTotal) VALUES (?, ?, ?)";
        Connection conn = null; // Declare connection inside the method
        try {
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, facture.getIdCommande());
                // Assuming Facture model has getHorodatage() returning java.util.Date
                ps.setDate(2, new java.sql.Date(facture.getHorodatage().getTime()));
                ps.setDouble(3, facture.getMontantTotal());

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            facture.setIdFacture(generatedKeys.getInt(1)); // Set the generated ID back on the object
                        }
                    }
                    return true; // Insertion successful
                }
            } // PreparedStatement and ResultSet (if any) are closed here
        } catch (SQLException e) {
            e.printStackTrace();
            // Log the error
        }
        // Connection is NOT closed here. Singleton manages it.
        return false; // Insertion failed
    }

    // Retrieve a facture by ID
    public Facture getFactureById(int idFacture) {
        Facture facture = null;
        String sql = "SELECT idFacture, idCommande, dateFacture, montantTotal FROM facture WHERE idFacture = ?"; // Select columns explicitly
        Connection conn = null; // Declare connection inside the method
        try {
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idFacture);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Assuming Facture constructor takes java.util.Date
                        facture = new Facture(
                                rs.getDate("dateFacture"), // get java.sql.Date
                                rs.getInt("idCommande"),
                                rs.getInt("idFacture"),
                                rs.getDouble("montantTotal")
                        );
                        // The constructor likely handles converting java.sql.Date to java.util.Date if needed
                    }
                } // ResultSet closed
            } // PreparedStatement closed
        } catch (SQLException e) {
            e.printStackTrace();
            // Log the error
        }
        // Connection is NOT closed here. Singleton manages it.
        return facture;
    }

    // Retrieve all factures from the database
    public List<Facture> getAllFactures() {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT idFacture, idCommande, dateFacture, montantTotal FROM facture"; // Select columns explicitly
        Connection conn = null; // Declare connection inside the method
        try {
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    // Assuming Facture constructor takes java.util.Date
                    Facture facture = new Facture(
                            rs.getDate("dateFacture"), // get java.sql.Date
                            rs.getInt("idCommande"),
                            rs.getInt("idFacture"),
                            rs.getDouble("montantTotal")
                    );
                    factures.add(facture);
                }
            } // Statement and ResultSet closed
        } catch (SQLException e) {
            e.printStackTrace();
            // Log the error
        }
        // Connection is NOT closed here. Singleton manages it.
        return factures;
    }

    public boolean updateFacture(Facture facture) {
        String sql = "UPDATE facture SET idCommande = ?, dateFacture = ?, montantTotal = ? WHERE idFacture = ?";
        Connection conn = null; // Declare connection inside the method
        try {
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, facture.getIdCommande());
                // Assuming Facture model has getHorodatage() returning java.util.Date
                ps.setDate(2, new java.sql.Date(facture.getHorodatage().getTime()));
                ps.setDouble(3, facture.getMontantTotal());
                ps.setInt(4, facture.getIdFacture());

                int rows = ps.executeUpdate();
                return rows > 0;
            } // PreparedStatement closed
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        // Connection is NOT closed here. Singleton manages it.
    }

    public boolean deleteFacture(int idFacture) {
        String sql = "DELETE FROM facture WHERE idFacture = ?";
        Connection conn = null; // Declare connection inside the method
        try {
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idFacture);
                int rows = ps.executeUpdate();
                return rows > 0;
            } // PreparedStatement closed
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        // Connection is NOT closed here. Singleton manages it.
    }

    public List<Facture> getFacturesByCommandeId(int idCommande) {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT idFacture, idCommande, dateFacture, montantTotal FROM facture WHERE idCommande = ?"; // Select columns explicitly
        Connection conn = null; // Declare connection inside the method
        try {
            conn = SingletonConnection.getInstance(); // Get connection from Singleton
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCommande);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // Assuming Facture constructor takes java.util.Date
                        Facture facture = new Facture(
                                rs.getDate("dateFacture"), // get java.sql.Date
                                rs.getInt("idCommande"),
                                rs.getInt("idFacture"),
                                rs.getDouble("montantTotal")
                        );
                        factures.add(facture);
                    }
                } // ResultSet closed
            } // PreparedStatement closed
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Connection is NOT closed here. Singleton manages it.
        return factures;
    }
}