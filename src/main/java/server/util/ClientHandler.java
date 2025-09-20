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
import server.service.BookService;
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

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ClientHandler {
    private Socket clientSocket;
    private BookService bookService;
    private AuthorDAO authorDao;
    private CategoryDAO categoryDao;
    private StatusDAO statusDao;

    public ClientHandler(Socket clientSocket, BookService bookService) {
        this.clientSocket = clientSocket;
        this.bookService = bookService;
        this.authorDao = new AuthorDAO();
        this.categoryDao = new CategoryDAO();
        this.statusDao = new StatusDAO();
    }

    public void handleClient() {
        try (
                ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream())
        ) {
            while (true) {
                Request request = (Request) input.readObject();
                Response response = null;

                switch (request.getAction()) {

                    case GET_ENTITY_BY_ID: {
                        Map<String, Object> data = (Map<String, Object>) request.getData();
                        String entity = (String) data.get("entity");
                        int id = (int) data.get("id");

                        Object result = null;

                        switch (entity.toLowerCase()) {
                            case "author":
                                Author author = authorDao.findById(id);
                                if (author != null) {
                                    result = new AuthorDTO(
                                            author.getAuthorId(),
                                            author.getAuthorName(),
                                            author.getAuthorSurname(),
                                            author.getLifespan()
                                    );
                                }
                                break;
                            case "category":
                                Category category = categoryDao.findById(id);
                                if (category != null) {
                                    result = new CategoryDTO(
                                            category.getCategoryId(),
                                            category.getCategoryName()
                                    );
                                }
                                break;
                            case "status":
                                Status status = statusDao.findById(id);
                                if (status != null) {
                                    result = new StatusDTO(
                                            status.getStatusId(),
                                            status.getStatusName()
                                    );
                                }
                                break;
                            default:
                                response = new Response(false, "Unknown entity type");
                        }

                        if (result != null) {
                            response = new Response(true, result);
                        } else if (response == null) {
                            response = new Response(false, "Entity not found");
                        }
                        break;
                    }

                    case GET_BOOKS:
                        response = bookService.getAllBooks();
                        break;

                    case ADD_BOOK:
                        response = bookService.addBook((BookDTO) request.getData());
                        break;

                    case UPDATE_BOOK:
                        response = bookService.updateBook((BookDTO) request.getData());
                        break;

                    case DELETE_BOOK:
                        response = bookService.deleteBook((Integer) request.getData());
                        break;

                    case READ_ALL_BOOKS_SORTED:
                        response = bookService.readAllBooksSorted((String) request.getData());
                        break;

                    case SEARCH_BOOKS_BY_TITLE_SORTED: {
                        Map<String, String> params = (Map<String, String>) request.getData();
                        response = bookService.searchBooks(params.get("filter"), params.get("order"));
                        break;
                    }

                    case GET_BOOK_COUNT_BY_CATEGORY:
                        response = bookService.getBookCountByCategory();
                        break;

                    case EXIT:
                        response = new Response(true, "Goodbye");
                        output.writeObject(response);
                        output.flush();
                        return;

                    default:
                        response = new Response(false, "Unknown action");
                }

                output.writeObject(response);
                output.flush();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
