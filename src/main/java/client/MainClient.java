package client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainClient extends Application
{

    private static ClientService clientService;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Connect to server when app starts
        clientService = ClientService.getInstance();
        clientService.connect("localhost", 5555);

        Parent root = FXMLLoader.load(getClass().getResource("/views/mainPage.fxml"));

        primaryStage.setTitle("Book Client");
        primaryStage.setScene(new Scene(root, 1280, 800));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        // Disconnect from server when app closes
        if (clientService != null) {
            clientService.disconnect();
        }
        super.stop();
    }

    public static ClientService getClientService() {
        return clientService;
    }

    public static void main(String[] args) {
        launch(args);
    }
}