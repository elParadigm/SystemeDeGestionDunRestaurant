package dao;

import model.Client;
import model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO extends UtilisateurDAO {


    public Client getClientById(int id) {
        Utilisateur utilisateur = super.getUtilisateurById(id);
        if (utilisateur != null && "client".equalsIgnoreCase(utilisateur.getRole())) {
            Client client = new Client();
            client.setIdUtilisateur(utilisateur.getIdUtilisateur());
            client.setNomUtilisateur(utilisateur.getNomUtilisateur());
            client.setMotDePasse(utilisateur.getMotDePasse());
            client.setRole(utilisateur.getRole());
            return client;
        }
        return null;
    }


    public List<Client> getAllClients() {
        List<Utilisateur> utilisateurs = super.getAllUtilisateurs();
        List<Client> clients = new ArrayList<>();

        for (Utilisateur utilisateur : utilisateurs) {
            if ("client".equalsIgnoreCase(utilisateur.getRole())) {
                Client client = new Client();
                client.setIdUtilisateur(utilisateur.getIdUtilisateur());
                client.setNomUtilisateur(utilisateur.getNomUtilisateur());
                client.setMotDePasse(utilisateur.getMotDePasse());
                client.setRole(utilisateur.getRole());
                clients.add(client);
            }
        }

        return clients;
    }


    public boolean insertClient(Client client) {

        client.setRole("client");
        return super.insertUtilisateur(client);
    }
}
