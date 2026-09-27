package services;

import models.Compte;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import DAOS.CompteDAO;
import exceptions.CompteIntrouvableException;

public class CompteService {

    private final CompteDAO compteDAO = new CompteDAO();

    /**
     * Crée un compte rattaché à un client.
     *
     * @throws IllegalArgumentException si le compte est null, sans numéro ou sans client
     * @throws RuntimeException         en cas d'erreur technique (base de données)
     */
    public boolean creerCompte(Compte compte) {
        if (compte == null) {
            throw new IllegalArgumentException("Le compte à créer ne peut pas être nul.");
        }
        if (compte.getNumero() == null || compte.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro de compte ne peut pas être vide.");
        }
        if (compte.getClient() == null) {
            throw new IllegalArgumentException("Le compte doit être rattaché à un client.");
        }

        try {
            return compteDAO.create(compte);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du compte : " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour un compte existant.
     *
     * @throws CompteIntrouvableException si aucun compte ne porte cet ID
     * @throws IllegalArgumentException   si le compte est null
     */
    public boolean mettreAJourCompte(Compte compte) {
        if (compte == null) {
            throw new IllegalArgumentException("Le compte à mettre à jour ne peut pas être nul.");
        }

        try {
            exigerCompteParId(compte.getId());
            return compteDAO.update(compte);
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du compte : " + e.getMessage(), e);
        }
    }

    /**
     * Supprime le compte correspondant au numéro donné.
     *
     * @throws CompteIntrouvableException si aucun compte ne porte ce numéro
     * @throws IllegalArgumentException   si le numéro est vide
     */
    public boolean supprimerCompte(String compteNum) {
        if (compteNum == null || compteNum.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro du compte à supprimer ne peut pas être vide.");
        }

        try {
            Compte compte = exigerCompteParNumero(compteNum);
            return compteDAO.delete(compte.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du compte : " + e.getMessage(), e);
        }
    }

    /**
     * Liste les comptes rattachés à un client.
     *
     * @throws RuntimeException en cas d'erreur technique (base de données)
     */
    public List<Compte> trouverComptesParClient(String clientId) {
        try {
            return compteDAO.findAll().stream()
                    .filter(c -> c.getClient() != null && String.valueOf(c.getClient().getId()).equals(clientId))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des comptes par client : " + e.getMessage(), e);
        }
    }

    /**
     * Recherche un compte par son numéro.
     *
     * @return le compte, ou Optional.empty() si aucun compte ne porte ce numéro
     * @throws RuntimeException en cas d'erreur technique (base de données)
     */
    public Optional<Compte> trouverCompteParNumero(String numero) {
        try {
            return compteDAO.findAll().stream()
                    .filter(c -> c.getNumero() != null && c.getNumero().equals(numero))
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par numéro de compte : " + e.getMessage(), e);
        }
    }

    /**
     * Recherche le compte au solde le plus élevé.
     */
    public Optional<Compte> trouverCompteSoldeMaximum() {
        try {
            List<Compte> comptes = compteDAO.findAll();
            return comptes.stream()
                    .max(Comparator.comparingDouble(Compte::getSolde));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du solde maximum : " + e.getMessage(), e);
        }
    }

    /**
     * Recherche le compte au solde le plus faible.
     */
    public Optional<Compte> trouverCompteSoldeMinimum() {
        try {
            List<Compte> comptes = compteDAO.findAll();
            return comptes.stream()
                    .min(Comparator.comparingDouble(Compte::getSolde));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du solde minimum : " + e.getMessage(), e);
        }
    }

    /**
     * Vérifie qu'un compte existe (par son ID) puis le retourne.
     *
     * @throws CompteIntrouvableException si aucun compte ne porte cet ID
     */
    private Compte exigerCompteParId(String id) throws SQLException {
        return compteDAO.findById(id)
                .orElseThrow(() -> new CompteIntrouvableException("Compte introuvable avec l'ID " + id));
    }

    /**
     * Vérifie qu'un compte existe (par son numéro) puis le retourne.
     *
     * @throws CompteIntrouvableException si aucun compte ne porte ce numéro
     */
    private Compte exigerCompteParNumero(String numero) throws SQLException {
        return compteDAO.findByNumero(numero)
                .orElseThrow(() -> new CompteIntrouvableException("Compte introuvable avec le numéro " + numero));
    }
}