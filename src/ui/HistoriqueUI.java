package UI;

import models.Transaction;
import services.TransactionService;

import java.util.List;
import java.util.Scanner;

public class HistoriqueUI {

    private final Scanner scanner;
    private final TransactionService transactionService = new TransactionService();

    public HistoriqueUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executer() {
        System.out.println("\n--- Consulter l'historique des transactions ---");
        System.out.print("Entrez l'ID du compte : ");
        String idCompteHist = scanner.nextLine();
        List<Transaction> historique = transactionService.listerParCompteTrieesParDate(idCompteHist);

        if (historique.isEmpty()) {
            System.out.println("Aucune transaction trouvée pour ce compte.");
        } else {
            System.out.println("Historique trié par date :");
            for (Transaction t : historique) {
                System.out.println("- [" + t.getDate() + "] " + t.getType() + " : " + t.getMontant() + " MAD (Lieu: " + t.getLieu() + ")");
            }
        }
    }
}