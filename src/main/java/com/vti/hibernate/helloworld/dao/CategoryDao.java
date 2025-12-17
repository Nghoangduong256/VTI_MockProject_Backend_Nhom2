package com.vti.hibernate.helloworld.dao;

import com.vti.hibernate.helloworld.entities.Category;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CategoryDao {
    // get all category
    List<Category> getAllCategories();

    // find category by name
    List<Category> findCategoryByName(String categoryName);

    // insert category
    int insertCategory(Category category);

    // update category
    int updateCategory(Category category, int categoryId);

    // delete category
    int deleteCategoryById(int categoryId);

    // sort, 2 param string: sortColumnName, sortType
    List<Category> sortCategories(String sortColumnName, String sortType);

    // group by, param groupColumnName
    List<Category> groupByCategories(String groupColumnName);

    // paging, param int from, int to
    List<Category> getAllCategoryPaging(int from, int to);
}
