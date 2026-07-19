package com.localservice.backend.controller;

import com.localservice.backend.dto.ServiceRequestDTO;
import com.localservice.backend.dto.ServiceResponseDTO;
import com.localservice.backend.service.ServiceManagementService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {

    @Autowired
    private ServiceManagementService serviceManagementService;

    @GetMapping
    public org.springframework.data.domain.Page<ServiceResponseDTO> getAllServices(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size,
                org.springframework.data.domain.Sort.by(sortBy));

        return serviceManagementService.searchServices(title, category, pageable);
    }

    @GetMapping("/{id}")
    public ServiceResponseDTO getServiceById(@PathVariable Long id) {
        return serviceManagementService.getServiceById(id);
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @PostMapping
    public ServiceResponseDTO createService(
            @Valid @RequestBody ServiceRequestDTO dto,
            Authentication authentication
    ) {
        String providerEmail = authentication.getName();
        return serviceManagementService.createService(dto, providerEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(
            @PathVariable Long id,
            Authentication authentication
    ) {
        serviceManagementService.deleteService(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}