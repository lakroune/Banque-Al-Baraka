package services;

import models.Compte;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import DAOS.CompteDAO;

public class CompteService {

    private final CompteDAO compteDAO = new CompteDAO();

    public boolean creerCompte(Compte compte) {
        try {
            if (compte.getNumero() == null || compte.getNumero().trim().isEmpty()) {
                System.out.println("Erreur : Le numéro de compte ne peut pas être vide.");
                return false;
            }
            if (compte.getSolde() < 0) {

                System.out.println("Attention : Création d'un compte avec un solde négatif.");
            }
            return compteDAO.create(compte);
        } catch (Exception e) {
            System.out.println("Erreur lors de la création du compte : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean mettreAJourCompte(Compte compte) {
        try {
            Optional<Compte> existingCompte = compteDAO.findById(compte.getId());
            if (existingCompte.isEmpty()) {
                System.out.println("Erreur : Compte introuvable avec l'ID " + compte.getId());
                return false;
            }
            return compteDAO.update(compte);
        } catch (SQLException e) {
            System.out.println("Erreur lors de la mise à jour du compte : " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Compte> trouverComptesParClient(String clientId) {
        try {
            // Suppose que votre CompteDAO ou une méthode spécifique filtre par client
            return compteDAO.findAll().stream()
                    .filter(c -> c.getClient() != null && String.valueOf(c.getClient().getId()).equals(clientId))
                    .toList();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche des comptes par client : " + e.getMessage());
            e.printStackTrace();
        }
        return List.of();
    }

    public Optional<Compte> trouverCompteParNumero(String numero) {
        try {
            return compteDAO.findAll().stream()
                    .filter(c -> c.getNumero() != null && c.getNumero().equals(numero))
                    .findFirst();
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche par numéro de compte : " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Compte> trouverCompteSoldeMaximum() {
        try {
            List<Compte> comptes = compteDAO.findAll();
            return comptes.stream()
                    .max(Comparator.comparingDouble(Compte::getSolde));
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du solde maximum : " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Compte> trouverCompteSoldeMinimum() {
        try {
            List<Compte> comptes = compteDAO.findAll();
            return comptes.stream()
                    .min(Comparator.comparingDouble(Compte::getSolde));
        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche du solde minimum : " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }
}