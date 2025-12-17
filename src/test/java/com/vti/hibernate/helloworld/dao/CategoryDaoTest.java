package com.vti.hibernate.helloworld.dao;

import com.vti.hibernate.helloworld.entities.Category;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CategoryDaoTest {
    private static CategoryDao categoryDao;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        categoryDao = new CategoryDaoImpl();
    }


    @Test
    void testGetAllCategories() {

        List<Category> categoryList = categoryDao.getAllCategories();
        // test
        assertTrue(categoryList.size() > 0);

    }

    @Test
    void testFindCategoryByName() {

        String searchName = "Test category3";

        List<Category> categoryList = categoryDao.findCategoryByName(searchName);
        // test
        assertTrue(categoryList.size() > 0);

    }

    @Test
    void testInsertCategory() {

        Category category = new Category();
        category.setCategoryName("Test category2");
        // test
        assertEquals(1, categoryDao.insertCategory(category));
    }

    @Test
    void testUpdateCategory() {

        Category category = new Category();
        int categoryId = 1;
        category.setCategoryName("update category id 1");
        // test
        assertEquals(1, categoryDao.updateCategory(category, categoryId));
    }

    @Test
    void testDeleteCategory() {

        int categoryId = 1;
        // test
        assertEquals(1, categoryDao.deleteCategoryById(categoryId));
    }

    @Test
    void testOrderBy() {

        String columnName = "Id"; // mapping với tên field trong entity java
        String sortType = "DESC";

        List<Category> categoryList = categoryDao.sortCategories(columnName, sortType);
        // test
        assertTrue(categoryList.size() > 0);

    }

    @Test
    void testGroupBy() {

        String searchName = "Test category3";

        List<Category> categoryList = categoryDao.groupByCategories(searchName);
        // test
        assertTrue(categoryList.size() > 0);

    }

    @Test
    void testPaging() {

        int from = 0;
        int to = 2;

        List<Category> categoryList = categoryDao.getAllCategoryPaging(from, to);
        // test
        assertTrue(categoryList.size() > 0);

    }
}