package server.models;

import jakarta.persistence.*;


@Entity
@Table(name = "reviews")
public class Review
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private int reviewId;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private int rating;

    public Review()
    {
    }

    public Review(int reviewId, Book book, int rating)
    {
        this.reviewId = reviewId;
        this.book = book;
        this.rating = rating;
    }

    public int getReviewId()
    {
        return reviewId;
    }

    public void setReviewId(int reviewId)
    {
        this.reviewId = reviewId;
    }

    public Book getBook()
    {
        return book;
    }

    public void setBook(Book book)
    {
        this.book = book;
    }

    public int getRating()
    {
        return rating;
    }

    public void setRating(int rating)
    {
        this.rating = rating;
    }
}
