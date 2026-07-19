package com.localservice.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponseDTO {
    private Long id;
    private String serviceTitle;
    private Long serviceId;
    private String customerName;
    private String providerName;
    private String status;
    private LocalDateTime bookingDate;
}