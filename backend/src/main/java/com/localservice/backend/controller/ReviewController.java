package com.localservice.backend.controller;

import com.localservice.backend.dto.ReviewRequestDTO;
import com.localservice.backend.dto.ReviewResponseDTO;
import com.localservice.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public ReviewResponseDTO createReview(
            @Valid @RequestBody ReviewRequestDTO dto,
            Authentication authentication) {
        return reviewService.createReview(dto, authentication.getName());
    }

    @GetMapping("/service/{serviceId}")
    public List<ReviewResponseDTO> getReviewsForService(@PathVariable Long serviceId) {
        return reviewService.getReviewsForService(serviceId);
    }

    @GetMapping("/service/{serviceId}/summary")
    public java.util.Map<String, String> getReviewSummary(@PathVariable Long serviceId) {
        String summary = reviewService.summarizeReviews(serviceId);
        return java.util.Map.of("summary", summary);
    }
}