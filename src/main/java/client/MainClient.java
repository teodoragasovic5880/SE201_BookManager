package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainClient
{
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 3306);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String message = in.readLine();
            System.out.println("Server says: " + message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
