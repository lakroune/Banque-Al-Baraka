package services;

import DAOS.TransactionDAO;
import models.Transaction;

/**
 * TransactionService
 */
public class TransactionService {

    private TransactionDAO transactionDAO = new TransactionDAO();

    public boolean enregistrerTransaction(Transaction transaction) {
        if (transaction == null || transaction.getMontant() <= 0) {
            return false;
        }
        return transactionDAO.create(transaction);
    }
}