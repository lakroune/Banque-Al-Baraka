package services;

import models.Client;
import models.Compte;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RapportService {

    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();

    /**
     * Classe les clients par solde total décroissant (somme des soldes de tous leurs comptes)
     * et retourne les cinq premiers.
     */
    public List<Client> genererTop5ClientsParSolde() {
        List<Client> clients = clientService.listerTousLesClients();

        // Les soldes sont calculés une seule fois par client (et non à chaque comparaison du tri).
        Map<String, Double> soldesParClient = new HashMap<>();
        for (Client client : clients) {
            soldesParClient.put(client.getId(), calculerSoldeTotal(client));
        }

        return clients.stream()
                .sorted(Comparator
                        .comparingDouble((Client client) -> soldesParClient.getOrDefault(client.getId(), 0.0))
                        .reversed())
                .limit(5)
                .toList();
    }

    /**
     * Solde total d'un client : somme des soldes de tous ses comptes.
     */
    public double calculerSoldeTotal(Client client) {
        if (client == null) {
            return 0.0;
        }

        return compteService.trouverComptesParClient(client.getId()).stream()
                .mapToDouble(Compte::getSolde)
                .sum();
    }
}