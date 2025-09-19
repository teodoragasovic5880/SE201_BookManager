package server.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "books")
public class Book
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private int bookId;

    private String title;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Status status;

    @Column(name = "cover_image_path")
    private String coverImagePath;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    public Book()
    {
    }

    public Book(String title, Status status, String coverImagePath, Author author, Category category)
    {
        this.title = title;
        this.status = status;
        this.coverImagePath = coverImagePath;
        this.author = author;
        this.category = category;
    }

    public int getBookId()
    {
        return bookId;
    }

    public void setBookId(int bookId)
    {
        this.bookId = bookId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public String getCoverImagePath()
    {
        return coverImagePath;
    }

    public void setCoverImagePath(String coverImagePath)
    {
        this.coverImagePath = coverImagePath;
    }

    public Author getAuthor()
    {
        return author;
    }

    public void setAuthor(Author author)
    {
        this.author = author;
    }

    public Category getCategory()
    {
        return category;
    }

    public void setCategory(Category category)
    {
        this.category = category;
    }

    public List<Review> getReviews()
    {
        return reviews;
    }

    public void setReviews(List<Review> reviews)
    {
        this.reviews = reviews;
    }

    public void addReview(Review review)
    {
        reviews.add(review);
    }
}
