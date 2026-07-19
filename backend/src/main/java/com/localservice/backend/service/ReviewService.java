package com.localservice.backend.service;

import com.localservice.backend.dto.ReviewRequestDTO;
import com.localservice.backend.dto.ReviewResponseDTO;
import com.localservice.backend.exception.ResourceNotFoundException;
import com.localservice.backend.model.Booking;
import com.localservice.backend.model.BookingStatus;
import com.localservice.backend.model.Review;
import com.localservice.backend.repository.BookingRepository;
import com.localservice.backend.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private ReviewResponseDTO toResponseDTO(Review review) {
        return new ReviewResponseDTO(
                review.getId(),
                review.getRating(),
                review.getComment(),
                review.getBooking().getCustomer().getName(),
                review.getCreatedAt());
    }

    public ReviewResponseDTO createReview(ReviewRequestDTO dto, String customerEmail) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + dto.getBookingId()));

        if (!booking.getCustomer().getEmail().equals(customerEmail)) {
            throw new SecurityException("You can only review your own bookings");
        }

        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new IllegalStateException("You can only review completed bookings");
        }

        if (reviewRepository.findByBookingId(booking.getId()).isPresent()) {
            throw new IllegalStateException("This booking has already been reviewed");
        }

        Review review = new Review();
        review.setBooking(booking);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        Review saved = reviewRepository.save(review);
        return toResponseDTO(saved);
    }

    public List<ReviewResponseDTO> getReviewsForService(Long serviceId) {
        return reviewRepository.findByBookingServiceId(serviceId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
}