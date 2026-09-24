package UI;

import models.Compte;
import models.CompteCourant;
import models.Transaction;
import models.TypeTransaction;
import services.CompteService;
import services.TransactionService;

import java.time.LocalDate;
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
        System.out.print("Numero du compte : ");
        String compteNum = scanner.nextLine();



        Compte compte = compteService.trouverCompteParNumero(compteNum).orElse(null);
        if (compte == null) {
            compte = new CompteCourant();
            compte.setNumero(compteNum); 
        }

        System.out.print("Montant : ");
        double montant = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Type (VERSEMENT / RETRAIT / VIREMENT) : ");
        String typeStr = scanner.nextLine();
        System.out.print("Lieu de la transaction (ex: Maroc) : ");
        String lieu = scanner.nextLine();

        Transaction tx = new Transaction();
        tx.setCompte(compte);
        tx.setMontant(montant);     
        tx.setType(TypeTransaction.valueOf(typeStr.toUpperCase()));
        tx.setDate(LocalDate.now());
        tx.setLieu(lieu);

        transactionService.enregistrerTransaction(tx);
        System.out.println("Transaction enregistrée avec succès pour le compte N°: " + compteNum);

    }

}