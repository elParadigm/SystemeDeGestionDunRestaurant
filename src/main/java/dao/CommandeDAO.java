package dao;

import model.Commande;
import model.Plat; // Assuming Plat model is needed for CartItem details
import gui.CartItem; // Import CartItem class from gui package

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandeDAO {

    private Connection connection;

    public CommandeDAO() {
        connection = SingletonConnection.getInstance(); // Get connection via the singleton
    }

    // Insert a new commande record and return the generated ID
    public int insertCommande(Commande commande) {
        int generatedId = -1;
        // Corrected SQL: dateCommande column name and removed horodatage
        // Using idClient in SQL to match database schema, but mapping from Commande.getIdUtilisateur()
        String sql = "INSERT INTO commande(idClient, dateCommande, statut, montantTotal) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commande.getIdUtilisateur()); // Use getIdUtilisateur() from Commande model
            // Use Timestamp for dateCommande as it's a datetime field
            ps.setTimestamp(2, new Timestamp(commande.getHorodatage().getTime())); // Use getHorodatage() from Commande model
            ps.setString(3, commande.getStatut());
            ps.setDouble(4, commande.getMontantTotal());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        generatedId = generatedKeys.getInt(1); // Get the auto-generated ID
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
        }
        return generatedId; // Return the generated ID or -1 on failure
    }

    // Insert a record into the commandeplat table
    public boolean insertCommandePlat(int idCommande, int idPlat, int quantite) {
        String sql = "INSERT INTO commandeplat(idCommande, idPlat, quantite) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCommande);
            ps.setInt(2, idPlat);
            ps.setInt(3, quantite);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Consider logging the error or throwing a custom exception
            return false;
        }
    }

    // Place an entire order (commande and commandeplat items) within a transaction
    // This method still takes idClient as it's the parameter passed from the GUI
    public boolean placeOrder(int idClient, List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("Cannot place an empty order.");
            return false;
        }

        // Calculate total amount
        double totalAmount = 0;
        for (CartItem item : cartItems) {
            totalAmount += item.getPrice() * item.getQuantity();
        }

        // Create the main Commande object
        Commande newCommande = new Commande();
        newCommande.setIdUtilisateur(idClient); // Use setIdUtilisateur() from Commande model
        newCommande.setHorodatage(new Date()); // Use setHorodatage() from Commande model
        newCommande.setStatut("en_attente"); // Set initial status
        newCommande.setMontantTotal(totalAmount); // Set calculated total amount

        boolean success = false;
        try {
            connection.setAutoCommit(false); // Start transaction

            // 1. Insert the main commande record
            int idCommande = insertCommande(newCommande);

            if (idCommande != -1) {
                // 2. Insert each item into commandeplat
                boolean allItemsInserted = true;
                for (CartItem item : cartItems) {
                    // Ensure Plat object in CartItem is not null and has a valid idPlat
                    if (item.getPlat() != null && item.getPlat().getIdPlat() > 0) {
                        boolean itemInserted = insertCommandePlat(idCommande, item.getPlat().getIdPlat(), item.getQuantity());
                        if (!itemInserted) {
                            allItemsInserted = false;
                            break; // Stop if any item insertion fails
                        }
                    } else {
                        System.err.println("Error: CartItem contains a null Plat or invalid Plat ID.");
                        allItemsInserted = false;
                        break;
                    }
                }

                if (allItemsInserted) {
                    connection.commit(); // Commit transaction if all successful
                    success = true;
                    System.out.println("Order placed successfully with ID: " + idCommande);
                } else {
                    connection.rollback(); // Rollback if any item insertion failed
                    System.err.println("Order placement failed: Failed to insert all items.");
                }
            } else {
                connection.rollback(); // Rollback if main commande insertion failed
                System.err.println("Order placement failed: Failed to insert main commande record.");
            }

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback on any SQL exception
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.err.println("Order placement failed due to SQL exception.");
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // Restore auto-commit mode
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }


    // Obtenir une commande par ID (without plats - consider adding a method to get items too)
    public Commande getCommandeById(int id) {
        Commande commande = null;
        // Corrected SQL: dateCommande column name
        String sql = "SELECT * FROM commande WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) { // Use try-with-resources for ResultSet
                if (rs.next()) {
                    commande = new Commande();
                    commande.setIdCommande(rs.getInt("idCommande"));
                    commande.setIdUtilisateur(rs.getInt("idClient")); // Map idClient from DB to idUtilisateur in model
                    commande.setHorodatage(rs.getTimestamp("dateCommande")); // Map dateCommande from DB to horodatage in model
                    commande.setStatut(rs.getString("statut"));
                    commande.setMontantTotal(rs.getDouble("montantTotal"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commande;
    }

    // Obtenir toutes les commandes (without plats - consider adding a method to get items too)
    public List<Commande> getAllCommandes() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT * FROM commande";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Commande commande = new Commande();
                commande.setIdCommande(rs.getInt("idCommande"));
                commande.setIdUtilisateur(rs.getInt("idClient")); // Map idClient from DB to idUtilisateur in model
                commande.setHorodatage(rs.getTimestamp("dateCommande")); // Map dateCommande from DB to horodatage in model
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
        // Corrected SQL: dateCommande column name
        String sql = "UPDATE commande SET idClient = ?, dateCommande = ?, statut = ?, montantTotal = ? WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commande.getIdUtilisateur()); // Use getIdUtilisateur() from Commande model
            ps.setTimestamp(2, new Timestamp(commande.getHorodatage().getTime())); // Use getHorodatage() from Commande model
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

    // Obtenir les commandes d’un utilisateur (without plats - consider adding a method to get items too)
    public List<Commande> getCommandesByUtilisateurId(int idUtilisateur) {
        List<Commande> commandes = new ArrayList<>();
        // Corrected SQL: idClient and dateCommande column names
        String sql = "SELECT * FROM commande WHERE idClient = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try(ResultSet rs = ps.executeQuery()) { // Use try-with-resources for ResultSet
                while (rs.next()) {
                    Commande commande = new Commande();
                    commande.setIdCommande(rs.getInt("idCommande"));
                    commande.setIdUtilisateur(rs.getInt("idClient")); // Map idClient from DB to idUtilisateur in model
                    commande.setHorodatage(rs.getTimestamp("dateCommande")); // Map dateCommande from DB to horodatage in model
                    commande.setStatut(rs.getString("statut"));
                    commande.setMontantTotal(rs.getDouble("montantTotal"));
                    commandes.add(commande);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }

    // You might want methods to get the items for a specific order later
    // public List<CartItem> getCommandeItems(int idCommande) { ... }
}
