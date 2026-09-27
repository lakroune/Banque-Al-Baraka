package services;

import models.Client;
import models.Compte;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import DAOS.TransactionDAO;

public class RapportService {

    public static final int MOIS_INACTIVITE_PAR_DEFAUT = 12;

    private final ClientService clientService = new ClientService();
    private final CompteService compteService = new CompteService();
    private final TransactionDAO transactionDAO = new TransactionDAO();

   
    public List<Client> genererTop5ClientsParSolde() {
        List<Client> clients = clientService.listerTousLesClients();
 
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

    public List<Compte> listerComptesInactifs() {
        return listerComptesInactifs(MOIS_INACTIVITE_PAR_DEFAUT);
    }

    public List<Compte> listerComptesInactifs(int moisInactivite) {
        if (moisInactivite <= 0) {
            throw new IllegalArgumentException("Le nombre de mois d'inactivité doit être supérieur à zéro.");
        }

        LocalDateTime seuil = LocalDateTime.now().minusMonths(moisInactivite);
        Map<String, LocalDateTime> dernieresActivites = chargerDernieresActivites();

        return compteService.listerTousLesComptes().stream()
                .filter(compte -> estInactif(dernieresActivites.get(compte.getId()), seuil))
                .sorted(Comparator.comparing(Compte::getNumero))
                .toList();
    }

    public Optional<LocalDateTime> derniereActivite(String compteId) {
        if (compteId == null || compteId.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(chargerDernieresActivites().get(compteId));
    }

    private boolean estInactif(LocalDateTime derniereActivite, LocalDateTime seuil) {
        return derniereActivite == null || derniereActivite.isBefore(seuil);
    }

    private Map<String, LocalDateTime> chargerDernieresActivites() {
        try {
            return transactionDAO.findDernieresActivitesParCompte();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des dernières activités : " + e.getMessage(), e);
        }
    }
}