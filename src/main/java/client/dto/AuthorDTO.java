package client.dto;


import java.io.Serializable;

public class AuthorDTO implements Serializable
{
    private int authorId;
    private String authorName;
    private String authorSurname;
    private String lifespan;  // maybe store date as string for simplicity

    public AuthorDTO() {}

    public AuthorDTO(int authorId, String authorName, String authorSurname, String lifespan) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.authorSurname = authorSurname;
        this.lifespan = lifespan;
    }

    public int getAuthorId()
    {
        return authorId;
    }

    public void setAuthorId(int authorId)
    {
        this.authorId = authorId;
    }

    public String getAuthorName()
    {
        return authorName;
    }

    public void setAuthorName(String authorName)
    {
        this.authorName = authorName;
    }

    public String getAuthorSurname()
    {
        return authorSurname;
    }

    public void setAuthorSurname(String authorSurname)
    {
        this.authorSurname = authorSurname;
    }

    public String getLifespan()
    {
        return lifespan;
    }

    public void setLifespan(String lifespan)
    {
        this.lifespan = lifespan;
    }
}
