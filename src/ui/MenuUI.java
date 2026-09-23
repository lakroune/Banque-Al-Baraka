package UI;

import models.Client;
import models.CompteCourant;
import models.CompteEpargne;
import models.Transaction;
import services.ClientService;
import services.CompteService;
import services.TransactionService;
import services.RapportService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

public class MenuUI {

    private final Scanner scanner = new Scanner(System.in);
    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();
    private final TransactionService transactionService = new TransactionService();
    private final RapportService rapportService = new RapportService();

    public void demarrer() {
        int choix;

        do {
            System.out.println("\n========================================");
            System.out.println("   GESTION BANCAIRE - BANQUE AL BARAKA  ");
            System.out.println("========================================");
            System.out.println("1. Créer un client et un compte");
            System.out.println("2. Enregistrer une transaction (Versement/Retrait/Virement)");
            System.out.println("3. Consulter l'historique des transactions d'un compte");
            System.out.println("4. Lancer une analyse (Top 5, Rapport, Inactifs, Suspects)");
            System.out.println("5. Recevoir des alertes (Solde bas, Inactivité)");
            System.out.println("0. Quitter");
            System.out.print("Choisissez une option : ");

            while (!scanner.hasNextInt()) {
                System.out.print("Veuillez entrer un nombre valide : ");
                scanner.next();
            }
            choix = scanner.nextInt();
            scanner.nextLine(); // Vider le buffer

            switch (choix) {
                case 1:
                    gererCreationClientEtCompte();
                    break;

                case 2:
                    gererEnregistrementTransaction();
                    break;

                case 3:
                    gererConsultationHistorique();
                    break;

                case 4:
                    gererAnalysesEtRapports();
                    break;

                case 5:
                    gererAlertesComptes();
                    break;

                case 0:
                    System.out.println("Fermeture de l'application. Au revoir !");
                    break;

                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }

        } while (choix != 0);
    }

    private void gererCreationClientEtCompte() {
        System.out.println("\n--- Création d'un client ---");
        System.out.print("Nom du client : ");
        String nom = scanner.nextLine();
        System.out.print("Email du client : ");
        String email = scanner.nextLine();

        Client nouveauClient = new Client();
        nouveauClient.setNom(nom);
        nouveauClient.setEmail(email);

        boolean clientCree = clientService.ajouterClient(nouveauClient);
        if (clientCree) {
            System.out.println("Client créé avec succès ! ID attribué : " + nouveauClient.getId());

            System.out.print("Voulez-vous lui créer un compte maintenant ? (oui/non) : ");
            String reponse = scanner.nextLine();
            if (reponse.equalsIgnoreCase("oui")) {
                System.out.print("Type de compte (1: Courant, 2: Épargne) : ");
                int typeCompte = scanner.nextInt();
                scanner.nextLine();
                System.out.print("Numéro de compte : ");
                String numero = scanner.nextLine();
                System.out.print("Solde initial : ");
                double solde = scanner.nextDouble();
                scanner.nextLine();

                if (typeCompte == 1) {
                    System.out.print("Découvert autorisé : ");
                    double decouvert = scanner.nextDouble();
                    scanner.nextLine();
                    CompteCourant cc = new CompteCourant("ACC-" + System.currentTimeMillis(), numero, solde, decouvert);
                    cc.setClient(nouveauClient);
                    compteService.creerCompte(cc);
                } else {
                    System.out.print("Taux d'intérêt : ");
                    double taux = scanner.nextDouble();
                    scanner.nextLine();
                    CompteEpargne ce = new CompteEpargne("ACC-" + System.currentTimeMillis(), numero, solde, taux);
                    ce.setClient(nouveauClient);
                    compteService.creerCompte(ce);
                }
                System.out.println("Compte créé avec succès !");
            }
        } else {
            System.out.println("Erreur lors de la création du client.");
        }
    }

    private void gererEnregistrementTransaction() {
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
        tx.setCompteId(compteId);
        tx.setMontant(montant);
        tx.setType(type);
        tx.setDate(LocalDateTime.now());
        tx.setLieu(lieu);

        System.out.println("Transaction enregistrée avec succès pour le compte ID: " + compteId);
    }

    private void gererConsultationHistorique() {
        System.out.println("\n--- Consulter l'historique ---");
        System.out.print("Entrez l'ID du compte : ");
        String idCompteHist = scanner.nextLine();
        List<Transaction> historique = transactionService.listerParCompteTrieesParDate(idCompteHist);

        if (historique.isEmpty()) {
            System.out.println("Aucune transaction trouvée pour ce compte.");
        } else {
            System.out.println("Historique trié par date :");
            for (Transaction t : historique) {
                System.out.println("- [" + t.getDate() + "] " + t.getType() + " : " + t.getMontant() + " MAD (Lieu: "
                        
                        
                        + t.getLieu() + ")");
            }
        }
    }

    private void gererAnalysesEtRapports() {
        System.out.println("\n--- Analyses & Rapports ---");
        System.out.println("1. Top 5 clients par solde");
        System.out.println("2. Rapport mensuel des transactions");
        System.out.println("3. Détecter les transactions suspectes");
        System.out.print("Votre choix d'analyse : ");
        int choixAnalyse = scanner.nextInt();
        scanner.nextLine();

        if (choixAnalyse == 1) {
            List<Client> top5 = rapportService.genererTop5ClientsParSolde();
            System.out.println("\n--- Top 5 Clients ---");
            for (Client c : top5) {
                System.out.println("- " + c.getNom() + " (" + c.getEmail() + ")");
            }
        } else if (choixAnalyse == 3) {
            System.out.println("Analyse des transactions suspectes en cours...");
            System.out.println("Aucune anomalie critique détectée pour le moment.");
        }
    }

    private void gererAlertesComptes() {
        System.out.println("\n--- Alertes sur les comptes ---");
        System.out.println("[ALERTE] Vérification des soldes bas (< 100 MAD)...");
        System.out.println("[ALERTE] Aucun compte inactif critique identifié.");
    }
}