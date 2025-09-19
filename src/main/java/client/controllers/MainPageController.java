package client.controllers;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MainPageController
{
    /** Glavni panel */
    @FXML
    private AnchorPane contentPane;

    /** Kontroler za meni dugmica */
    @FXML
    private MenuButtonsController menuButtonsController;

    /** Panel u koji će se ubaciti meni dugmica */
    @FXML
    private AnchorPane menuButtonsPane;

    @FXML
    public void initialize()
    {
        try
        {
            // Ucitavanje FXML fajla koji sadrzi meni dugmića
            FXMLLoader buttonsLoader = new FXMLLoader(getClass().getResource("/views/menuButtons.fxml"));

            // Ucitavanje celog sadrzaja tog fajla kao AnchorPane objekat
            AnchorPane buttons = buttonsLoader.load();

            // Dobijanje kontrolera za dugmice iz loadera
            menuButtonsController = buttonsLoader.getController();

            // Postavljanje contentPane iz glavnog kontrolera u meni kontroler
            // kako bi dugmici mogli da menjaju sadrzaj
            menuButtonsController.setContentPane(contentPane);

            // Ubacivanje ucitanog menija u odgovarajuci panel
            menuButtonsPane.getChildren().add(buttons);

            // Podesavanje da se meni prostire celom povrsinom svog panela
            AnchorPane.setTopAnchor(buttons, 0.0);
            AnchorPane.setBottomAnchor(buttons, 0.0);
            AnchorPane.setLeftAnchor(buttons, 0.0);
            AnchorPane.setRightAnchor(buttons, 0.0);

            //Ista postavka i za pocetni sadrzaj
            Parent initialContent = FXMLLoader.load(getClass().getResource("/views/kanbanPage.fxml"));
            contentPane.getChildren().setAll(initialContent);
            AnchorPane.setTopAnchor(initialContent, 0.0);
            AnchorPane.setBottomAnchor(initialContent, 0.0);
            AnchorPane.setLeftAnchor(initialContent, 0.0);
            AnchorPane.setRightAnchor(initialContent, 0.0);

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
