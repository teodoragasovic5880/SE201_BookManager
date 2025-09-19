package client.controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MenuButtonsController
{

    private AnchorPane contentPane;

    /**
     * Setter koji omogucava da se contentPane prenese iz MainController-a
     * @param contentPane AnchorPane koji ce se postaviti kao glavni content pane
     */
    public void setContentPane(AnchorPane contentPane)
    {
        this.contentPane = contentPane;
    }

    /**
     * Handler koji se poziva kada se klikne bilo koje dugme iz menija
     * @param event Dogadjaj klika na dugme
     */
    @FXML
    private void handleButtonClick(ActionEvent event)
    {
        // Preuzimanje dugmeta koje je izazvalo dogadjaj
        Button btn = (Button) event.getSource();
        String fxml = null;

        // Na osnovu ID-ja dugmeta se ucitava odgovarajuci FXML
        switch (btn.getId())
        {
            case "kanbanBtn":
                fxml = "/views/kanbanPage.fxml";
                break;
            case "allBooksBtn":
                fxml = "/views/allBooksPage.fxml";
                break;
            case "newBookBtn":
                fxml = "/views/insertBookPage.fxml";
                break;
            case "statisticsBtn":
                fxml = "/views/statisticsPage.fxml";
                break;
        }

        // Ucitavanje novog sadrzaja
        if (fxml != null && contentPane != null)
        {
            try
            {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
                Parent root = loader.load();

                contentPane.getChildren().setAll(root);

                // Proveri da li implementuje Refreshable interface
                Object controller = loader.getController();
                if (controller instanceof Refreshable refreshable)
                {
                    refreshable.refresh();
                }

                AnchorPane.setTopAnchor(root, 0.0);
                AnchorPane.setBottomAnchor(root, 0.0);
                AnchorPane.setLeftAnchor(root, 0.0);
                AnchorPane.setRightAnchor(root, 0.0);

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
