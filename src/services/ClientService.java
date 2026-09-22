package services;

import models.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import DAOS.ClientDAO;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAO();

    public boolean ajouterClient(Client client) {
        try {
            if (client.getNom() == null || client.getNom().trim().isEmpty()) {
                System.out.println("Erreur : Le nom du client ne peut pas être vide.");
                return false;
            }
            return clientDAO.create(client);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du client : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean modifierClient(Client client) {
        try {
            Optional<Client> existingClient = clientDAO.findById(client.getId());
            if (existingClient.isEmpty()) {
                System.out.println("Erreur : Client introuvable avec l'ID " + client.getId());
                return false;
            }
            return clientDAO.update(client);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du client : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean supprimerClient(String id) {
        try {
            Optional<Client> existingClient = clientDAO.findById(id);
            if (existingClient.isEmpty()) {
                System.out.println("Erreur : Impossible de supprimer, client introuvable avec l'ID " + id);
                return false;
            }
            return clientDAO.delete(id);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du client : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     */
    public Optional<Client> trouverClientParId(String id) {
        try {
            return clientDAO.findById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     */
    public List<Client> listerTousLesClients() {
        try {
            return clientDAO.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public List<Client> trouverClientsParNom(String nom) {
        try {
            return clientDAO.findAll().stream()
                    .filter(c -> c.getNom() != null && c.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .toList();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par nom : " + e.getMessage());
            e.printStackTrace();
        }
        return List.of();
    }
}