package de.thb.ccdo.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Minimales Repository mit reinem JDBC – bewusst ohne Framework,
 * damit im Test sichtbar bleibt, dass gegen eine echte PostgreSQL gearbeitet wird.
 */
public class UserRepository {

    private final String url;
    private final String username;
    private final String password;

    public UserRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        createTableIfMissing();
    }

    public void save(User user) {
        String sql = "INSERT INTO users (name) VALUES (?)";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Speichern fehlgeschlagen", e);
        }
    }

    public Optional<User> findByName(String name) {
        String sql = "SELECT name FROM users WHERE name = ?";
        try (Connection c = connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(new User(rs.getString("name"))) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Suche fehlgeschlagen", e);
        }
    }

    private void createTableIfMissing() {
        // UNIQUE-Constraint: wird von PostgreSQL durchgesetzt – genau solche Details
        // verhalten sich in In-Memory-DBs wie H2 mitunter anders.
        String ddl = "CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, name TEXT NOT NULL UNIQUE)";
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute(ddl);
        } catch (SQLException e) {
            throw new IllegalStateException("Schema konnte nicht angelegt werden", e);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
