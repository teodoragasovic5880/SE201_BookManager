package server.dao;

import server.models.Review;

public class ReviewDAO extends BaseDAO<Review, Integer> {
    public ReviewDAO() {
        super(Review.class);
    }
}
