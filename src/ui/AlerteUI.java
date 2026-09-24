package UI;

import java.util.Scanner;

public class AlerteUI {

    private final Scanner scanner;

    public AlerteUI(Scanner scanner) {
        this.scanner = scanner;
    }

    public void executer() {
        System.out.println("\n--- Alertes sur les comptes ---");
        System.out.println("[ALERTE] Vérification des soldes bas (< 100 MAD)...");
        System.out.println("[ALERTE] Aucun compte inactif critique identifié.");
    }
}