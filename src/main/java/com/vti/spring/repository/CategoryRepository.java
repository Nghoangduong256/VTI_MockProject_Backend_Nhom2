package com.vti.spring.repository;

import com.vti.hibernate.helloworld.entities.Category;

import java.util.List;

public interface CategoryRepository {
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

    // Custom search all inn one
    // url ex: search?name=xxxx,sortField=id,sortDir=asc,page=0,size=5
    // Criteria sẽ hỗ trợ custom động câu sql
    List<Category> getAllCategoryBySearch(String searchName, int page, int size,
                                          String sortField, String sortDir);

}
