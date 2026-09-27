package services;

import models.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import DAOS.ClientDAO;
import exceptions.ClientIntrouvableException;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAO();

   
    public boolean ajouterClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client à ajouter ne peut pas être nul.");
        }
        if (client.getNom() == null || client.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du client ne peut pas être vide.");
        }

        try {
            return clientDAO.create(client);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout du client : " + e.getMessage(), e);
        }
    }

   
    public boolean modifierClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Le client à modifier ne peut pas être nul.");
        }

        try {
            exigerClient(client.getId());
            return clientDAO.update(client);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la modification du client : " + e.getMessage(), e);
        }
    }

   
    public boolean supprimerClient(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID du client à supprimer ne peut pas être vide.");
        }

        try {
            exigerClient(id);
            return clientDAO.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du client : " + e.getMessage(), e);
        }
    }

   
    public Optional<Client> trouverClientParId(String id) {
        try {
            return clientDAO.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du client : " + e.getMessage(), e);
        }
    }

   
    public List<Client> listerTousLesClients() {
        try {
            return clientDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des clients : " + e.getMessage(), e);
        }
    }

    
    public List<Client> trouverClientsParNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom recherché ne peut pas être vide.");
        }

        try {
            return clientDAO.findAll().stream()
                    .filter(c -> c.getNom() != null && c.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par nom : " + e.getMessage(), e);
        }
    }

    
    private Client exigerClient(String id) throws SQLException {
        return clientDAO.findById(id)
                .orElseThrow(() -> new ClientIntrouvableException("Client introuvable avec l'ID " + id));
    }
}