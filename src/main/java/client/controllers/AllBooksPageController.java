package client.controllers;


import client.MainClient;
import client.dto.BookDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import shared.Action;
import shared.Request;
import shared.Response;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllBooksPageController implements Refreshable {

    @FXML
    private ComboBox<String> sortComboBox;

    @FXML
    private TextField searchByTitle;

    @FXML
    private ListView<Node> allBooksList;

    private final ObservableList<Node> bookCards = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        sortComboBox.getItems().addAll("Ascending", "Descending");
        sortComboBox.getSelectionModel().selectFirst();

        allBooksList.setItems(bookCards);
        allBooksList.setFocusTraversable(false);

        searchByTitle.textProperty().addListener((obs, oldVal, newVal) -> reloadBooks());
        sortComboBox.setOnAction(e -> reloadBooks());

        reloadBooks();
    }

    private void reloadBooks() {
        String filter = searchByTitle.getText();
        String sortOrder = sortComboBox.getSelectionModel().getSelectedItem().equalsIgnoreCase("Descending") ? "DESC" : "ASC";
        loadBooks(filter, sortOrder);
    }

    private void loadBooks(String filter, String sortOrder) {
        bookCards.clear();
        try {
            Request request;
            if (filter == null || filter.isEmpty()) {
                request = new Request(Action.READ_ALL_BOOKS_SORTED, sortOrder);
            } else {
                Map<String, String> params = new HashMap<>();
                params.put("filter", filter);
                params.put("order", sortOrder);
                request = new Request(Action.SEARCH_BOOKS_BY_TITLE_SORTED, params);
            }

            Response response = MainClient.getClientService().sendRequest(request);

            if (response.isSuccess()) {
                List<BookDTO> books = (List<BookDTO>) response.getData();

                for (BookDTO book : books) {
                    Node card = createBookCard(book);
                    if (card != null) bookCards.add(card);
                }
            } else {
                System.out.println("Failed to load books.");
            }

        } catch (Exception e) {
            System.out.println("Error loading books.");
            e.printStackTrace();
        }
    }

    private Node createBookCard(BookDTO bookDto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/bookCell.fxml"));
            HBox cardRoot = loader.load();
            BookCellController controller = loader.getController();
            controller.setBookData(bookDto);
            return cardRoot;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void refresh() {
        reloadBooks();
    }
}
