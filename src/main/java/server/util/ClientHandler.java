package server.util;


import client.dto.AuthorDTO;
import client.dto.BookDTO;
import client.dto.CategoryDTO;
import client.dto.StatusDTO;
import org.hibernate.Session;
import server.dao.AuthorDAO;
import server.dao.BookDAO;
import server.dao.CategoryDAO;
import server.dao.StatusDAO;
import server.models.Author;
import server.models.Book;
import server.models.Category;
import server.models.Status;
import shared.Request;
import shared.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler
{
    private Socket clientSocket;
    private BookDAO bookDao;
    private AuthorDAO authorDao;
    private CategoryDAO categoryDao;
    private StatusDAO statusDao;

    public ClientHandler(Socket clientSocket, BookDAO bookDao)
    {
        this.clientSocket = clientSocket;
        this.bookDao = bookDao;
        this.authorDao = new AuthorDAO();
        this.categoryDao = new CategoryDAO();
        this.statusDao = new StatusDAO();
    }

    public void handleClient()
    {
        try (
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())
        )
        {
            while (true)
            {
                Request request = (Request) input.readObject();
                Response response = null;

                switch (request.getAction())
                {
                    case GET_ENTITY_BY_ID:
                    {
                        Map<String, Object> data = (Map<String, Object>) request.getData();
                        String entity = (String) data.get("entity");
                        int id = (int) data.get("id");

                        Object result = null;

                        switch (entity.toLowerCase())
                        {
                            case "author":
                                Author author = authorDao.findById(id);
                                if (author != null)
                                {
                                    AuthorDTO authorDto = new AuthorDTO(
                                            author.getAuthorId(),
                                            author.getAuthorName(),
                                            author.getAuthorSurname(),
                                            author.getLifespan()
                                    );
                                    result = authorDto;
                                } else
                                {
                                    result = null;
                                }
                                break;
                            case "category":
                                Category category = categoryDao.findById(id);
                                if (category != null)
                                {
                                    CategoryDTO categoryDto = new CategoryDTO(
                                            category.getCategoryId(),
                                            category.getCategoryName()
                                    );
                                    result = categoryDto;
                                } else
                                {
                                    result = null;
                                }
                                break;

                            case "status":
                                Status status = statusDao.findById(id);
                                if (status != null)
                                {
                                    StatusDTO statusDto = new StatusDTO(
                                            status.getStatusId(),
                                            status.getStatusName()
                                    );
                                    result = statusDto;
                                } else
                                {
                                    result = null;
                                }
                                break;

                            default:
                                response = new Response(false, "Unknown entity type");
                                break;
                        }

                        if (result != null)
                        {
                            response = new Response(true, result);
                        } else if (response == null)
                        {
                            response = new Response(false, "Entity not found");
                        }

                        break;
                    }
                    case GET_BOOKS:
                    {
                        List<Book> bookEntities = bookDao.findAll();
                        List<BookDTO> books = convertToDtoList(bookEntities);
                        response = new Response(true, books);
                        break;
                    }

                    case ADD_BOOK:
                    {
                        BookDTO bookToAdd = (BookDTO) request.getData();
                        Book entityToAdd = convertToEntity(bookToAdd);
                        bookDao.save(entityToAdd);
                        response = new Response(true, "Book added");
                        break;
                    }

                    case UPDATE_BOOK:
                    {
                        BookDTO bookToUpdate = (BookDTO) request.getData();
                        Book entityToUpdate = convertToEntity(bookToUpdate);
                        bookDao.update(entityToUpdate);
                        response = new Response(true, "Book updated");
                        break;
                    }

                    case DELETE_BOOK:
                    {
                        int bookId = (Integer) request.getData();
                        bookDao.deleteById(bookId);
                        response = new Response(true, "Book deleted");
                        break;
                    }

                    case READ_ALL_BOOKS_SORTED:
                    {
                        String sortOrder = (String) request.getData();
                        try (Session session = HibernateUtil.getSessionFactory().openSession())
                        {
                            String hql = "FROM Book ORDER BY title " + sortOrder;
                            List<Book> bookEntities = session.createQuery(hql, Book.class).getResultList();
                            List<BookDTO> books = convertToDtoList(bookEntities);
                            response = new Response(true, books);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            response = new Response(false, "Failed to sort books");
                        }
                        break;
                    }

                    case SEARCH_BOOKS_BY_TITLE_SORTED:
                    {
                        Map<String, String> params = (Map<String, String>) request.getData();
                        String filter = params.get("filter").toLowerCase();
                        String sortOrder = params.get("order");

                        try (Session session = HibernateUtil.getSessionFactory().openSession())
                        {
                            String hql = "FROM Book WHERE LOWER(title) LIKE :title ORDER BY title " + sortOrder;
                            List<Book> bookEntities = session.createQuery(hql, Book.class)
                                    .setParameter("title", "%" + filter + "%")
                                    .getResultList();
                            List<BookDTO> books = convertToDtoList(bookEntities);
                            response = new Response(true, books);
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            response = new Response(false, "Failed to search books");
                        }
                        break;
                    }
                    case GET_BOOK_COUNT_BY_CATEGORY:
                        Map<String, Integer> categoryCounts = new HashMap<>();
                        try (Session session = HibernateUtil.getSessionFactory().openSession())
                        {
                            String hql = "SELECT c.categoryName, COUNT(b) FROM Book b JOIN b.category c GROUP BY c.categoryName";
                            List<Object[]> results = session.createQuery(hql).list();

                            for (Object[] row : results)
                            {
                                String categoryName = (String) row[0];
                                Long count = (Long) row[1];
                                categoryCounts.put(categoryName, count.intValue());
                            }

                            response = new Response(true, categoryCounts);

                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            response = new Response(false, "Failed to get book counts by category");
                        }
                        break;

                    case EXIT:
                    {
                        response = new Response(true, "Goodbye");
                        output.writeObject(response);
                        output.flush();
                        return;
                    }

                    default:
                    {
                        response = new Response(false, "Unknown action");
                        break;
                    }
                }

                output.writeObject(response);
                output.flush();
            }
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                clientSocket.close();
            } catch (IOException ignored)
            {
            }
        }
    }
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
                authorDto = new AuthorDTO(0, "Unknown", "Unknown", ""); // optional default
            }

            CategoryDTO categoryDto = null;
            if (e.getCategory() != null) {
                categoryDto = new CategoryDTO(
                        e.getCategory().getCategoryId(),
                        e.getCategory().getCategoryName()
                );
            } else {
                categoryDto = new CategoryDTO(0, "Unknown"); // optional default
            }

            StatusDTO statusDto = null;
            if (e.getStatus() != null) {
                statusDto = new StatusDTO(
                        e.getStatus().getStatusId(),
                        e.getStatus().getStatusName()
                );
            } else {
                statusDto = new StatusDTO(0, "Unknown"); // optional default
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



    private Book convertToEntity(BookDTO dto)
    {
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
