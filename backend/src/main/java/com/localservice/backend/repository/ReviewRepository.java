package com.localservice.backend.repository;

import com.localservice.backend.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByBookingId(Long bookingId);

    List<Review> findByBookingServiceId(Long serviceId);
}