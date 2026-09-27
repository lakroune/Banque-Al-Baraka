package services;

import models.Client;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import DAOS.ClientDAO;
import exceptions.ClientIntrouvableException;

public class ClientService {

    private final ClientDAO clientDAO = new ClientDAO();

    /**
     * Ajoute un nouveau client.
     *
     * @throws IllegalArgumentException si le client est null ou si son nom est vide
     * @throws RuntimeException         en cas d'erreur technique (base de données)
     */
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

    /**
     * Modifie un client existant.
     *
     * @throws ClientIntrouvableException si aucun client ne porte cet ID
     * @throws IllegalArgumentException   si le client est null
     */
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

    /**
     * Supprime un client (et, en cascade, ses comptes et ses transactions).
     *
     * @throws ClientIntrouvableException si aucun client ne porte cet ID
     * @throws IllegalArgumentException   si l'ID est vide
     */
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

    /**
     * Recherche un client par son ID.
     *
     * @return le client, ou Optional.empty() si aucun client ne porte cet ID
     * @throws RuntimeException en cas d'erreur technique (base de données)
     */
    public Optional<Client> trouverClientParId(String id) {
        try {
            return clientDAO.findById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du client : " + e.getMessage(), e);
        }
    }

    /**
     * Liste tous les clients enregistrés.
     *
     * @throws RuntimeException en cas d'erreur technique (base de données)
     */
    public List<Client> listerTousLesClients() {
        try {
            return clientDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du chargement des clients : " + e.getMessage(), e);
        }
    }

    /**
     * Recherche les clients dont le nom contient la chaîne donnée.
     *
     * @throws IllegalArgumentException si le nom recherché est vide
     * @throws RuntimeException         en cas d'erreur technique (base de données)
     */
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

    /**
     * Vérifie qu'un client existe puis le retourne.
     *
     * @throws ClientIntrouvableException si aucun client ne porte cet ID
     */
    private Client exigerClient(String id) throws SQLException {
        return clientDAO.findById(id)
                .orElseThrow(() -> new ClientIntrouvableException("Client introuvable avec l'ID " + id));
    }
}