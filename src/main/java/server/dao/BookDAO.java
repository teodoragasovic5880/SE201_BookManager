package server.dao;

import server.models.Book;

public class BookDAO extends BaseDAO<Book, Integer> {
    public BookDAO() {
        super(Book.class);
    }
}
