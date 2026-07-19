package com.localservice.backend.service;

import com.localservice.backend.dto.BookingRequestDTO;
import com.localservice.backend.dto.BookingResponseDTO;
import com.localservice.backend.exception.ResourceNotFoundException;
import com.localservice.backend.model.Booking;
import com.localservice.backend.model.BookingStatus;
import com.localservice.backend.model.Service;
import com.localservice.backend.model.User;
import com.localservice.backend.repository.BookingRepository;
import com.localservice.backend.repository.ServiceRepository;
import com.localservice.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    private BookingResponseDTO toResponseDTO(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getService().getTitle(),
                booking.getService().getId(),
                booking.getCustomer().getName(),
                booking.getService().getProvider().getName(),
                booking.getStatus().name(),
                booking.getBookingDate());
    }

    public BookingResponseDTO createBooking(BookingRequestDTO dto, String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + dto.getServiceId()));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setBookingDate(dto.getBookingDate());
        booking.setStatus(BookingStatus.PENDING);

        Booking saved = bookingRepository.save(booking);
        return toResponseDTO(saved);
    }

    // Bookings made BY this customer
    public List<BookingResponseDTO> getMyBookings(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    // Bookings received BY this provider (for their services)
    public List<BookingResponseDTO> getReceivedBookings(String providerEmail) {
        User provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByServiceProviderId(provider.getId())
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public BookingResponseDTO updateStatus(Long bookingId, BookingStatus newStatus, String requesterEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        String providerEmail = booking.getService().getProvider().getEmail();
        if (!providerEmail.equals(requesterEmail)) {
            throw new SecurityException("Only the service provider can update this booking's status");
        }

        booking.setStatus(newStatus);
        Booking updated = bookingRepository.save(booking);
        return toResponseDTO(updated);
    }
}