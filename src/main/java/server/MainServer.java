package server;

import server.dao.BookDAO;
import server.service.BookService;
import server.util.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainServer
{
    public static void main(String[] args)
    {
        try (ServerSocket serverSocket = new ServerSocket(5555))
        {
            System.out.println("Server started on port 5555");

            Socket clientSocket = serverSocket.accept();
            ClientHandler handler = new ClientHandler(clientSocket, new BookService(new BookDAO()));

            handler.handleClient();
            System.out.println("Client connected " + clientSocket.getInetAddress());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}

