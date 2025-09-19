package server.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "authors")
public class Author
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private int authorId;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "author_surname")
    private String authorSurname;

    @Column(name = "lifespan")
    private String lifespan;

    @OneToMany(mappedBy = "author")
    private List<Book> books;

    public Author()
    {
    }

    public Author(String authorName, String authorSurname, String lifespan, List<Book> books)
    {
        this.authorName = authorName;
        this.authorSurname = authorSurname;
        this.lifespan = lifespan;
        this.books = books;
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

    public List<Book> getBooks()
    {
        return books;
    }

    public void setBooks(List<Book> books)
    {
        this.books = books;
    }
}
