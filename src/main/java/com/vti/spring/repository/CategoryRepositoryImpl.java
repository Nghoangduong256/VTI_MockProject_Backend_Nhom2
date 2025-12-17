package com.vti.spring.repository;

import com.vti.hibernate.helloworld.entities.Category;
import com.vti.hibernate.helloworld.utils.HibernateUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    @Override
    public List<Category> getAllCategories() {

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String sql = "From Category";
            Query<Category> query = session.createQuery(sql, Category.class);
            // lấy data, select query nên không cần transaction.commit()

            List<Category> categories = query.list();
            for(Category category: categories) {
                System.out.println("category name: " + category.getCategoryName());
            }

            return categories;
        }
    }

    @Override
    public List<Category> findCategoryByName(String categoryName) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            String sql = "From Category where categoryName = :name";
            // tương tự search like, in ... : mn thử case này
            Query<Category> query = session.createQuery(sql, Category.class);
            // set parameter
            query.setParameter("name", categoryName);

            List<Category> categories = query.list();
            for(Category category: categories) {
                System.out.println("category name: " + category.getCategoryName());
            }

            return categories;
        }
    }

    @Override
    public int insertCategory(Category category) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            MutationQuery query = session.createMutationQuery("INSERT INTO "
                    + "Category(categoryName) VALUES (?1)");

            query.setParameter(1, category.getCategoryName());

            int result = query.executeUpdate();
            // execute data thì cần transaction.commit()
            transaction.commit();

            return result;
        }
    }

    @Override
    public int updateCategory(Category category, int categoryId) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            MutationQuery query = session.createMutationQuery("update " +
                    "Category set categoryName = :newName " +
                    "Where categoryId= :id");

            query.setParameter("newName", category.getCategoryName());
            query.setParameter("id", categoryId);

            int result = query.executeUpdate();
            // execute data thì cần transaction.commit()
            transaction.commit();

            return result;
        }
    }

    @Override
    public int deleteCategoryById(int categoryId) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            MutationQuery query = session.createMutationQuery("delete from " +
                    "Category Where categoryId= :id");

            query.setParameter("id", categoryId);

            int result = query.executeUpdate();
            // execute data thì cần transaction.commit()
            transaction.commit();

            return result;
        }
    }

    @Override
    public List<Category> sortCategories(String sortColumnName, String sortType) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {

            // note: liên quan đến tên class, field trong entity thì cần ghi vào, k sử dụng được param
            String sql = "From Category ORDER BY categoryName DESC";
            Query<Category> query = session.createQuery(sql, Category.class);
            // set parameter
//            query.setParameter("columnName", sortColumnName);
//            query.setParameter("sortType", sortType);

            List<Category> categories = query.list();
            for(Category category: categories) {
                System.out.println("category name: " + category.getCategoryName());
            }

            return categories;
        }
    }

    @Override
    public List<Category> groupByCategories(String groupColumnName) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {

            String sql = "From Category GROUP BY categoryId";
            Query<Category> query = session.createQuery(sql, Category.class);
            // set parameter
//            query.setParameter("columnName", groupColumnName);

            List<Category> categories = query.list();
            for(Category category: categories) {
                System.out.println("category name: " + category.getCategoryName());
            }

            return categories;
        }
    }

    @Override
    public List<Category> getAllCategoryPaging(int from, int to) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {

            // ex mysql: SELECT * FROM TestingSystem.product_categories limit 0,2
           //  ex hsql: product_categories limit ?,?
            String sql = "From Category";
            Query<Category> query = session.createQuery(sql, Category.class);
            // set LIMIT, OFFSET
            query.setFirstResult(from);
            query.setMaxResults(to);

            List<Category> categories = query.list();
            for(Category category: categories) {
                System.out.println("category name: " + category.getCategoryName());
            }

            return categories;
        }
    }

    @Override
    public List<Category> getAllCategoryBySearch(String searchName, int page, int size,
                                                 String sortField, String sortDir) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Category> cq = cb.createQuery(Category.class);

            Root<Category> root = cq.from(Category.class);

            // where
            List<Predicate> predicates = new ArrayList<>();
            // search by name, có value mới search
            // sql: where category_name like %searchname%
            if(searchName != null && !searchName.isEmpty()) {
                Predicate byName = cb.like(cb.lower(root.get("categoryName")), "%"
                        + searchName.toLowerCase() + "%");
                predicates.add(byName);
            }

            // nếu muôuốn search các column khách thì tạo thêm các predicate khác
            // Predicate byId = cb.like(root.get("categoryId"), "%"+ searchName + "%");
            // predicates.add(byId)

            cq.where(predicates.toArray(new Predicate[0]));

            // sort
            if (sortField != null && !sortField.isEmpty()) {
                if ("desc".equalsIgnoreCase(sortDir)) {
                    cq.orderBy(cb.desc(root.get(sortField)));
                } else {
                    cq.orderBy(cb.asc(root.get(sortField)));
                }
            }

            // paging
            Query<Category> query = session.createQuery(cq);

            if(page != 0 && size != 0) {
                query.setFirstResult(page *size);
                query.setMaxResults(size);

            }
            return query.getResultList();
        }
    }


}
