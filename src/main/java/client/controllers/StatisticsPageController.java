package client.controllers;


import client.ClientService;
import client.OpenLibraryScraper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import shared.Action;
import shared.Request;
import shared.Response;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class StatisticsPageController implements Refreshable {

    @FXML
    private ImageView bookImage1;
    @FXML private Text bookTitle1;
    @FXML private ImageView bookImage2;
    @FXML private Text bookTitle2;
    @FXML private ImageView bookImage3;
    @FXML private Text bookTitle3;
    @FXML private ImageView bookImage4;
    @FXML private Text bookTitle4;
    @FXML private ImageView bookImage5;
    @FXML private Text bookTitle5;

    @FXML private PieChart categoryChart;

    private List<ImageView> bookImages;
    private List<Text> bookTitles;

    @FXML
    public void initialize() {
        bookImages = List.of(bookImage1, bookImage2, bookImage3, bookImage4, bookImage5);
        bookTitles = List.of(bookTitle1, bookTitle2, bookTitle3, bookTitle4, bookTitle5);

        loadCategoryStatistics();
        loadTrendingBooks();
    }

    private void loadCategoryStatistics() {
        new Thread(() -> {
            try {
                // Build and send request for category statistics
                Request request = new Request(Action.GET_BOOK_COUNT_BY_CATEGORY, null);
                Response response = ClientService.getInstance().sendRequest(request);

                if (response.isSuccess()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Integer> stats = (Map<String, Integer>) response.getData();

                    Platform.runLater(() -> {
                        categoryChart.getData().clear();

                        stats.forEach((category, count) -> {
                            PieChart.Data slice = new PieChart.Data(category, count);
                            categoryChart.getData().add(slice);
                        });

                        categoryChart.getData().forEach(data -> data.getNode().setStyle("-fx-opacity: 0.6;"));

                        categoryChart.setTitle(""); // or set meaningful title if needed
                    });
                } else {
                    Platform.runLater(() -> {
                        System.out.println("Failed to load category statistics.");
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    System.out.println("Error loading category statistics.");
                });
            }
        }).start();
    }

    private void loadTrendingBooks() {
        new Thread(() -> {
            try {
                List<OpenLibraryScraper.Book> books = OpenLibraryScraper.fetchBooks();

                Platform.runLater(() -> {
                    for (int i = 0; i < Math.min(books.size(), 5); i++) {
                        OpenLibraryScraper.Book book = books.get(i);
                        bookTitles.get(i).setText(book.title);

                        Image image = new Image(book.imageUrl, 100, 150, true, true);
                        bookImages.get(i).setImage(image);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    System.out.println("Failed to load trending books.");

                });
            }
        }).start();
    }

    @Override
    public void refresh() {
        loadCategoryStatistics();
        loadTrendingBooks();
    }
}
