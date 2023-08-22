package no.boco.backend.location;

import no.boco.backend.category.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class LocationIntegrationTest {
    @Autowired
    LocationService locationService;

    @Autowired
    LocationRepository locationRepository;

    @AfterEach
    public void emptyDatabase() {
        locationRepository.deleteAll();
    }

    @Test
    public void method_saves_object(){
        assertTrue(locationRepository.findAll().isEmpty());

        Location location = new Location();
        locationService.add(location);

        assertFalse(locationRepository.findAll().isEmpty());
        assertEquals(locationRepository.findAll().size(), 1);
    }
}
