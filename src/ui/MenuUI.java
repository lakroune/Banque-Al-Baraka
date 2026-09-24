package UI;

import java.util.Scanner;

public class MenuUI {

    private final Scanner scanner = new Scanner(System.in);
    
    private final ClientCompteUI clientCompteUI = new ClientCompteUI(scanner);
    private final TransactionUI transactionUI = new TransactionUI(scanner);
    private final HistoriqueUI historiqueUI = new HistoriqueUI(scanner);
    private final AnalyseUI analyseUI = new AnalyseUI(scanner);
    private final AlerteUI alerteUI = new AlerteUI(scanner);

    public void demarrer() {
        int choix;

        do {
            System.out.println("\n========================================");
            System.out.println("   GESTION BANCAIRE - BANQUE AL BARAKA  ");
            System.out.println("========================================");
            System.out.println("1. Créer un client et ses comptes");
            System.out.println("2. Enregistrer une transaction (Versement/Retrait/Virement)");
            System.out.println("3. Consulter l'historique des transactions d'un compte");
            System.out.println("4. Lancer une analyse (Top 5, Rapport, Inactifs, Suspects)");
            System.out.println("5. Recevoir des alertes sur les comptes");
            System.out.println("0. Quitter");
            System.out.print("Choisissez une option : ");

            while (!scanner.hasNextInt()) {
                System.out.print("Veuillez entrer un nombre valide : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine();

            switch (choix) {
                case 1:
                    clientCompteUI.executer();
                    break;
                case 2:
                    transactionUI.executer();
                    break;
                case 3:
                    historiqueUI.executer();
                    break;
                case 4:
                    analyseUI.executer();
                    break;
                case 5:
                    alerteUI.executer();
                    break;
                case 0:
                    System.out.println("Fermeture de l'application. Au revoir !");
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }

        } while (choix != 0);
    }
}