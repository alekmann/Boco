package no.boco.backend.category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class CategoryIntegrationTest {
    @Autowired
    CategoryService categoryService;
    @Autowired
    CategoryRepository categoryRepository;

    @AfterEach
    public void emptyDatabase() {
        categoryRepository.deleteAll();
    }

    @Nested
    class getAll{
        @Test
        public void method_finds_all(){
            assertTrue(categoryRepository.findAll().isEmpty());

            for(int i=0; i<5; i++){
                Category category = new Category("Tools " + i);
                categoryRepository.save(category);
            }

            List<Category> categories = categoryService.getAll();
            assertEquals(categories.size(), 5);
        }
    }

    @Nested
    class add{
        @Test
        public void method_saves_object(){
            assertTrue(categoryRepository.findAll().isEmpty());

            Category category = new Category("Tools");
            categoryService.add(category);

            assertFalse(categoryRepository.findAll().isEmpty());
            assertEquals(categoryRepository.findAll().get(0).getName(), "Tools");
        }
    }

    @Nested
    class categoryExists{
        @Test
        public void returns_true_on_match(){
            Category category = new Category("Tools");
            category = categoryRepository.save(category);

            assertTrue(categoryService.categoryExists(category.getId()));
        }

        @Test
        public void returns_false_on_miss(){
            assertTrue(categoryRepository.findAll().isEmpty());
            assertFalse(categoryService.categoryExists(1L));
        }
    }
}
