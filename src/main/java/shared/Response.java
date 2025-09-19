package shared;

import java.io.Serializable;

// Response from server to client
public class Response implements Serializable
{
    private boolean success;
    private Object data;     // e.g. List<BookDTO>, message, error info

    public Response(boolean success, Object data)
    {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public Object getData()
    {
        return data;
    }
}
