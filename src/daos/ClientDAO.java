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

public class ClientDAO implements DAO<Client> {

    @Override
    public boolean create(Client obj) {
        if (obj.getId() == null || obj.getId().isEmpty()) {
            obj.setId(java.util.UUID.randomUUID().toString());
        }

        String sql = "INSERT INTO clients (id, nom, email) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getId());
            pstmt.setString(2, obj.getNom());
            pstmt.setString(3, obj.getEmail());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override

    public Optional<Client> findById(String id) throws SQLException {
        String sql = "SELECT c.id AS client_id, c.nom, c.email, " +
                "com.id AS compte_id, com.numero, com.solde, com.decouvert_autorise, com.taux_interet, com.type_compte "
                +
                "FROM clients c LEFT JOIN compte com ON c.id = com.client_id WHERE c.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, id);
            try (ResultSet resultat = pstmt.executeQuery()) {
                Client client = null;
                List<Compte> listcompte = new ArrayList<>();

                while (resultat.next()) {
                    if (client == null) {
                        client = new Client(
                                resultat.getString("client_id"),
                                resultat.getString("nom"),
                                resultat.getString("email"),
                                listcompte);
                    }

                    String compteId = resultat.getString("compte_id");
                    if (compteId != null) {
                        String typeCompte = resultat.getString("type_compte");
                        String numero = resultat.getString("numero");
                        double solde = resultat.getDouble("solde");

                        if ("COURANT".equalsIgnoreCase(typeCompte)) {
                            double decouvert = resultat.getDouble("decouvert_autorise");
                            CompteCourant cc = new CompteCourant(compteId, numero, solde, decouvert);
                            cc.setClient(client);
                            listcompte.add(cc);
                        } else if ("EPARGNE".equalsIgnoreCase(typeCompte)) {
                            double taux = resultat.getDouble("taux_interet");
                            CompteEpargne ce = new CompteEpargne(compteId, numero, solde, taux);
                            ce.setClient(client);
                            listcompte.add(ce);
                        }
                    }
                }

                if (client != null) {
                    return Optional.of(client);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        try (Connection connection = DatabaseConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet resultat = stmt.executeQuery(sql)) {

            while (resultat.next()) {
                Client client = new Client(
                        resultat.getString("id"),
                        resultat.getString("nom"),
                        resultat.getString("email"));
                clients.add(client);
            }
        }
        return clients;
    }

    @Override
    public boolean update(Client obj) throws SQLException {
        String sql = "UPDATE clients SET nom = ?, email = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getNom());
            pstmt.setString(2, obj.getEmail());
            pstmt.setString(3, obj.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM clients WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }
}