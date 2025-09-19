package server.dao;

import server.models.Category;

public class CategoryDAO extends BaseDAO<Category, Integer> {
    public CategoryDAO() {
        super(Category.class);
    }
}
