package UI;

import models.Transaction;
import services.TransactionService;

import java.time.LocalDateTime;
import java.util.Scanner;

public class TransactionUI {

    private final Scanner scanner;
    private final TransactionService transactionService = new TransactionService();

    public TransactionUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executer() {
        System.out.println("\n--- Enregistrer une transaction ---");
        System.out.print("ID du compte : ");
        String compteId = scanner.nextLine();
        System.out.print("Montant : ");
        double montant = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Type (VERSEMENT / RETRAIT / VIREMENT) : ");
        String type = scanner.nextLine();
        System.out.print("Lieu de la transaction (ex: Maroc) : ");
        String lieu = scanner.nextLine();

        Transaction tx = new Transaction();
        tx.setCompte(compteId);
        tx.setMontant(montant);
        tx.setType(type);
        tx.setDate(LocalDateTime.now());
        tx.setLieu(lieu);

        System.out.println("Transaction enregistrée avec succès pour le compte ID: " + compteId);
    }
}