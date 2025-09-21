package client.controllers;

import client.dto.BookDTO;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import server.dao.ReviewDAO;
import server.service.ReviewService;

public class BookCellController
{
    @FXML
    private Text bookTitle;

    @FXML
    private ImageView image;

    @FXML
    private TextFlow authorNameSur;

    @FXML
    private TextFlow category;

    @FXML
    private TextFlow status;

    @FXML
    private TextFlow reviews; // Add this

    private ReviewService reviewService;

    public BookCellController() {
        this.reviewService = new ReviewService(new ReviewDAO()); // or inject DAO if using DI
    }

    public void setBookData(BookDTO bookDto) {
        bookTitle.setText(bookDto.getTitle());

        // Author
        authorNameSur.getChildren().clear();
        Text authorLabel = new Text("Author: ");
        Text authorValue = new Text(bookDto.getAuthor() != null
                ? bookDto.getAuthor().getAuthorName() + " " + bookDto.getAuthor().getAuthorSurname()
                : "Unknown");
        authorValue.getStyleClass().add("bookCellValue");
        authorNameSur.getChildren().addAll(authorLabel, authorValue);

        // Category
        category.getChildren().clear();
        Text categoryLabel = new Text("Category: ");
        Text categoryValue = new Text(bookDto.getCategory() != null
                ? bookDto.getCategory().getCategoryName()
                : "Unknown");
        categoryValue.getStyleClass().add("bookCellValue");
        category.getChildren().addAll(categoryLabel, categoryValue);

        // Status
        status.getChildren().clear();
        Text statusLabel = new Text("Status: ");
        Text statusValue = new Text(bookDto.getStatus() != null
                ? bookDto.getStatus().getStatusName()
                : "Unknown");
        statusValue.getStyleClass().add("bookCellValue");
        status.getChildren().addAll(statusLabel, statusValue);

        // Image
        try {
            Image img = new Image(bookDto.getCoverImagePath(), true);
            image.setImage(img);
        } catch (Exception e) {
            System.out.println("Failed to load image: " + bookDto.getCoverImagePath());
        }

        // Reviews
        reviews.getChildren().clear();
        Text reviewLabel = new Text("Avg. Rating: ");
        double averageRating = reviewService.getAverageRatingForBook(bookDto.getId());
        Text reviewValue = new Text(averageRating > 0 ? String.format("%.2f / 5", averageRating) : "No reviews");
        reviewValue.getStyleClass().add("bookCellValue");
        reviews.getChildren().addAll(reviewLabel, reviewValue);
    }

}

