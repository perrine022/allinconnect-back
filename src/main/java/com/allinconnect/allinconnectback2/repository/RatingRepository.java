package com.allinconnect.allinconnectback2.repository;

import com.allinconnect.allinconnectback2.entity.Rating;
import com.allinconnect.allinconnectback2.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByRated(User rated);
    List<Rating> findByRater(User rater);
    Optional<Rating> findByRaterAndRated(User rater, User rated);
}
