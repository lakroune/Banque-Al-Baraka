package DAOS;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface DAO<T> {

    boolean create(T obj) throws SQLException;

    Optional<T> findById(String id) throws SQLException;

    List<T> findAll() throws SQLException;

    boolean update(T obj) throws SQLException;

    boolean delete(String id) throws SQLException;
}