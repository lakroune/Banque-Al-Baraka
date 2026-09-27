package services;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import DAOS.CompteDAO;
import DAOS.TransactionDAO;
import exceptions.CompteIntrouvableException;
import exceptions.MontantInvalideException;
import exceptions.SoldeInsuffisantException;
import models.Compte;
import models.CompteCourant;
import models.Transaction;
import models.TypeTransaction;
import util.DatabaseConnection;

public class TransactionService {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CompteDAO compteDAO = new CompteDAO();

    public boolean effectuerVersement(Compte compteDestination, double montant, String lieu, LocalDate date) {
        validerMontantEtCompte(compteDestination, montant);

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                compteDestination.setSolde(compteDestination.getSolde() + montant);

                if (!compteDAO.update(compteDestination)) {
                    throw new SQLException("Échec de la mise à jour du solde du compte destination.");
                }

                Transaction tx = new Transaction(date, montant, TypeTransaction.VERSEMENT, lieu, null,
                        compteDestination);
                if (!transactionDAO.create(tx)) {
                    throw new SQLException("Échec de l'enregistrement du versement.");
                }

                connection.commit();
                return true;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean effectuerRetrait(Compte compteSource, double montant, String lieu, LocalDate date) {
        validerMontantEtCompte(compteSource, montant);
        verifierSoldeDisponible(compteSource, montant);

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                compteSource.setSolde(compteSource.getSolde() - montant);

                if (!compteDAO.update(compteSource)) {
                    throw new SQLException("Échec de la mise à jour du solde du compte source.");
                }

                Transaction tx = new Transaction(date, montant, TypeTransaction.RETRAIT, lieu, compteSource, null);
                if (!transactionDAO.create(tx)) {
                    throw new SQLException("Échec de l'enregistrement du retrait.");
                }

                connection.commit();
                return true;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean effectuerVirement(Compte compteSource, Compte compteDestination, double montant, String lieu,
            LocalDate date) {
        validerMontantEtCompte(compteSource, montant);
        if (compteDestination == null) {
            throw new CompteIntrouvableException("Le compte destination ne peut pas être nul.");
        }
        verifierSoldeDisponible(compteSource, montant);

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                compteSource.setSolde(compteSource.getSolde() - montant);
                compteDestination.setSolde(compteDestination.getSolde() + montant);

                if (!compteDAO.update(compteSource)) {
                    throw new SQLException("Échec de la mise à jour du compte source.");
                }

                if (!compteDAO.update(compteDestination)) {
                    throw new SQLException("Échec de la mise à jour du compte destination.");
                }

                Transaction tx = new Transaction(date, montant, TypeTransaction.VIREMENT, lieu, compteSource,
                        compteDestination);
                if (!transactionDAO.create(tx)) {
                    throw new SQLException("Échec de l'enregistrement du virement.");
                }

                connection.commit();
                return true;
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Transaction> listerParCompteTrieesParDate(String numeroCompte) {
        return transactionDAO.findByCompteTrieesParDate(numeroCompte);
    }

    private void validerMontantEtCompte(Compte compte, double montant) {
        if (compte == null) {
            throw new CompteIntrouvableException("Le compte spécifié ne peut pas être nul.");
        }
        if (montant <= 0) {
            throw new MontantInvalideException("Le montant de la transaction doit être supérieur à zéro.");
        }
    }

    private void verifierSoldeDisponible(Compte compte, double montant) {
        double decouvert = (compte instanceof CompteCourant)
                ? ((CompteCourant) compte).getDecouvertAutorise()
                : 0.0;

        if ((compte.getSolde() + decouvert) < montant) {
            throw new SoldeInsuffisantException("Solde insuffisant pour effectuer cette opération.");
        }
    }
}