package client.controllers;

import client.dto.BookDTO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class BookCellController
{

    @FXML
    private ImageView image;

    @FXML
    private Text bookTitle;

    @FXML
    private Text authorNameSur;

    @FXML
    private Text category;

    @FXML
    private Text status;

    public void setBookData(BookDTO bookDto) {
        bookTitle.setText(bookDto.getTitle());
        authorNameSur.setText("Author: " + bookDto.getAuthor().getAuthorName() + " " + bookDto.getAuthor().getAuthorSurname());
        category.setText("Category: " + bookDto.getCategory().getCategoryName());
        status.setText("Status: " + bookDto.getStatus().getStatusName());

        try {
            Image img = new Image(bookDto.getCoverImagePath(), true);
            image.setImage(img);
        } catch (Exception e) {
            System.out.println("Failed to load image: " + bookDto.getCoverImagePath());
        }
    }
}

