package UI;

import models.Transaction;
import services.TransactionService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class HistoriqueUI {

    private static final DateTimeFormatter FORMAT_DATE_HEURE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final Scanner scanner;
    private final TransactionService transactionService = new TransactionService();

    public HistoriqueUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executer() {
        System.out.println("\n--- Consulter l'historique des transactions ---");
        System.out.print("Entrez numero du compte : ");
        String NumCompteHist = scanner.nextLine();
        List<Transaction> historique = transactionService.listerParCompteTrieesParDate(NumCompteHist);

        if (historique.isEmpty()) {
            System.out.println("Aucune transaction trouvée pour ce compte.");
        } else {
            System.out.println("Historique trié par date et heure :");
            for (Transaction t : historique) {
                String dateHeure = t.getDate() != null ? t.getDate().format(FORMAT_DATE_HEURE) : "-";
                System.out.println("- [" + dateHeure + "] "
                        + t.getType() + " : "
                        + t.getMontant()
                        + " MAD (Lieu: "
                        + t.getLieu() + ")");
            }
        }
    }
}