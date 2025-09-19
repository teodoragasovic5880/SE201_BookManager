package shared;


import java.io.Serializable;

// Request from client to server
public class Request implements Serializable
{
    private Action action;  // e.g. "GET_BOOKS", "ADD_BOOK", "DELETE_BOOK"
    private Object data;    // Optional: data for the request, e.g. BookDTO, id, etc.

    public Request(Action action, Object data)
    {
        this.action = action;
        this.data = data;
    }

    public Action getAction()
    {
        return action;
    }

    public Object getData()
    {
        return data;
    }
}
