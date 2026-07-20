package com.localservice.backend.controller;

import com.localservice.backend.dto.ServiceRequestDTO;
import com.localservice.backend.dto.ServiceResponseDTO;
import com.localservice.backend.service.GeminiService;
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

    @Autowired
    private GeminiService geminiService;

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
            Authentication authentication) {
        String providerEmail = authentication.getName();
        return serviceManagementService.createService(dto, providerEmail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(
            @PathVariable Long id,
            Authentication authentication) {
        serviceManagementService.deleteService(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @PostMapping("/generate-description")
    public java.util.Map<String, String> generateDescription(
            @RequestBody java.util.Map<String, String> request) {
        String title = request.get("title");
        String keywords = request.getOrDefault("keywords", "");

        String prompt = "Write a short, professional, appealing service listing description "
                + "(2-3 sentences, no markdown, no headers) for a local service marketplace. "
                + "Service title: " + title + ". "
                + "Key details to include: " + keywords;

        String description = geminiService.generateText(prompt);
        return java.util.Map.of("description", description.trim());
    }

    @GetMapping("/search/semantic")
    public List<ServiceResponseDTO> semanticSearch(@RequestParam String query) {
        return serviceManagementService.semanticSearch(query);
    }

    @PreAuthorize("hasRole('PROVIDER')")
    @PostMapping("/backfill-embeddings")
    public java.util.Map<String, Object> backfillEmbeddings() {
        int updated = serviceManagementService.backfillEmbeddings();
        return java.util.Map.of("servicesUpdated", updated);
    }

    @GetMapping("/chat")
    public java.util.Map<String, String> chat(@RequestParam String question) {
        String answer = serviceManagementService.chatQuery(question);
        return java.util.Map.of("answer", answer);
    }
}