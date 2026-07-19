package com.localservice.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Double price;
    private String category;
    private String providerName;
    private Long providerId;
}