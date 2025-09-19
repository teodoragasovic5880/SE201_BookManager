package client.dto;

import java.io.Serializable;
public class ReviewDTO implements Serializable {
    private int reviewId;
    private int bookId;
    private int rating;

    public ReviewDTO() {}

    public ReviewDTO(int reviewId, int bookId, int rating) {
        this.reviewId = reviewId;
        this.bookId = bookId;
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

    public int getBookId()
    {
        return bookId;
    }

    public void setBookId(int bookId)
    {
        this.bookId = bookId;
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
