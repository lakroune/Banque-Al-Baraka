package DAOS;

import models.Compte;
import models.Transaction;
import models.TypeTransaction;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAO implements DAO<Transaction> {

    private final CompteDAO compteDAO = new CompteDAO();

    public boolean create(Transaction obj, Connection connection) throws SQLException {
        String sql = "INSERT INTO transactions (id, date_transaction, montant, type, lieu, compte_source_id, compte_destination_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getId());
            pstmt.setObject(2, obj.getDate());
            pstmt.setDouble(3, obj.getMontant());
            pstmt.setString(4, obj.getType().name());
            pstmt.setString(5, obj.getLieu());

            if (obj.getCompteSource() != null) {
                pstmt.setString(6, obj.getCompteSource().getId());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }

            if (obj.getCompteDestination() != null) {
                pstmt.setString(7, obj.getCompteDestination().getId());
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Transaction> findById(String id) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet resultat = pstmt.executeQuery()) {
                if (resultat.next()) {
                    Transaction transaction = mapResultSetToTransaction(resultat);
                    return Optional.of(transaction);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection connection = DatabaseConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet resultat = stmt.executeQuery(sql)) {

            while (resultat.next()) {
                transactions.add(mapResultSetToTransaction(resultat));
            }
        }
        return transactions;
    }

    @Override
    public boolean update(Transaction obj) throws SQLException {
        String sql = "UPDATE transactions SET date_transaction = ?, montant = ?, type = ?, lieu = ?, compte_source_id = ?, compte_destination_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setObject(1, obj.getDate());
            pstmt.setDouble(2, obj.getMontant());
            pstmt.setString(3, obj.getType().name());
            pstmt.setString(4, obj.getLieu());

            if (obj.getCompteSource() != null) {
                pstmt.setString(5, obj.getCompteSource().getId());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            if (obj.getCompteDestination() != null) {
                pstmt.setString(6, obj.getCompteDestination().getId());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }

            pstmt.setString(7, obj.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Transaction mapResultSetToTransaction(ResultSet resultat) throws SQLException {
        String id = resultat.getString("id");
        LocalDate date = resultat.getObject("date_transaction", LocalDate.class);
        double montant = resultat.getDouble("montant");
        String typeStr = resultat.getString("type");
        TypeTransaction type = typeStr != null ? TypeTransaction.valueOf(typeStr) : null;
        String lieu = resultat.getString("lieu");

        String compteSourceId = resultat.getString("compte_source_id");
        String compteDestId = resultat.getString("compte_destination_id");

        Compte compteSource = null;
        if (compteSourceId != null) {
            Optional<Compte> srcOpt = compteDAO.findById(compteSourceId);
            if (srcOpt.isPresent()) {
                compteSource = srcOpt.get();
            }
        }

        Compte compteDestination = null;
        if (compteDestId != null) {
            Optional<Compte> destOpt = compteDAO.findById(compteDestId);
            if (destOpt.isPresent()) {
                compteDestination = destOpt.get();
            }
        }

        return new Transaction(id, date, montant, type, lieu, compteSource, compteDestination);
    }

    public List<Transaction> findByCompteTrieesParDate(String numeroCompte) {
        List<Transaction> transactions = new ArrayList<>();

        String sql = "SELECT DISTINCT t.* FROM transactions t " +
                "LEFT JOIN comptes cs ON t.compte_source_id = cs.id " +
                "LEFT JOIN comptes cd ON t.compte_destination_id = cd.id " +
                "WHERE cs.numero = ? OR cd.numero = ? " +
                "ORDER BY t.date_transaction DESC";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, numeroCompte);
            pstmt.setString(2, numeroCompte);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = mapResultSetToTransaction(rs);
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}