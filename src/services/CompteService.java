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

    
    public List<Compte> listerTousLesComptes() {
        try {
            return compteDAO.findAll();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la liste des comptes : " + e.getMessage(), e);
        }
    }

   
    public List<Compte> trouverComptesParClient(String clientId) {
        try {
            return compteDAO.findAll().stream()
                    .filter(c -> c.getClient() != null && String.valueOf(c.getClient().getId()).equals(clientId))
                    .toList();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des comptes par client : " + e.getMessage(), e);
        }
    }

     
    public Optional<Compte> trouverCompteParNumero(String numero) {
        try {
            return compteDAO.findAll().stream()
                    .filter(c -> c.getNumero() != null && c.getNumero().equals(numero))
                    .findFirst();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par numéro de compte : " + e.getMessage(), e);
        }
    }

    
    public Optional<Compte> trouverCompteSoldeMaximum() {
        try {
            List<Compte> comptes = compteDAO.findAll();
            return comptes.stream()
                    .max(Comparator.comparingDouble(Compte::getSolde));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du solde maximum : " + e.getMessage(), e);
        }
    }

    
    public Optional<Compte> trouverCompteSoldeMinimum() {
        try {
            List<Compte> comptes = compteDAO.findAll();
            return comptes.stream()
                    .min(Comparator.comparingDouble(Compte::getSolde));
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du solde minimum : " + e.getMessage(), e);
        }
    }

   
    private Compte exigerCompteParId(String id) throws SQLException {
        return compteDAO.findById(id)
                .orElseThrow(() -> new CompteIntrouvableException("Compte introuvable avec l'ID " + id));
    }

    
    private Compte exigerCompteParNumero(String numero) throws SQLException {
        return compteDAO.findByNumero(numero)
                .orElseThrow(() -> new CompteIntrouvableException("Compte introuvable avec le numéro " + numero));
    }
}