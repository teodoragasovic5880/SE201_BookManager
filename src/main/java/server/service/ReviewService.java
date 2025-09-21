package server.service;

import client.dto.ReviewDTO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import server.dao.ReviewDAO;
import server.util.HibernateUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReviewService {
    private ReviewDAO reviewDao;
    private final SessionFactory sessionFactory;

    public ReviewService(ReviewDAO reviewDao) {
        this(reviewDao, HibernateUtil.getSessionFactory());
    }

    public ReviewService(ReviewDAO reviewDao, SessionFactory sessionFactory) {
        this.reviewDao = reviewDao;
        this.sessionFactory = sessionFactory;
    }

    // Returns average rating for a book
    public double getAverageRatingForBook(int bookId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT AVG(r.rating) FROM Review r WHERE r.book.bookId = :bookId";
            Double average = session.createQuery(hql, Double.class)
                    .setParameter("bookId", bookId)
                    .uniqueResult();
            return average != null ? average : 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Returns all reviews as ReviewDTOs
    public List<ReviewDTO> getReviewsForBook(int bookId) {
        try (Session session = sessionFactory.openSession()) {
            String hql = "SELECT r.reviewId, r.book.bookId, r.rating FROM Review r WHERE r.book.bookId = :bookId";
            List<Object[]> results = session.createQuery(hql).setParameter("bookId", bookId).getResultList();
            List<ReviewDTO> dtos = new ArrayList<>();
            for (Object[] row : results) {
                dtos.add(new ReviewDTO(
                        (int) row[0],
                        (int) row[1],
                        (int) row[2]
                ));
            }
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
