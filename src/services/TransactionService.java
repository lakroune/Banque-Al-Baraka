package services;

import DAOS.TransactionDAO;

public class TransactionService {

    private final TransactionDAO transactionDAO = new TransactionDAO();

    // public List<Transaction> listerParCompteTrieesParDate(String compteId) {
    // try {
    // return transactionDAO.findByCompteId(compteId).stream()
    // .sorted(Comparator.comparing(Transaction::getDate).reversed())
    // .collect(Collectors.toList());
    // } catch (Exception e) {
    // System.err.println("Erreur lors de la récupération des transactions du compte
    // : " + e.getMessage());
    // }
    // return List.of();
    // }

}