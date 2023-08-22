package no.boco.backend.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Get all categories stored in the database.
     * @return an iterable of all categories found.
     */
    public List<Category> getAll(){
        return categoryRepository.findAll();
    }

    public Category getByName(String name){
        return categoryRepository.findByName(name);
    }

    /**
     * Saves category to the repository.
     * @param category to be saved
     * @return name of the saved category.
     */
    public String add(Category category) {
        return categoryRepository.save(category).getName();
    }

    public boolean categoryExists(Long categoryId) {
        return categoryRepository.existsById(categoryId);
    }
}
