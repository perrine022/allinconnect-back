package com.allinconnect.allinconnectback2.controller;

import com.allinconnect.allinconnectback2.dto.RatingRequest;
import com.allinconnect.allinconnectback2.entity.Rating;
import com.allinconnect.allinconnectback2.entity.User;
import com.allinconnect.allinconnectback2.service.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<Rating> rateUser(
            @AuthenticationPrincipal User rater,
            @RequestBody RatingRequest request) {
        return ResponseEntity.ok(ratingService.rateUser(
                rater,
                request.getRatedId(),
                request.getScore(),
                request.getComment()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getRatingsForUser(userId));
    }

    @GetMapping("/user/{userId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getAverageRating(userId));
    }
}
