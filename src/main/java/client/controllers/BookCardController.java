package client.controllers;

import client.ClientService;
import client.dto.AuthorDTO;
import client.dto.BookDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import shared.Action;
import shared.Request;
import shared.Response;

import java.util.HashMap;
import java.util.Map;

public class BookCardController
{
    @FXML
    private Label bookTitle;

    @FXML
    private Label bookAuthor;

    @FXML
    private ImageView bookCover;

    private BookDTO book;

    public void setBook(BookDTO book)
    {
        this.book = book;
        bookTitle.setText(book.getTitle());

        try
        {
            //posto request prima generican Object, a get_entity_by_id zahteva
            //da zna tip objekta kako bi mapirao pravilnu tabeul
            Map<String, Object> data = new HashMap<>();
            data.put("entity", "author");
            data.put("id", book.getAuthor().getAuthorId());

            Request req = new Request(Action.GET_ENTITY_BY_ID, data);
            Response res = ClientService.getInstance().sendRequest(req);

            if (res != null)
            {
                AuthorDTO author = book.getAuthor();
                if (author != null) {
                    bookAuthor.setText("Author: " + author.getAuthorName() + " " + author.getAuthorSurname());
                } else {
                    bookAuthor.setText("Author: Unknown");
                }

            } else
            {
                bookAuthor.setText("Author: Unknown");
            }
        } catch (Exception e)
        {
            bookAuthor.setText("Author: [Error]");
            e.printStackTrace();
        }

        if (book.getCoverImagePath() != null)
        {
            bookCover.setImage(new Image(book.getCoverImagePath(), true));
        }
    }


    public BookDTO getBook()
    {
        return book;
    }
}
