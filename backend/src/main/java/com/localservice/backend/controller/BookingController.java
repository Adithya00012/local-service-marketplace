package com.localservice.backend.controller;

import com.localservice.backend.dto.BookingRequestDTO;
import com.localservice.backend.dto.BookingResponseDTO;
import com.localservice.backend.model.BookingStatus;
import com.localservice.backend.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping
    public BookingResponseDTO createBooking(
            @Valid @RequestBody BookingRequestDTO dto,
            Authentication authentication) {
        return bookingService.createBooking(dto, authentication.getName());
    }

    @GetMapping("/my-bookings")
    public List<BookingResponseDTO> getMyBookings(Authentication authentication) {
        return bookingService.getMyBookings(authentication.getName());
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('PROVIDER')")
    public List<BookingResponseDTO> getReceivedBookings(Authentication authentication) {
        return bookingService.getReceivedBookings(authentication.getName());
    }

    @PutMapping("/{id}/status")
    public BookingResponseDTO updateStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status,
            Authentication authentication) {
        return bookingService.updateStatus(id, status, authentication.getName());
    }
}