package client.controllers;


import client.ClientService;
import client.dto.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import shared.Action;
import shared.Request;
import shared.Response;
import sqlinjectiondemo.UnsafeBookDAO;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Kontroler zaduzen za ubacivanje nove knjige putem forme.
 * Verifikuje ulaz korisnika, i salje novonastali objekat serveru
 * kako bi ga ubacio u bazu podataka.
 */
public class InsertBookPageController
{

    @FXML
    private TextField titleField;
    @FXML private TextField authorNameField;
    @FXML private TextField authorSurnameField;
    @FXML private DatePicker authorDobPicker;
    @FXML private TextField categoryField;
    @FXML private TextField imagePathField;
    @FXML private ComboBox<String> statusComboBox;

    @FXML private Text titleErrorLabel;
    @FXML private Text authorNameErrorLabel;
    @FXML private Text authorSurnameErrorLabel;
    @FXML private Text authorDobErrorLabel;
    @FXML private Text categoryErrorLabel;
    @FXML private Text imagePathErrorLabel;
    @FXML private Text statusErrorLabel;

    private final Map<String, Integer> statusNameToId = new HashMap<>();

    @FXML
    public void initialize() {
        statusNameToId.put("Not Read", 1);
        statusNameToId.put("Reading", 3);
        statusNameToId.put("Read", 2);

        statusComboBox.getItems().addAll(statusNameToId.keySet());
        statusComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSubmit() {
        clearErrorLabels();

        boolean valid = true;

        String title = titleField.getText();
        if (title == null || title.trim().isEmpty() || title.length() > 50) {
            titleErrorLabel.setText("Book title must be non-empty and max 50 characters.");
            valid = false;
        }

        String authorName = authorNameField.getText();
        if (authorName == null || authorName.trim().isEmpty() || authorName.length() > 50) {
            authorNameErrorLabel.setText("Author name must be non-empty and max 50 characters.");
            valid = false;
        }

        String authorSurname = authorSurnameField.getText();
        if (authorSurname == null || authorSurname.trim().isEmpty() || authorSurname.length() > 50) {
            authorSurnameErrorLabel.setText("Author surname must be non-empty and max 50 characters.");
            valid = false;
        }

        LocalDate authorDob = authorDobPicker.getValue();
        if (authorDob == null || authorDob.isAfter(LocalDate.now())) {
            authorDobErrorLabel.setText("Author date of birth must be set and in the past.");
            valid = false;
        }

        String category = categoryField.getText();
        if (category == null || category.trim().isEmpty()) {
            categoryErrorLabel.setText("Category must not be empty.");
            valid = false;
        }

        String imagePath = imagePathField.getText();
        if (imagePath == null || !imagePath.matches("^https?://.*\\.(jpg|jpeg|png|gif|bmp)$")) {
            imagePathErrorLabel.setText("Cover image path must be a valid image URL.");
            valid = false;
        }

        if (!valid) return;

        String statusName = statusComboBox.getValue();
        Integer statusId = statusNameToId.get(statusName);
        if (statusId == null) {
            statusErrorLabel.setText("Status must be selected.");
            return;
        }

        // Create DTOs matching your server structure
        AuthorDTO authorDto = new AuthorDTO(
                0,  // id = 0 for new author, or load if editing
                authorNameField.getText(),
                authorSurnameField.getText(),
                authorDobPicker.getValue().toString()  // lifespan string, or format it nicely
        );
        CategoryDTO categoryDto = new CategoryDTO(
                0,  // new category id (or fetched)
                categoryField.getText()
        );
        StatusDTO statusDto = new StatusDTO(statusId, statusName);
        List<ReviewDTO> reviews = new ArrayList<>(); // empty list for new book

        BookDTO bookDto = new BookDTO(
                0,
                title,
                authorDto,
                categoryDto,
                statusDto,
                imagePath
        );

        // Prepare request
        Request request = new Request(Action.ADD_BOOK, bookDto);

        try {
            ClientService clientService = ClientService.getInstance(); // adapt if singleton or inject
            Response response = clientService.sendRequest(request);

            if (response.isSuccess()) {
                System.out.println("Book successfully inserted");
                clearForm();
            } else {
                System.out.println("Failed to insert book");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Communication error with server");
        }
    }

    @FXML
    private void handleSubmitUnsafe() {
        // Read fields directly (no validation here — intentional for demo)
        String title = titleField.getText();
        String authorName = authorNameField.getText();
        String authorSurname = authorSurnameField.getText();
        String authorFull = (authorName == null ? "" : authorName) + " " + (authorSurname == null ? "" : authorSurname);
        String category = categoryField.getText();
        String imagePath = imagePathField.getText();

        try {
            UnsafeBookDAO dao = new UnsafeBookDAO();
            dao.init(); // ensure table exists
            dao.addBookUnsafe(title, authorFull, category, imagePath);
            System.out.println("Unsafe demo: statement executed. Check DB and console for SQL printed by DAO.");
        } catch (SQLException e) {
            System.err.println("Unsafe demo failed: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void clearForm() {
        titleField.clear();
        authorNameField.clear();
        authorSurnameField.clear();
        authorDobPicker.setValue(null);
        categoryField.clear();
        imagePathField.clear();
        statusComboBox.getSelectionModel().selectFirst();
    }

    private void clearErrorLabels() {
        titleErrorLabel.setText("");
        authorNameErrorLabel.setText("");
        authorSurnameErrorLabel.setText("");
        authorDobErrorLabel.setText("");
        categoryErrorLabel.setText("");
        imagePathErrorLabel.setText("");
        statusErrorLabel.setText("");
    }
}
