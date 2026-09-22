package DAOS


import models.TypeTransaction;
import models.Compte;
import models.Transaction;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAO implements DAO<Transaction> {

    private final CompteDAO compteDAO = new CompteDAO();

    @Override
    public boolean create(Transaction obj) {
        String sql = "INSERT INTO transaction (id, date_transaction, montant, type, lieu, compte_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getId());
            pstmt.setObject(2, obj.getDate()); // Java 8 LocalDate supporté par JDBC moderne
            pstmt.setDouble(3, obj.getMontant());
            pstmt.setString(4, obj.getType().name());
            pstmt.setString(5, obj.getLieu());

            if (obj.getCompte() != null) {
                pstmt.setString(6, obj.getCompte().getId());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Transaction> findById(String id) throws SQLException {
        String sql = "SELECT * FROM transaction WHERE id = ?";
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
        String sql = "SELECT * FROM transaction";
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
        String sql = "UPDATE transaction SET date_transaction = ?, montant = ?, type = ?, lieu = ?, compte_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setObject(1, obj.getDate());
            pstmt.setDouble(2, obj.getMontant());
            pstmt.setString(3, obj.getType().name());
            pstmt.setString(4, obj.getLieu());

            if (obj.getCompte() != null) {
                pstmt.setString(5, obj.getCompte().getId());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            pstmt.setString(6, obj.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM transaction WHERE id = ?";
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
        String compteId = resultat.getString("compte_id");

        Compte compte = null;
        if (compteId != null) {
            Optional<Compte> compteOpt = compteDAO.findById(compteId);
            if (compteOpt.isPresent()) {
                compte = compteOpt.get();
            }
        }

        return new Transaction(id, date, montant, type, lieu, compte);
    }
}