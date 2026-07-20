package com.localservice.backend.service;

import com.localservice.backend.dto.ServiceRequestDTO;
import com.localservice.backend.dto.ServiceResponseDTO;
import com.localservice.backend.exception.ResourceNotFoundException;
import com.localservice.backend.model.User;
import com.localservice.backend.repository.ServiceRepository;
import com.localservice.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceManagementService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmbeddingService embeddingService;

    @Autowired
    private AiTextService aiTextService;

    private ServiceResponseDTO toResponseDTO(com.localservice.backend.model.Service service) {
        return new ServiceResponseDTO(
                service.getId(),
                service.getTitle(),
                service.getDescription(),
                service.getPrice(),
                service.getCategory(),
                service.getProvider().getName(),
                service.getProvider().getId()
        );
    }

    public List<ServiceResponseDTO> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public ServiceResponseDTO getServiceById(Long id) {
        com.localservice.backend.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));
        return toResponseDTO(service);
    }

    public ServiceResponseDTO createService(ServiceRequestDTO dto, String providerEmail) {
        User provider = userRepository.findByEmail(providerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        com.localservice.backend.model.Service service = new com.localservice.backend.model.Service();
        service.setTitle(dto.getTitle());
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setCategory(dto.getCategory());
        service.setProvider(provider);

        String textToEmbed = dto.getTitle() + ". " + (dto.getDescription() != null ? dto.getDescription() : "");
        List<Double> embedding = embeddingService.generateEmbedding(textToEmbed);
        service.setEmbedding(embeddingService.embeddingToString(embedding));

        com.localservice.backend.model.Service saved = serviceRepository.save(service);
        return toResponseDTO(saved);
    }

    public void deleteService(Long id, String requesterEmail) {
        com.localservice.backend.model.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found with id: " + id));

        if (!service.getProvider().getEmail().equals(requesterEmail)) {
            throw new SecurityException("You can only delete your own services");
        }

        serviceRepository.delete(service);
    }

    public org.springframework.data.domain.Page<ServiceResponseDTO> searchServices(
            String title, String category, org.springframework.data.domain.Pageable pageable) {
        org.springframework.data.domain.Page<com.localservice.backend.model.Service> page;

        boolean hasTitle = title != null && !title.isBlank();
        boolean hasCategory = category != null && !category.isBlank();

        if (hasTitle && hasCategory) {
            page = serviceRepository.findByCategoryContainingIgnoreCaseAndTitleContainingIgnoreCase(category, title,
                    pageable);
        } else if (hasTitle) {
            page = serviceRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else if (hasCategory) {
            page = serviceRepository.findByCategoryContainingIgnoreCase(category, pageable);
        } else {
            page = serviceRepository.findAll(pageable);
        }

        return page.map(this::toResponseDTO);
    }

    public List<ServiceResponseDTO> semanticSearch(String query) {
        List<Double> queryEmbedding = embeddingService.generateEmbedding(query);

        List<com.localservice.backend.model.Service> allServices = serviceRepository.findAll();

        return allServices.stream()
                .filter(s -> s.getEmbedding() != null && !s.getEmbedding().isBlank())
                .map(s -> {
                    List<Double> serviceEmbedding = embeddingService.stringToEmbedding(s.getEmbedding());
                    double similarity = embeddingService.cosineSimilarity(queryEmbedding, serviceEmbedding);
                    return new java.util.AbstractMap.SimpleEntry<>(s, similarity);
                })
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(10)
                .map(entry -> toResponseDTO(entry.getKey()))
                .collect(Collectors.toList());
    }

    public int backfillEmbeddings() {
        List<com.localservice.backend.model.Service> allServices = serviceRepository.findAll();
        int count = 0;

        for (com.localservice.backend.model.Service service : allServices) {
            if (service.getEmbedding() == null || service.getEmbedding().isBlank()) {
                String textToEmbed = service.getTitle() + ". " +
                        (service.getDescription() != null ? service.getDescription() : "");
                List<Double> embedding = embeddingService.generateEmbedding(textToEmbed);
                service.setEmbedding(embeddingService.embeddingToString(embedding));
                serviceRepository.save(service);
                count++;
            }
        }

        return count;
    }

    public String chatQuery(String question) {
        List<ServiceResponseDTO> relevantServices = semanticSearch(question);

        StringBuilder context = new StringBuilder();
        if (relevantServices.isEmpty()) {
            context.append("No services are currently available in the marketplace.");
        } else {
            int limit = Math.min(5, relevantServices.size());
            for (int i = 0; i < limit; i++) {
                ServiceResponseDTO s = relevantServices.get(i);
                context.append("- ").append(s.getTitle())
                        .append(" (Category: ").append(s.getCategory())
                        .append(", Price: ₹").append(s.getPrice())
                        .append(", Provider: ").append(s.getProviderName())
                        .append("): ").append(s.getDescription())
                        .append("\n");
            }
        }

        String prompt = "You are a helpful assistant for a local service marketplace app. "
                + "A customer asked: \"" + question + "\"\n\n"
                + "Here are the most relevant services currently available:\n" + context + "\n"
                + "Based ONLY on the services listed above, answer the customer's question helpfully and concisely (2-4 sentences). "
                + "If none of the listed services are actually relevant to their question, politely say so instead of making something up. "
                + "Do not use markdown formatting.";

        return aiTextService.generateText(prompt);
    }
}