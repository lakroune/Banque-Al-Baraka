package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Fournit les connexions JDBC a l'application (PostgreSQL - base "bankab").
 *
 * Chaque appel a getConnection() ouvre une NOUVELLE connexion : c'est ce qui rend
 * compatible l'usage systematique du try-with-resources dans les DAO.
 * Chaque methode DAO ferme ainsi SA propre connexion sans jamais invalider une
 * connexion utilisee ailleurs (appelant, traitement imbrique, transaction en cours).
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5433/bankab";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "123456";

    private DatabaseConnection() {
    }

    /**
     * Ouvre une nouvelle connexion a la base de donnees.
     *
     * @return une connexion JDBC ouverte, a fermer par l'appelant
     * @throws SQLException si la connexion ne peut pas etre etablie
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * Ferme une connexion en ignorant les erreurs de fermeture.
     *
     * @param connection la connexion a fermer (peut etre null)
     */
    public static void closeConnection(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}