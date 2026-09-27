package DAOS;

import models.Client;
import models.Compte;
import models.CompteCourant;
import models.CompteEpargne;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompteDAO implements DAO<Compte> {

    private final ClientDAO clientDAO = new ClientDAO();

    @Override
    public boolean create(Compte obj) throws SQLException {
        if (obj.getId() == null || obj.getId().isEmpty()) {
            obj.setId(java.util.UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO comptes (id, numero, solde, decouvert_autorise, taux_interet, type_compte, client_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getId());
            pstmt.setString(2, obj.getNumero());
            pstmt.setDouble(3, obj.getSolde());

            if (obj instanceof CompteCourant) {
                Double decouvert = ((CompteCourant) obj).getDecouvertAutorise();
                pstmt.setDouble(4, decouvert != null ? decouvert : 0.0);
                pstmt.setNull(5, Types.DOUBLE);
                pstmt.setString(6, "COURANT");
            } else if (obj instanceof CompteEpargne) {
                pstmt.setNull(4, Types.DOUBLE);
                Double taux = ((CompteEpargne) obj).getTauxInteret();
                pstmt.setDouble(5, taux != null ? taux : 0.0);
                pstmt.setString(6, "EPARGNE");
            } else {
                throw new SQLException("Type de compte non supporté : " + obj.getClass().getName());
            }

            if (obj.getClient() != null) {
                pstmt.setString(7, obj.getClient().getId());
            } else {
                pstmt.setNull(7, Types.VARCHAR);
            }

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public Optional<Compte> findById(String id) throws SQLException {
        String sql = "SELECT * FROM comptes WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet resultat = pstmt.executeQuery()) {
                if (resultat.next()) {
                    Compte compte = mapResultSetToCompte(resultat);
                    return Optional.of(compte);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Compte> findAll() throws SQLException {
        List<Compte> comptes = new ArrayList<>();
        String sql = "SELECT * FROM comptes";
        try (Connection connection = DatabaseConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet resultat = stmt.executeQuery(sql)) {

            while (resultat.next()) {
                comptes.add(mapResultSetToCompte(resultat));
            }
        }
        return comptes;
    }

    @Override
    public boolean update(Compte obj) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            return update(obj, connection);
        }
    }

    /**
     * Met a jour le compte en utilisant la connexion fournie par l'appelant.
     * Permet d'inscrire la mise a jour du solde dans une transaction SQL externe
     * (utilise par TransactionService pour garantir l'atomicite versement / retrait / virement).
     */
    public boolean update(Compte obj, Connection connection) throws SQLException {
        String sql = "UPDATE comptes SET numero = ?, solde = ?, decouvert_autorise = ?, taux_interet = ?, type_compte = ?, client_id = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getNumero());
            pstmt.setDouble(2, obj.getSolde());

            if (obj instanceof CompteCourant) {
                Double decouvert = ((CompteCourant) obj).getDecouvertAutorise();
                pstmt.setDouble(3, decouvert != null ? decouvert : 0.0);
                pstmt.setNull(4, Types.DOUBLE);
                pstmt.setString(5, "COURANT");
            } else if (obj instanceof CompteEpargne) {
                pstmt.setNull(3, Types.DOUBLE);
                Double taux = ((CompteEpargne) obj).getTauxInteret();
                pstmt.setDouble(4, taux != null ? taux : 0.0);
                pstmt.setString(5, "EPARGNE");
            } else {
                throw new SQLException("Type de compte non supporté : " + obj.getClass().getName());
            }

            if (obj.getClient() != null) {
                pstmt.setString(6, obj.getClient().getId());
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }

            pstmt.setString(7, obj.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM comptes WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    private Compte mapResultSetToCompte(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String numero = rs.getString("numero");
        double solde = rs.getDouble("solde");
        String typeCompte = rs.getString("type_compte");

        String clientId = rs.getObject("client_id", String.class);
        Client client = null;
        if (clientId != null) {
            Optional<Client> clientOpt = clientDAO.findById(clientId);
            if (clientOpt.isPresent()) {
                client = clientOpt.get();
            }
        }

        Compte compte;
        if ("COURANT".equalsIgnoreCase(typeCompte)) {
            Double decouvert = rs.getObject("decouvert_autorise", Double.class);
            compte = new CompteCourant(id, numero, solde, decouvert);
        } else {
            Double taux = rs.getObject("taux_interet", Double.class);
            compte = new CompteEpargne(id, numero, solde, taux);
        }

        compte.setClient(client);
        return compte;
    }

    // Recherche d'un compte par son numéro (méthode spécifique, non définie dans l'interface DAO)
    public Optional<Compte> findByNumero(String numero) throws SQLException {
        String sql = "SELECT * FROM comptes WHERE numero = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, numero);
            try (ResultSet resultat = pstmt.executeQuery()) {
                if (resultat.next()) {
                    return Optional.of(mapResultSetToCompte(resultat));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Compte> trouverCompteSoldeMax() throws SQLException {
        String sql = "SELECT * FROM comptes ORDER BY solde DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {

                return Optional.of(mapResultSetToCompte(rs));
            }
        }
        return Optional.empty();
    }

    public Optional<Compte> trouverCompteSoldeMin() throws SQLException {
        String sql = "SELECT * FROM comptes ORDER BY solde ASC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return Optional.of(mapResultSetToCompte(rs));
            }
        }
        return Optional.empty();
    }

}
