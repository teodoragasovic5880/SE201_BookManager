package client;

import shared.Action;
import shared.Request;
import shared.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientService
{
    private static ClientService instance;
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private ClientService() {
        // private constructor to prevent instantiation
    }

    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public void connect(String host, int port) throws IOException
    {
        socket = new Socket(host, port);
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized Response sendRequest(Request request) throws IOException, ClassNotFoundException {
        output.writeObject(request);
        output.flush();

        return (Response) input.readObject();
    }

    public void disconnect() {
        try {
            if (output != null) {
                sendRequest(new Request(Action.EXIT, null)); // polite disconnect
                output.close();
            }
            if (input != null) input.close();
            if (socket != null) socket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
