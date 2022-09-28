package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Category;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.repository.CategoryRepository;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Contains methods for saving, deleting, and retrieving category objects to the database.
 */
@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EvidenceRepository evidenceRepository;

    /**
     * This function returns a category based on the categoryId.
     * @param categoryId id of category looking to return
     * @return category.
     */
    public Category getCategory(int categoryId) {
        Optional<Category> sOptional = categoryRepository.findById(categoryId);
        Category category = null;
        if (sOptional.isPresent()) {
            category = sOptional.get();
            categoryRepository.deleteById(category.getCategoryId());
        }
        return category;
    }

    /**
     * This function returns all categories ordered alphabetically.
     * @return List of categories.
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * This function returns all categories based on the evidenceId.
     * @param evidenceId id for piece of evidence
     * @return List of categories.
     */
    public Set<Category> getCategories(int evidenceId) {
        Optional<Evidence> evidence = evidenceRepository.findById(evidenceId);
        Set<Category> categories = null;
        if (evidence.isPresent()) {
            Evidence evidence1 = evidence.get();
            categories = evidence1.getCategories();
        }
        return categories;
    }


    /**
     * Remove a category from the database.
     * @param categoryId Id of the category being removed
     * @return true if removed, otherwise false
     */
    public boolean removeCategory(int categoryId) {
        Optional<Category> sOptional = categoryRepository.findById(categoryId);

        if (sOptional.isPresent()) {
            Category category = sOptional.get();
            categoryRepository.deleteById(category.getCategoryId());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all categories that a user has used.
     * @param userId user id of the user
     * @return list of categories the user has used
     */
    public List<Category> getCategoriesFromUserId(int userId) {
        List<Evidence> evidenceList = evidenceRepository.findAllByUserId(userId);
        Set<Category> allCategories = new HashSet<>();

        for (Evidence evidence : evidenceList) {
            Set<Category> categories = evidence.getCategories();
            allCategories.addAll(categories);
        }
        return allCategories.stream().toList();
    }
}
