package dao;

import model.Facture;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FactureDAO {

    private Connection connection;

    public FactureDAO() {
        connection = SingletonConnection.getInstance(); // Connexion via Singleton
    }


    public boolean insertFacture(Facture facture) {
        String sql = "INSERT INTO facture(idCommande, montantTotal, horodatage) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, facture.getIdCommande());
            ps.setDouble(2, facture.getMontantTotal());
            ps.setDate(3, new java.sql.Date(facture.getHorodatage().getTime()));

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    facture.setIdFacture(generatedKeys.getInt(1)); // Met à jour l'id généré
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 2. Récupérer une facture par ID
    public Facture getFactureById(int idFacture) {
        String sql = "SELECT * FROM facture WHERE idFacture = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idFacture);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Facture(
                        rs.getDate("horodatage"),
                        rs.getInt("idCommande"),
                        rs.getInt("idFacture"),
                        rs.getDouble("montantTotal")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 3. Obtenir toutes les factures
    public List<Facture> getAllFactures() {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT * FROM facture";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Facture facture = new Facture(
                        rs.getDate("horodatage"),
                        rs.getInt("idCommande"),
                        rs.getInt("idFacture"),
                        rs.getDouble("montantTotal")
                );
                factures.add(facture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factures;
    }


    public boolean updateFacture(Facture facture) {
        String sql = "UPDATE facture SET idCommande = ?, montantTotal = ?, horodatage = ? WHERE idFacture = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, facture.getIdCommande());
            ps.setDouble(2, facture.getMontantTotal());
            ps.setDate(3, new java.sql.Date(facture.getHorodatage().getTime()));
            ps.setInt(4, facture.getIdFacture());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteFacture(int idFacture) {
        String sql = "DELETE FROM facture WHERE idFacture = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idFacture);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Facture> getFacturesByCommandeId(int idCommande) {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT * FROM facture WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Facture facture = new Facture(
                        rs.getDate("horodatage"),
                        rs.getInt("idCommande"),
                        rs.getInt("idFacture"),
                        rs.getDouble("montantTotal")
                );
                factures.add(facture);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return factures;
    }
}