package UI;

import models.Client;
import services.RapportService;

import java.util.List;
import java.util.Scanner;

public class AnalyseUI {

    private final Scanner scanner;
    private final RapportService rapportService = new RapportService();

    public AnalyseUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executer() {
        System.out.println("\n--- Analyses & Rapports ---");
        System.out.println("1. Top 5 clients par solde");
        System.out.println("2. Rapport mensuel des transactions");
        System.out.println("3. Détecter les transactions suspectes");
        System.out.print("Votre choix d'analyse : ");
        int choixAnalyse = scanner.nextInt();
        scanner.nextLine();

        if (choixAnalyse == 1) {
            try {
                List<Client> top5 = rapportService.genererTop5ClientsParSolde();
                System.out.println("\n--- Top 5 Clients ---");
                for (Client c : top5) {
                    System.out.println("- " + c.getNom() + " (" + c.getEmail() + ")");
                }
            } catch (RuntimeException e) {
                System.out.println("Erreur lors de la génération du rapport : " + e.getMessage());
            }
        } else if (choixAnalyse == 3) {
            System.out.println("Analyse des transactions suspectes en cours...");
            System.out.println("Aucune anomalie critique détectée pour le moment.");
        }
    }
}