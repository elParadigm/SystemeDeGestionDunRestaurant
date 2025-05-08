package dao;

import model.Commande;
import model.Plat;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandeDAO {

    private Connection connection;

    public CommandeDAO() {
        connection = SingletonConnection.getInstance();
    }

    // Ajouter une commande sans les plats (à gérer via LigneCommandeDAO)
    public boolean insertCommande(Commande commande) {
        String sql = "INSERT INTO commande(idUtilisateur, horodatage, statut, montantTotal) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commande.getIdUtilisateur());
            ps.setDate(2, new java.sql.Date(commande.getHorodatage().getTime()));
            ps.setString(3, commande.getStatut());
            ps.setDouble(4, commande.getMontantTotal());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    commande.setIdCommande(generatedKeys.getInt(1)); // Récupère l'id généré
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Obtenir une commande par ID (sans les plats)
    public Commande getCommandeById(int id) {
        Commande commande = null;
        String sql = "SELECT * FROM commande WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                commande = new Commande();
                commande.setIdCommande(rs.getInt("idCommande"));
                commande.setIdUtilisateur(rs.getInt("idUtilisateur"));
                commande.setHorodatage(rs.getDate("horodatage"));
                commande.setStatut(rs.getString("statut"));
                commande.setMontantTotal(rs.getDouble("montantTotal"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commande;
    }

    // Obtenir toutes les commandes (sans les plats)
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Commande commande = new Commande();
                commande.setIdCommande(rs.getInt("idCommande"));
                commande.setIdUtilisateur(rs.getInt("idUtilisateur"));
                commande.setHorodatage(rs.getDate("horodatage"));
                commande.setStatut(rs.getString("statut"));
                commande.setMontantTotal(rs.getDouble("montantTotal"));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    // Mettre à jour une commande
    public boolean updateCommande(Commande commande) {
        String sql = "UPDATE commande SET idUtilisateur = ?, horodatage = ?, statut = ?, montantTotal = ? WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commande.getIdUtilisateur());
            ps.setDate(2, new java.sql.Date(commande.getHorodatage().getTime()));
            ps.setString(3, commande.getStatut());
            ps.setDouble(4, commande.getMontantTotal());
            ps.setInt(5, commande.getIdCommande());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une commande
    public boolean deleteCommande(int id) {
        String sql = "DELETE FROM commande WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtenir les commandes d’un utilisateur
    public List<Commande> getCommandesByUtilisateurId(int idUtilisateur) {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande WHERE idUtilisateur = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Commande commande = new Commande();
                commande.setIdCommande(rs.getInt("idCommande"));
                commande.setIdUtilisateur(rs.getInt("idUtilisateur"));
                commande.setHorodatage(rs.getDate("horodatage"));
                commande.setStatut(rs.getString("statut"));
                commande.setMontantTotal(rs.getDouble("montantTotal"));
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }
}