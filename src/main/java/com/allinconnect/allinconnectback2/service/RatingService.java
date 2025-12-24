package com.allinconnect.allinconnectback2.service;

import com.allinconnect.allinconnectback2.entity.Rating;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.repository.RatingRepository;
import com.allinconnect.allinconnectback2.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {

    private static final Logger log = LoggerFactory.getLogger(RatingService.class);
    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public Rating rateUser(User rater, Long ratedId, Integer score, String comment) {
        log.debug("Service: User {} rating user {}", rater.getEmail(), ratedId);
        if (rater.getId().equals(ratedId)) {
            log.debug("User {} tried to rate themselves", rater.getEmail());
            throw new RuntimeException("You cannot rate yourself");
        }

        User rated = userRepository.findById(ratedId)
                .orElseThrow(() -> new RuntimeException("User to be rated not found"));

        if (ratingRepository.findByRaterAndRated(rater, rated).isPresent()) {
            log.debug("User {} has already rated user {}", rater.getEmail(), ratedId);
            throw new RuntimeException("You have already rated this user");
        }

        Rating rating = Rating.builder()
                .rater(rater)
                .rated(rated)
                .score(score)
                .comment(comment)
                .build();

        return ratingRepository.save(rating);
    }

    public List<Rating> getRatingsForUser(Long userId) {
        log.debug("Service: Getting ratings for user {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ratingRepository.findByRated(user);
    }

    public Double getAverageRating(Long userId) {
        log.debug("Service: Calculating average rating for user {}", userId);
        List<Rating> ratings = getRatingsForUser(userId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);
    }
}
