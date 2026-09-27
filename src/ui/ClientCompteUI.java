package UI;

import models.Client;
import models.Compte;
import models.CompteCourant;
import models.CompteEpargne;
import services.ClientService;
import services.CompteService;
import exceptions.ClientIntrouvableException;
import exceptions.CompteIntrouvableException;

import java.util.Optional;
import java.util.Scanner;

public class ClientCompteUI {

    private final Scanner scanner;
    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();

    public ClientCompteUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executer() {
        int choix;
        do {
            System.out.println("\n========================================");
            System.out.println("   GESTION DES CLIENTS ET DES COMPTES  ");
            System.out.println("========================================");
            System.out.println("1. Créer un client");
            System.out.println("2. Créer un compte pour un client");
            System.out.println("3. Afficher un client (par ID ou email)");
            System.out.println("4. Afficher un compte (par numéro)");
            System.out.println("5. Supprimer un client");
            System.out.println("6. Supprimer un compte");
            System.out.println("0. Retour au menu principal");
            System.out.print("Choisissez une option : ");

            choix = scanner.nextInt();
            scanner.nextLine(); // Vider le buffer

            switch (choix) {
                case 1:
                    creerClient();
                    break;
                case 2:
                    creerCompte();
                    break;
                case 3:
                    afficherClient();
                    break;
                case 4:
                    afficherCompte();
                    break;
                case 5:
                    supprimerClient();
                    break;
                case 6:
                    supprimerCompte();
                    break;
                case 0:
                    System.out.println("Retour au menu principal...");
                    break;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        } while (choix != 0);
    }

    private void creerClient() {
        System.out.println("\n--- Création d'un client ---");
        System.out.print("Nom du client : ");
        String nom = scanner.nextLine();
        System.out.print("Email du client : ");
        String email = scanner.nextLine();

        Client nouveauClient = new Client();
        nouveauClient.setNom(nom);
        nouveauClient.setEmail(email);

        try {
            boolean clientCree = clientService.ajouterClient(nouveauClient);
            if (clientCree) {
                System.out.println("Client créé avec succès ! ID attribué : " + nouveauClient.getId());
            } else {
                System.out.println("Erreur lors de la création du client.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erreur technique : " + e.getMessage());
        }
    }

    private void creerCompte() {
        System.out.println("\n--- Création d'un compte ---");
        System.out.print("Entrez l'ID du client propriétaire : ");
        String clientId = scanner.nextLine();

        Optional<Client> client;
        try {
            client = clientService.trouverClientParId(clientId);
        } catch (RuntimeException e) {
            System.out.println("Erreur technique : " + e.getMessage());
            return;
        }

        if (!client.isPresent()) {
            System.out.println("Aucun client trouvé avec cet ID.");
            return;
        }

        System.out.print("Type de compte (1: Courant, 2: Épargne) : ");
        int typeCompte = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Numéro de compte : ");
        String numero = scanner.nextLine();

        try {
            if (typeCompte == 1) {
                System.out.print("Découvert autorisé : ");
                double decouvert = scanner.nextDouble();
                scanner.nextLine();

                CompteCourant cc = new CompteCourant(numero, 0.0, decouvert);
                cc.setClient(client.get());
                compteService.creerCompte(cc);
            } else {
                System.out.print("Taux d'intérêt : ");
                double taux = scanner.nextDouble();
                scanner.nextLine();

                CompteEpargne ce = new CompteEpargne(numero, 0.0, taux);
                ce.setClient(client.get());
                compteService.creerCompte(ce);
            }
            System.out.println("Compte créé avec succès !");
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erreur technique : " + e.getMessage());
        }
    }

    private void afficherClient() {
        System.out.println("\n--- Afficher un client ---");
        System.out.print("Entrez l'ID du client : ");
        String id = scanner.nextLine();

        try {
            Optional<Client> client = clientService.trouverClientParId(id);
            if (client.isPresent()) {
                System.out.println(client.get());
                System.out.println("Comptes associés : " + client.get().getCompteList().size());
            } else {
                System.out.println("Client introuvable.");
            }
        } catch (RuntimeException e) {
            System.out.println("Erreur technique : " + e.getMessage());
        }
    }

    private void afficherCompte() {
        System.out.println("\n--- Afficher un compte ---");
        System.out.print("Entrez le numéro du compte : ");
        String numero = scanner.nextLine();

        try {
            Optional<Compte> compte = compteService.trouverCompteParNumero(numero);
            if (compte.isPresent()) {
                System.out.println(compte.get());
            } else {
                System.out.println("Compte introuvable.");
            }
        } catch (RuntimeException e) {
            System.out.println("Erreur technique : " + e.getMessage());
        }
    }

    private void supprimerClient() {
        System.out.println("\n--- Supprimer un client ---");
        System.out.print("Entrez l'ID du client à supprimer : ");
        String id = scanner.nextLine();

        try {
            boolean supprime = clientService.supprimerClient(id);
            if (supprime) {
                System.out.println("Client supprimé avec succès (et ses comptes associés en cascade).");
            } else {
                System.out.println("Échec de la suppression du client.");
            }
        } catch (ClientIntrouvableException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erreur technique : " + e.getMessage());
        }
    }

    private void supprimerCompte() {
        System.out.println("\n--- Supprimer un compte ---");
        System.out.print("Entrez le numéro du compte à supprimer : ");
        String numero = scanner.nextLine();

        try {
            boolean supprime = compteService.supprimerCompte(numero);
            if (supprime) {
                System.out.println("Compte supprimé avec succès.");
            } else {
                System.out.println("Échec de la suppression du compte.");
            }
        } catch (CompteIntrouvableException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Erreur : " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("Erreur technique : " + e.getMessage());
        }

    }
}