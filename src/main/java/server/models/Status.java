package server.models;

import jakarta.persistence.*;

import java.util.List;


@Entity
@Table(name = "statuses")
public class Status
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private int statusId;

    @Column(name = "status_name")
    private String statusName;

    @OneToMany(mappedBy = "status")
    private List<Book> books;

    public Status()
    {
    }

    public Status(String statusName, List<Book> books)
    {
        this.statusName = statusName;
        this.books = books;
    }

    public int getStatusId()
    {
        return statusId;
    }

    public void setStatusId(int statusId)
    {
        this.statusId = statusId;
    }

    public String getStatusName()
    {
        return statusName;
    }

    public void setStatusName(String statusName)
    {
        this.statusName = statusName;
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
