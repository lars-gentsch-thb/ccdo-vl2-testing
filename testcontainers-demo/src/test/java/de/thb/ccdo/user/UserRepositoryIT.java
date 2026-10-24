package de.thb.ccdo.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
class UserRepositoryIT {

    // static: ein Container für alle Tests der Klasse (sonst einer pro Testmethode)
    @Container
    static PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:18-alpine");

    UserRepository repo;

    @BeforeEach
    void setUp() {
        repo = new UserRepository(postgres.getJdbcUrl(),
                postgres.getUsername(), postgres.getPassword());
    }

    @Test
    void savesAndFindsUser() {
        repo.save(new User("hans"));

        assertThat(repo.findByName("hans")).isPresent();
    }

    @Test
    void returnsEmptyForUnknownUser() {
        assertThat(repo.findByName("unbekannt")).isEmpty();
    }

    @Test
    void rejectsDuplicateNames() {
        repo.save(new User("grete"));

        assertThatThrownBy(() -> repo.save(new User("grete")))
                .isInstanceOf(IllegalStateException.class);
    }
}
