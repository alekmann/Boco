package no.boco.backend.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@CrossOrigin
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;


    /**
     * Get all categories stored in the database.
     * @return an iterable of all categories found.
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories(){
        logger.trace("Retrieving all categories...");
        return new ResponseEntity<>(categoryService.getAll(), HttpStatus.OK);
    }

    /**
     * Attempts to add a category to the database.
     * @param category to be added.
     * @return name of the added category.
     */
    @PostMapping
    public ResponseEntity<String> add(@RequestBody Category category){
        try{
            logger.info("Adding category...");
            return new ResponseEntity<>(categoryService.add(category), HttpStatus.CREATED);
        } catch (Exception e){
            logger.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
