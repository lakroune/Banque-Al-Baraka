package UI;

import models.Compte;
import models.TypeTransaction;
import services.CompteService;
import services.TransactionService;
import exceptions.CompteIntrouvableException;
import exceptions.MontantInvalideException;
import exceptions.SoldeInsuffisantException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

public class TransactionUI {

    private final Scanner scanner;
    private final TransactionService transactionService = new TransactionService();
    private final CompteService compteService = new CompteService();

    public TransactionUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executer() {
        System.out.println("\n--- Enregistrer une transaction ---");
        System.out.println("1. Versement");
        System.out.println("2. Retrait");
        System.out.println("3. Virement");
        System.out.print("Votre choix (1/2/3) : ");
        String choix = scanner.nextLine().trim();

        switch (choix) {
            case "1":
                saisirVersement();
                break;
            case "2":
                saisirRetrait();
                break;
            case "3":
                saisirVirement();
                break;
            default:
                System.out.println("Erreur : Choix invalide.");
        }
    }

    private void saisirVersement() {
        System.out.println("\n--- Nouveau Versement ---");
        Compte compteDestination = saisirCompte("Numero du compte destinataire : ");
        if (compteDestination == null) return;

        double montant = saisirMontant();
        String lieu = saisirLieu();

        try {
            boolean success = transactionService.effectuerVersement(compteDestination, montant, lieu, LocalDate.now());
            afficherResultat(success, TypeTransaction.VERSEMENT);
        } catch (MontantInvalideException | SoldeInsuffisantException | CompteIntrouvableException e) {
            System.out.println("\nOpération refusée : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("\nErreur technique : " + e.getMessage());
        }
    }

    private void saisirRetrait() {
        System.out.println("\n--- Nouveau Retrait ---");
        Compte compteSource = saisirCompte("Numero du compte source : ");
        if (compteSource == null) return;

        double montant = saisirMontant();
        String lieu = saisirLieu();

        try {
            boolean success = transactionService.effectuerRetrait(compteSource, montant, lieu, LocalDate.now());
            afficherResultat(success, TypeTransaction.RETRAIT);
        } catch (MontantInvalideException | SoldeInsuffisantException | CompteIntrouvableException e) {
            System.out.println("\nOpération refusée : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("\nErreur technique : " + e.getMessage());
        }
    }

    private void saisirVirement() {
        System.out.println("\n--- Nouveau Virement ---");
        Compte compteSource = saisirCompte("Numero du compte source : ");
        if (compteSource == null) return;

        Compte compteDestination = saisirCompte("Numero du compte destinataire : ");
        if (compteDestination == null) return;

        if (compteSource.getNumero().equals(compteDestination.getNumero())) {
            System.out.println("Erreur : Les comptes source et destination doivent être différents.");
            return;
        }

        double montant = saisirMontant();
        String lieu = saisirLieu();

        try {
            boolean success = transactionService.effectuerVirement(compteSource, compteDestination, montant, lieu, LocalDate.now());
            afficherResultat(success, TypeTransaction.VIREMENT);
        } catch (MontantInvalideException | SoldeInsuffisantException | CompteIntrouvableException e) {
            System.out.println("\nOpération refusée : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("\nErreur technique : " + e.getMessage());
        }
    }

    private Compte saisirCompte(String message) {
        System.out.print(message);
        String numero = scanner.nextLine();
        Optional<Compte> optionalCompte;
        try {
            optionalCompte = compteService.trouverCompteParNumero(numero);
        } catch (RuntimeException e) {
            System.out.println("Erreur technique : " + e.getMessage());
            return null;
        }

        if (optionalCompte.isEmpty()) {
            System.out.println("Erreur : Aucun compte trouvé avec le numéro " + numero);
            return null;
        }
        return optionalCompte.get();
    }

    private double saisirMontant() {
        System.out.print("Montant : ");
        while (!scanner.hasNextDouble()) {
            System.out.println("Veuillez entrer un montant valide.");
            scanner.next();
            System.out.print("Montant : ");
        }
        double montant = scanner.nextDouble();
        scanner.nextLine();
        return montant;
    }

    private String saisirLieu() {
        System.out.print("Lieu de la transaction (ex: Maroc) : ");
        return scanner.nextLine();
    }

    private void afficherResultat(boolean success, TypeTransaction type) {
        if (success) {
            System.out.println("Transaction (" + type + ") enregistrée avec succès !");
        } else {
            System.out.println("Erreur lors de l'enregistrement (solde insuffisant ou problème technique).");
        }
    }
}