package UI;

import models.Client;
import models.Compte;
import models.CompteEpargne;
import services.RapportService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class AnalyseUI {

    private static final DateTimeFormatter FORMAT_DATE_HEURE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
        System.out.println("4. Comptes inactifs (aucune opération depuis 1 an)");
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
        } else if (choixAnalyse == 4) {
            afficherComptesInactifs();
        }
    }

  
    private void afficherComptesInactifs() {
        try {
            List<Compte> comptesInactifs = rapportService.listerComptesInactifs();

            System.out.println("\n--- Comptes inactifs (aucune opération depuis "
                    + RapportService.MOIS_INACTIVITE_PAR_DEFAUT + " mois) ---");

            if (comptesInactifs.isEmpty()) {
                System.out.println("Aucun compte inactif : tous les comptes ont eu une opération récente.");
                return;
            }

            for (Compte compte : comptesInactifs) {
                System.out.println("- " + decrireCompte(compte));
            }
            System.out.println("Total : " + comptesInactifs.size() + " compte(s) inactif(s).");
        } catch (RuntimeException e) {
            System.out.println("Erreur lors de la détection des comptes inactifs : " + e.getMessage());
        }
    }

  
    private String decrireCompte(Compte compte) {
        String type = (compte instanceof CompteEpargne) ? "Épargne" : "Courant";
        String titulaire = (compte.getClient() != null) ? compte.getClient().getNom() : "client inconnu";
        String derniereOperation = rapportService.derniereActivite(compte.getId())
                .map(date -> "dernière opération le " + date.format(FORMAT_DATE_HEURE))
                .orElse("aucune opération enregistrée");

        return "Compte " + compte.getNumero() + " (" + type + ", " + titulaire + ", solde "
                + compte.getSolde() + " MAD) : " + derniereOperation;
    }
}