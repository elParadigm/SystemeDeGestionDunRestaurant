package dao;

import model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    private Connection connection;

    public ClientDAO() {
        connection = SingletonConnection.getInstance();
    }

    // Ajouter un client
    public boolean insertClient(Client client) {
        String sql = "INSERT INTO utilisateur (nomUtilisateur, motDePasse, role) VALUES (?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getNomUtilisateur());
            ps.setString(2, client.getMotDePasse());
            ps.setString(3, "Client"); // rôle fixe
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Récupérer un client par ID
    public Client getClientById(int id) {
        String sql = "SELECT * FROM utilisateur WHERE idUtilisateur = ? AND role = 'Client'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Client(
                        rs.getInt("idUtilisateur"),
                        rs.getString("nomUtilisateur"),
                        rs.getString("motDePasse")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lister tous les clients
    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE role = 'Client'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("idUtilisateur"),
                        rs.getString("nomUtilisateur"),
                        rs.getString("motDePasse")
                );
                clients.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // Mettre à jour un client
    public boolean updateClient(Client client) {
        String sql = "UPDATE utilisateur SET nomUtilisateur = ?, motDePasse = ? WHERE idUtilisateur = ? AND role = 'Client'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, client.getNomUtilisateur());
            ps.setString(2, client.getMotDePasse());
            ps.setInt(3, client.getIdUtilisateur());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un client
    public boolean deleteClient(int id) {
        String sql = "DELETE FROM utilisateur WHERE idUtilisateur = ? AND role = 'Client'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
