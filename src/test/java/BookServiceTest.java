import client.dto.*;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.dao.*;
import server.models.*;
import server.service.BookService;
import shared.Response;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock private BookDAO bookDao;
    @Mock private AuthorDAO authorDao;
    @Mock private CategoryDAO categoryDao;
    @Mock private StatusDAO statusDao;
    @Mock private SessionFactory sessionFactory; // new
    @Mock private Session session;
    @Mock private Query<Book> bookQuery;
    @InjectMocks private BookService bookService;

    private Author buildAuthor() {
        Author a = new Author();
        a.setAuthorId(1);
        a.setAuthorName("A");
        a.setAuthorSurname("B");
        a.setLifespan("1900-2000");
        return a;
    }

    private Category buildCategory() {
        Category c = new Category();
        c.setCategoryId(1);
        c.setCategoryName("Fiction");
        return c;
    }

    private Status buildStatus() {
        Status s = new Status();
        s.setStatusId(1);
        s.setStatusName("Available");
        return s;
    }

    private Book buildBook() {
        Book b = new Book();
        b.setBookId(10);
        b.setTitle("Test Book");
        b.setAuthor(buildAuthor());
        b.setCategory(buildCategory());
        b.setStatus(buildStatus());
        b.setCoverImagePath("cover.png");
        return b;
    }

    @Test
    void testGetAllBooks() {
        List<Book> books = List.of(buildBook());
        when(bookDao.findAll()).thenReturn(books);

        Response resp = bookService.getAllBooks();

        assertTrue(resp.isSuccess());
        List<BookDTO> dtos = (List<BookDTO>) resp.getData();
        assertEquals(1, dtos.size());
        assertEquals("Test Book", dtos.get(0).getTitle());
        verify(bookDao).findAll();
    }

    @Test
    void testAddBook() {
        BookDTO dto = new BookDTO(10, "Test Book",
                new AuthorDTO(1, "A", "B", "1900-2000"),
                new CategoryDTO(1, "Fiction"),
                new StatusDTO(1, "Available"),
                "cover.png");

        when(authorDao.findById(1)).thenReturn(buildAuthor());
        when(categoryDao.findById(1)).thenReturn(buildCategory());
        when(statusDao.findById(1)).thenReturn(buildStatus());

        Response resp = bookService.addBook(dto);

        assertTrue(resp.isSuccess());
        verify(bookDao).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        BookDTO dto = new BookDTO(10, "Updated Book",
                new AuthorDTO(1, "A", "B", "1900-2000"),
                new CategoryDTO(1, "Fiction"),
                new StatusDTO(1, "Available"),
                "cover.png");

        when(authorDao.findById(1)).thenReturn(buildAuthor());
        when(categoryDao.findById(1)).thenReturn(buildCategory());
        when(statusDao.findById(1)).thenReturn(buildStatus());

        Response resp = bookService.updateBook(dto);

        assertTrue(resp.isSuccess());
        verify(bookDao).update(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        Response resp = bookService.deleteBook(10);
        assertTrue(resp.isSuccess());
        verify(bookDao).deleteById(10);
    }

    @Test
    void testReadAllBooksSorted() {
        Book b = buildBook();
        List<Book> books = List.of(b);

        Query<Book> mockQuery = mock(Query.class);
        when(mockQuery.getResultList()).thenReturn(books);
        when(session.createQuery(anyString(), eq(Book.class))).thenReturn(mockQuery);
        when(sessionFactory.openSession()).thenReturn(session); // inject mock session

        Response resp = bookService.readAllBooksSorted("ASC");

        assertTrue(resp.isSuccess());
        List<BookDTO> dtos = (List<BookDTO>) resp.getData();
        assertEquals(1, dtos.size());
        assertEquals("Test Book", dtos.get(0).getTitle());
    }

    @Test
    void testSearchBooks() {
        Book b = buildBook();
        List<Book> books = List.of(b);

        Query<Book> mockQuery = mock(Query.class);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(books);
        when(session.createQuery(anyString(), eq(Book.class))).thenReturn(mockQuery);
        when(sessionFactory.openSession()).thenReturn(session);

        Response resp = bookService.searchBooks("test", "ASC");

        assertTrue(resp.isSuccess());
        List<BookDTO> dtos = (List<BookDTO>) resp.getData();
        assertEquals(1, dtos.size());
        assertEquals("Test Book", dtos.get(0).getTitle());
    }

    @Test
    void testGetBookCountByCategory() {
        Object[] row1 = new Object[]{"Fiction", 5L};
        Object[] row2 = new Object[]{"Non-Fiction", 3L};
        List<Object[]> results = new ArrayList<>();
        results.add(row1);
        results.add(row2);

        Query<Object[]> mockQuery = mock(Query.class);
        when(mockQuery.list()).thenReturn(results);
        when(session.createQuery(anyString())).thenReturn(mockQuery);
        when(sessionFactory.openSession()).thenReturn(session);

        Response resp = bookService.getBookCountByCategory();

        assertTrue(resp.isSuccess());
        Map<String, Integer> map = (Map<String, Integer>) resp.getData();
        assertEquals(2, map.size());
        assertEquals(5, map.get("Fiction"));
        assertEquals(3, map.get("Non-Fiction"));
    }
}
