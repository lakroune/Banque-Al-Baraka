
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
    public boolean create(Compte obj) {
        String sql = "INSERT INTO compte (id, numero, solde, decouvert_autorise, taux_interet, type_compte, client_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getId());
            pstmt.setString(2, obj.getNumero());
            pstmt.setDouble(3, obj.getSolde());

            if (obj instanceof CompteCourant) {
                pstmt.setDouble(4, ((CompteCourant) obj).getDecouvertAutorise());
                pstmt.setNull(5, Types.DOUBLE);
                pstmt.setString(6, "COURANT");
            } else if (obj instanceof CompteEpargne) {
                pstmt.setNull(4, Types.DOUBLE);
                pstmt.setDouble(5, ((CompteEpargne) obj).getTauxInteret());
                pstmt.setString(6, "EPARGNE");
            }

            if (obj.getClient() != null) {
                pstmt.setInt(7, obj.getClient().getId());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Compte> findById(String id) throws SQLException {
        String sql = "SELECT * FROM compte WHERE id = ?";
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
        String sql = "SELECT * FROM compte";
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
        String sql = "UPDATE compte SET numero = ?, solde = ?, decouvert_autorise = ?, taux_interet = ?, type_compte = ?, client_id = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getNumero());
            pstmt.setDouble(2, obj.getSolde());

            if (obj instanceof CompteCourant) {
                pstmt.setDouble(3, ((CompteCourant) obj).getDecouvertAutorise());
                pstmt.setNull(4, Types.DOUBLE);
                pstmt.setString(5, "COURANT");
            } else if (obj instanceof CompteEpargne) {
                pstmt.setNull(3, Types.DOUBLE);
                pstmt.setDouble(4, ((CompteEpargne) obj).getTauxInteret());
                pstmt.setString(5, "EPARGNE");
            }

            if (obj.getClient() != null) {
                pstmt.setInt(6, obj.getClient().getId());
            } else {
                pstmt.setNull(6, Types.INTEGER);
            }

            pstmt.setString(7, obj.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM compte WHERE id = ?";
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
        int clientId = rs.getInt("client_id");

        Client client = null;
        if (!rs.wasNull()) {
            Optional<Client> clientOpt = clientDAO.findById(String.valueOf(clientId));
            if (clientOpt.isPresent()) {
                client = clientOpt.get();
            }
        }

        Compte compte;
        if ("COURANT".equalsIgnoreCase(typeCompte)) {
            double decouvert = rs.getDouble("decouvert_autorise");
            compte = new CompteCourant(id, numero, solde, decouvert);
        } else {
            double taux = rs.getDouble("taux_interet");
            compte = new CompteEpargne(id, numero, solde, taux);
        }

        compte.setClient(client);
        return compte;
    }
}