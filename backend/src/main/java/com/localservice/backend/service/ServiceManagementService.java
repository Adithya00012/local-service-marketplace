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
}