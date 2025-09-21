package server.service;
import client.dto.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import server.dao.*;
import server.models.*;
import server.util.HibernateUtil;
import shared.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookService {
    private BookDAO bookDao;
    private AuthorDAO authorDao;
    private CategoryDAO categoryDao;
    private StatusDAO statusDao;
    private final SessionFactory sessionFactory;

    public BookService(BookDAO bookDao) {
        this(bookDao, new AuthorDAO(), new CategoryDAO(), new StatusDAO(), HibernateUtil.getSessionFactory());
    }
    public BookService(BookDAO bookDao, AuthorDAO authorDao, CategoryDAO categoryDao,
                       StatusDAO statusDao, SessionFactory sessionFactory) {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.categoryDao = categoryDao;
        this.statusDao = statusDao;
        this.sessionFactory = sessionFactory;
    }

    public Response getAllBooks() {
        List<Book> books = bookDao.findAll();
        return new Response(true, convertToDtoList(books));
    }

    public Response addBook(BookDTO bookDTO) {
        Book entity = convertToEntity(bookDTO);
        bookDao.save(entity);
        return new Response(true, "Book added");
    }

    public Response updateBook(BookDTO bookDTO) {
        Book entity = convertToEntity(bookDTO);
        bookDao.update(entity);
        return new Response(true, "Book updated");
    }

    public Response deleteBook(int bookId) {
        bookDao.deleteById(bookId);
        return new Response(true, "Book deleted");
    }

    public Response readAllBooksSorted(String sortOrder) {
        try (Session session = sessionFactory.openSession()) {  // use injected SessionFactory
            String hql = "FROM Book ORDER BY title " + sortOrder;
            List<Book> books = session.createQuery(hql, Book.class).getResultList();
            return new Response(true, convertToDtoList(books));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Failed to sort books");
        }
    }

    public Response searchBooks(String filter, String sortOrder) {
        try (Session session = sessionFactory.openSession()) {  // use injected SessionFactory
            String hql = "FROM Book WHERE LOWER(title) LIKE :title ORDER BY title " + sortOrder;
            List<Book> books = session.createQuery(hql, Book.class)
                    .setParameter("title", "%" + filter.toLowerCase() + "%")
                    .getResultList();
            return new Response(true, convertToDtoList(books));
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Failed to search books");
        }
    }

    public Response getBookCountByCategory() {
        Map<String, Integer> categoryCounts = new HashMap<>();
        try (Session session = sessionFactory.openSession()) {  // use injected SessionFactory
            String hql = "SELECT c.categoryName, COUNT(b) FROM Book b JOIN b.category c GROUP BY c.categoryName";
            List<Object[]> results = session.createQuery(hql).list();
            for (Object[] row : results) {
                String categoryName = (String) row[0];
                Long count = (Long) row[1];
                categoryCounts.put(categoryName, count.intValue());
            }
            return new Response(true, categoryCounts);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(false, "Failed to get book counts by category");
        }
    }

    // --- Conversion methods (your exact ones) ---

    private List<BookDTO> convertToDtoList(List<Book> entities) {
        List<BookDTO> dtos = new ArrayList<>();
        for (Book e : entities) {

            AuthorDTO authorDto = null;
            if (e.getAuthor() != null) {
                authorDto = new AuthorDTO(
                        e.getAuthor().getAuthorId(),
                        e.getAuthor().getAuthorName(),
                        e.getAuthor().getAuthorSurname(),
                        e.getAuthor().getLifespan()
                );
            } else {
                authorDto = new AuthorDTO(0, "Unknown", "Unknown", "");
            }

            CategoryDTO categoryDto = null;
            if (e.getCategory() != null) {
                categoryDto = new CategoryDTO(
                        e.getCategory().getCategoryId(),
                        e.getCategory().getCategoryName()
                );
            } else {
                categoryDto = new CategoryDTO(0, "Unknown");
            }

            StatusDTO statusDto = null;
            if (e.getStatus() != null) {
                statusDto = new StatusDTO(
                        e.getStatus().getStatusId(),
                        e.getStatus().getStatusName()
                );
            } else {
                statusDto = new StatusDTO(0, "Unknown");
            }

            dtos.add(new BookDTO(
                    e.getBookId(),
                    e.getTitle(),
                    authorDto,
                    categoryDto,
                    statusDto,
                    e.getCoverImagePath()
            ));
        }
        return dtos;
    }

    private Book convertToEntity(BookDTO dto) {
        Book entity = new Book();
        entity.setBookId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setCoverImagePath(dto.getCoverImagePath());
        entity.setAuthor(authorDao.findById(dto.getAuthor().getAuthorId()));
        entity.setCategory(categoryDao.findById(dto.getCategory().getCategoryId()));
        entity.setStatus(statusDao.findById(dto.getStatus().getStatusId()));
        return entity;
    }
}
