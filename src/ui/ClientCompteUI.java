package UI;

import models.Client;
import models.CompteCourant;
import models.CompteEpargne;
import services.ClientService;
import services.CompteService;

import java.util.Scanner;

public class ClientCompteUI {

    private final Scanner scanner;
    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();

    public ClientCompteUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executer() {
        System.out.println("\n--- Création d'un client et de ses comptes ---");
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
                System.out.print("ID du compte : ");
                String idCompte = scanner.nextLine();
                System.out.print("Numéro de compte : ");
                String numero = scanner.nextLine();

                if (typeCompte == 1) {
                    System.out.print("Découvert autorisé : ");
                    double decouvert = scanner.nextDouble();
                    scanner.nextLine();
                    CompteCourant cc = new CompteCourant(idCompte, numero, 0.0, decouvert);
                    cc.setClient(nouveauClient);
                    compteService.creerCompte(cc);
                } else {
                    System.out.print("Taux d'intérêt : ");
                    double taux = scanner.nextDouble();
                    scanner.nextLine();
                    CompteEpargne ce = new CompteEpargne(idCompte, numero, 0.0, taux);
                    ce.setClient(nouveauClient);
                    compteService.creerCompte(ce);
                }
                System.out.println("Compte créé avec succès !");
            }
        } else {
            System.out.println("Erreur lors de la création du client.");
        }
    }

}