
import models.Client;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientDAO implements DAO<Client> {

    @Override
    public boolean create(Client obj) {
        String sql = "INSERT INTO client (nom, email) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, obj.getNom());
            pstmt.setString(2, obj.getEmail());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        obj.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Client> findById(String id) throws SQLException {
        String sql = "SELECT * FROM client WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(id));
            try (ResultSet resultat = pstmt.executeQuery()) {
                if (resultat.next()) {
                    Client client = new Client(
                            resultat.getInt("id"),
                            resultat.getString("nom"),
                            resultat.getString("email"));
                    return Optional.of(client);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM client";
        try (Connection connection = DatabaseConnection.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet resultat = stmt.executeQuery(sql)) {

            while (resultat.next()) {
                Client client = new Client(
                        resultat.getInt("id"),
                        resultat.getString("nom"),
                        resultat.getString("email"));
                clients.add(client);
            }
        }
        return clients;
    }

    @Override
    public boolean update(Client obj) throws SQLException {
        String sql = "UPDATE client SET nom = ?, email = ? WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, obj.getNom());
            pstmt.setString(2, obj.getEmail());
            pstmt.setInt(3, obj.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        String sql = "DELETE FROM client WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
                PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(id));
            return pstmt.executeUpdate() > 0;
        }
    }
}