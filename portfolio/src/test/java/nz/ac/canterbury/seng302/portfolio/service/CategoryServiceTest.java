package nz.ac.canterbury.seng302.portfolio.service;

import nz.ac.canterbury.seng302.portfolio.model.Category;
import nz.ac.canterbury.seng302.portfolio.model.Evidence;
import nz.ac.canterbury.seng302.portfolio.repository.CategoryRepository;
import nz.ac.canterbury.seng302.portfolio.repository.EvidenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link CategoryService} class.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EvidenceRepository evidenceRepository;

    @InjectMocks
    private CategoryService categoryService;

    @InjectMocks
    private EvidenceService evidenceService;

    private static final List<Category> testCategories = new ArrayList<>();
    private static final List<Evidence> testEvidences = new ArrayList<>();

    /**
     * setUp list of Categories for testing which will be returned when mocking the repository's method which will return list of Categories.
     */
    @BeforeEach
    void setUp() {
        for (int i = 0; i < 4; i++) {
            Category category = new Category("Test category " + (i + 1));
            testCategories.add(category);
        }

        /* When evidence has sections for Categories, add the categories above to the piece of evidence */
        Evidence evidence1 = new Evidence(0, 1, "testEvidence1", "testEvidence1", new Date(100));
        Evidence evidence2 = new Evidence(0, 1, "testEvidence2", "testEvidence2", new Date(100));
        for (int i = 0; i < testCategories.size(); i++) {
            if (i % 2 == 0) {
                evidence1.addCategory(testCategories.get(i));
            } else {
                evidence2.addCategory(testCategories.get(i));
            }
        }
        testEvidences.add(evidence1);
        testEvidences.add(evidence2);
    }

    /**
     * Tests that the getCategories method returns all Categories.
     */
    @Test
    void testGetCategories() {
        Evidence evidence = testEvidences.get(1);
        int evidenceId = evidence.getEvidenceId();
        Set<Category> actualCategories = evidence.getCategories();

        doReturn(Optional.of(evidence)).when(evidenceRepository).findById(evidenceId);

        Set<Category> returned = categoryService.getCategories(evidenceId);
        assertEquals(actualCategories.size(), returned.size());
    }

    /**
     * Tests that the getCategory method returns a category.
     */
    @Test
    void testGetCategory() {
        Category category = testCategories.get(1);
        int categoryId = category.getCategoryId();
        doReturn(Optional.of(category)).when(categoryRepository).findById(categoryId);
        Category returned = categoryService.getCategory(categoryId);
        assertEquals(category, returned);
    }

    /**
     * Tests that the getAllCategories method returns all Categories.
     */
    @Test
    void testGetAllCategories() {
        when(categoryRepository.findAll()).thenReturn(testCategories);

        List<Category> actualCategories = categoryService.getAllCategories();

        assertEquals(actualCategories.size(), testCategories.size());
        verify(categoryRepository, times(1)).findAll();
    }

    /**
     * Tests that the getCategoriesFromUserId method returns all the categories attached to the user's pieces of evidence.
     */
    @Test
    void testGetCategoriesFromUserId() {
        int userId = 5;
        when(evidenceRepository.findAllByUserId(userId)).thenReturn(testEvidences);

        List<Category> actualCategories = categoryService.getCategoriesFromUserId(userId);

        assertEquals(new HashSet<>(testCategories), new HashSet<>(actualCategories));
    }

    /**
     * Tests that the getCategoriesFromUserId method returns an empty list when the user has no pieces of evidence.
     */
    @Test
    void testGetCategoriesFromUserIdWhenNoEvidence() {
        int userId = 5;
        when(evidenceRepository.findAllByUserId(userId)).thenReturn(new ArrayList<>());

        List<Category> actualCategories = categoryService.getCategoriesFromUserId(userId);

        assertEquals(new ArrayList<>(), actualCategories);
    }

    /**
     * Tests that the getCategory(int categoryId) method returns specific category.
     */
    @Test
    void removeCategory() {
        Category category = testCategories.get(1);
        int categoryId = category.getCategoryId();
        doReturn(Optional.of(category)).when(categoryRepository).findById(categoryId);
        doNothing().when(categoryRepository).deleteById(categoryId);
        boolean success = categoryService.removeCategory(categoryId);
        assertTrue(success);
        verify(categoryRepository).deleteById(categoryId);
    }
}
