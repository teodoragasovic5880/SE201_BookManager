package server.dao;

import server.models.Author;

public class AuthorDAO extends BaseDAO<Author, Integer> {
    public AuthorDAO() {
        super(Author.class);
    }
}
