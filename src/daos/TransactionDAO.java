package DAOS;

import models.Compte;
import models.Transaction;
import models.TypeTransaction;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TransactionDAO implements DAO<Transaction> {

    private final CompteDAO compteDAO = new CompteDAO();

    @Override
    public boolean create(Transaction obj) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return create(obj, connection);
        }
    }

    
    public boolean create(Transaction obj, Connection connection) throws SQLException {
        if (obj.getId() == null || obj.getId().isEmpty()) {
            obj.setId(java.util.UUID.randomUUID().toString());
        }
        if (obj.getType() == null) {
            throw new SQLException("Le type de la transaction ne peut pas être nul.");
        }
        if (obj.getMontant() == null) {
            throw new SQLException("Le montant de la transaction ne peut pas être nul.");
        }

        String sql = "INSERT INTO transactions (id, date_transaction, montant, type, lieu, compte_source_id, compte_destination_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getId());

            if (obj.getDate() != null) {
                pstmt.setObject(2, obj.getDate());
            } else {
                pstmt.setNull(2, Types.TIMESTAMP);
            }

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
        if (obj.getType() == null) {
            throw new SQLException("Le type de la transaction ne peut pas être nul.");
        }
        if (obj.getMontant() == null) {
            throw new SQLException("Le montant de la transaction ne peut pas être nul.");
        }

        String sql = "UPDATE transactions SET date_transaction = ?, montant = ?, type = ?, lieu = ?, compte_source_id = ?, compte_destination_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            if (obj.getDate() != null) {
                pstmt.setObject(1, obj.getDate());
            } else {
                pstmt.setNull(1, Types.TIMESTAMP);
            }

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
        LocalDateTime date = resultat.getObject("date_transaction", LocalDateTime.class);
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

    /**
     * Retourne la date de la derniere operation enregistree pour chaque compte deja utilise.
     * Un compte absent de la map n'a jamais enregistre de transaction.
     * Une seule requete SQL suffit (agregation MAX par compte).
     *
     * @return une map (identifiant du compte -> date de la derniere transaction)
     * @throws SQLException en cas d'erreur technique (base de donnees)
     */
    public Map<String, LocalDateTime> findDernieresActivitesParCompte() throws SQLException {
        Map<String, LocalDateTime> dernieresActivites = new HashMap<>();

        String sql = "SELECT c.id AS compte_id, MAX(t.date_transaction) AS derniere_activite " +
                "FROM comptes c " +
                "LEFT JOIN transactions t ON t.compte_source_id = c.id OR t.compte_destination_id = c.id " +
                "GROUP BY c.id";

        try (Connection connection = DatabaseConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LocalDateTime derniereActivite = rs.getObject("derniere_activite", LocalDateTime.class);
                if (derniereActivite != null) {
                    dernieresActivites.put(rs.getString("compte_id"), derniereActivite);
                }
            }
        }
        return dernieresActivites;
    }
}