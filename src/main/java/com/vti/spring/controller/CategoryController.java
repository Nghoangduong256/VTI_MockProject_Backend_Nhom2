package com.vti.spring.controller;

import com.vti.hibernate.helloworld.entities.Category;
import com.vti.spring.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/category")
public class CategoryController {

    // Call layer service
    @Autowired
    CategoryService categoryService;

    // get all category
    // url: localhost:8080/api/v1/category, type get
    @GetMapping()
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // search custom all in one
    // url: localhost:8080/api/v1/category/search?name=test&sortField=id&sortDir=desc&page=0&size=5
    // type get
    @GetMapping("/search")
    public List<Category> findCategoryByName(@RequestParam(name = "name", required = false) String queryName,
                                             @RequestParam(required = false) int page,
                                             @RequestParam(required = false) int size,
                                             @RequestParam(required = false) String sortField,
                                             @RequestParam(required = false) String sortDir

    ) {
        return categoryService.getAllCategoryBySearch(queryName, page, size,
                sortField, sortDir);
    }

    // create
    // url: localhost:8080/api/v1/category, type post
    @PostMapping
    public int createCategory(@RequestBody Category category) {
        return categoryService.insertCategory(category);
    }

    // update
    // url: localhost:8080/api/v1/category/:id, type put
    // ex url: localhost:8080/api/v1/category/1 sẽ update categoryId = 1
    @PutMapping
    public int updateCategory(@PathVariable(name = "id") int categoryId,
                               @RequestBody Category category) {
        return categoryService.updateCategory(category, categoryId);
    }

    // delete
    // url: localhost:8080/api/v1/category/:id, type delete
    @DeleteMapping
    public int deleteCategory(@PathVariable(name = "id") int categoryId) {
        return categoryService.deleteCategoryById(categoryId);
    }
}
