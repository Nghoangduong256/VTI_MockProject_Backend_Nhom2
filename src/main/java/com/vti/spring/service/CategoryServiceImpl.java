package com.vti.spring.service;

import com.vti.hibernate.helloworld.entities.Category;
import com.vti.spring.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    // Call layer repository
    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.getAllCategories();
    }

    @Override
    public List<Category> findCategoryByName(String categoryName) {
        return categoryRepository.findCategoryByName(categoryName);
    }

    @Override
    public int insertCategory(Category category) {
        return categoryRepository.insertCategory(category);
    }

    @Override
    public int updateCategory(Category category, int categoryId) {
        return categoryRepository.updateCategory(category, categoryId);
    }

    @Override
    public int deleteCategoryById(int categoryId) {
        return categoryRepository.deleteCategoryById(categoryId);
    }

    @Override
    public List<Category> sortCategories(String sortColumnName, String sortType) {
        return categoryRepository.sortCategories(sortColumnName, sortType);
    }

    @Override
    public List<Category> groupByCategories(String groupColumnName) {
        return categoryRepository.groupByCategories(groupColumnName);
    }

    @Override
    public List<Category> getAllCategoryPaging(int from, int to) {
        // Thêm xử lý logic rồi mới gọi db để lấy data
        return categoryRepository.getAllCategoryPaging(from, to);
    }

    @Override
    public List<Category> getAllCategoryBySearch(String searchName, int page, int size, String sortField, String sortDir) {
        return categoryRepository.getAllCategoryBySearch(searchName, page, size, sortField, sortDir);
    }
}
