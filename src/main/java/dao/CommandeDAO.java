package dao;

import model.Commande;
import model.Plat; // Assuming Plat model is needed for CartItem details
import gui.CartItem; // Import CartItem class from gui package
import dao.PlatDAO; // Import PlatDAO to fetch Plat details for CartItems

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommandeDAO {

    private Connection connection;

    public CommandeDAO() {
        connection = SingletonConnection.getInstance(); // Get connection via the singleton
    }


    public int insertCommande(Commande commande) {
        int generatedId = -1;

        String sql = "INSERT INTO commande(idClient, dateCommande, statut, montantTotal) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, commande.getIdUtilisateur());
            ps.setTimestamp(2, new Timestamp(commande.getHorodatage().getTime()));
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

            return false;
        }
    }

    public boolean placeOrder(int idClient, List<CartItem> cartItems) { // Removed tableNumber parameter
        if (cartItems == null || cartItems.isEmpty()) {
            System.out.println("Cannot place an empty order.");
            return false;
        }

        double totalAmount = 0;
        for (CartItem item : cartItems) {
            totalAmount += item.getPrice() * item.getQuantity();
        }


        Commande newCommande = new Commande();
        newCommande.setIdUtilisateur(idClient);
        newCommande.setHorodatage(new Date());
        newCommande.setStatut("en_attente");
        newCommande.setMontantTotal(totalAmount);


        boolean success = false;
        try {
            connection.setAutoCommit(false); // Start transaction


            int idCommande = insertCommande(newCommande);

            if (idCommande != -1) {

                boolean allItemsInserted = true;
                for (CartItem item : cartItems) {

                    if (item.getPlat() != null && item.getPlat().getIdPlat() > 0) {
                        boolean itemInserted = insertCommandePlat(idCommande, item.getPlat().getIdPlat(), item.getQuantity());
                        if (!itemInserted) {
                            allItemsInserted = false;
                            break;
                        }
                    } else {
                        System.err.println("Error: CartItem contains a null Plat or invalid Plat ID.");
                        allItemsInserted = false;
                        break;
                    }
                }

                if (allItemsInserted) {
                    connection.commit();
                    success = true;
                    System.out.println("Order placed successfully with ID: " + idCommande);
                } else {
                    connection.rollback();
                    System.err.println("Order placement failed: Failed to insert all items.");
                }
            } else {
                connection.rollback();
                System.err.println("Order placement failed: Failed to insert main commande record.");
            }

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            System.err.println("Order placement failed due to SQL exception.");
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return success;
    }



    public Commande getCommandeById(int id) {
        Commande commande = null;
        // Corrected SQL: dateCommande column name
        String sql = "SELECT * FROM commande WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try(ResultSet rs = ps.executeQuery()) {
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


    public List<Commande> getAllCommandesWithItems() {
        List<Commande> commandes = new ArrayList<>();
        String sql = "SELECT idCommande, idClient, dateCommande, statut, montantTotal FROM commande ORDER BY dateCommande DESC"; // Removed numeroTable from SELECT
        PlatDAO platDAO = new PlatDAO();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Commande commande = new Commande();
                int idCommande = rs.getInt("idCommande");
                commande.setIdCommande(idCommande);
                commande.setIdUtilisateur(rs.getInt("idClient"));
                commande.setHorodatage(rs.getTimestamp("dateCommande"));
                commande.setStatut(rs.getString("statut"));
                commande.setMontantTotal(rs.getDouble("montantTotal"));


                List<CartItem> items = new ArrayList<>();
                String itemsSql = "SELECT idPlat, quantite FROM commandeplat WHERE idCommande = ?";
                try (PreparedStatement itemsPs = connection.prepareStatement(itemsSql)) {
                    itemsPs.setInt(1, idCommande);
                    try(ResultSet itemsRs = itemsPs.executeQuery()) {
                        while (itemsRs.next()) {
                            int idPlat = itemsRs.getInt("idPlat");
                            int quantite = itemsRs.getInt("quantite");

                            Plat plat = platDAO.getPlatById(idPlat); // Assuming PlatDAO has getPlatById
                            if (plat != null) {
                                items.add(new CartItem(plat, quantite)); // Add CartItem to the list
                            } else {
                                System.err.println("Warning: Could not find Plat with ID " + idPlat + " for Commande ID " + idCommande);
                            }
                        }
                    }
                }
                commande.setItems(items);
                commandes.add(commande);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commandes;
    }



    public boolean updateCommandeStatus(int idCommande, String status) {
        String sql = "UPDATE commande SET statut = ? WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idCommande);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }



    public boolean updateCommande(Commande commande) {

        String sql = "UPDATE commande SET idClient = ?, dateCommande = ?, statut = ?, montantTotal = ? WHERE idCommande = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, commande.getIdUtilisateur());
            ps.setTimestamp(2, new Timestamp(commande.getHorodatage().getTime()));
            ps.setString(3, commande.getStatut());
            ps.setDouble(4, commande.getMontantTotal());
            // Removed setting numeroTable
            ps.setInt(5, commande.getIdCommande());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


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


    public List<Commande> getCommandesByUtilisateurId(int idUtilisateur) {
        List<Commande> commandes = new ArrayList<>();
        // Corrected SQL: idClient and dateCommande column names, removed numeroTable
        String sql = "SELECT idCommande, idClient, dateCommande, statut, montantTotal FROM commande WHERE idClient = ?"; // Removed numeroTable from SELECT
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idUtilisateur);
            try(ResultSet rs = ps.executeQuery()) { // Use try-with-resources for ResultSet
                while (rs.next()) {
                    Commande commande = new Commande();
                    commande.setIdCommande(rs.getInt("idCommande"));
                    commande.setIdUtilisateur(rs.getInt("idClient"));
                    commande.setHorodatage(rs.getTimestamp("dateCommande"));
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


}
