package com.localservice.backend.service;

import com.localservice.backend.dto.ServiceRequestDTO;
import com.localservice.backend.dto.ServiceResponseDTO;
import com.localservice.backend.exception.ResourceNotFoundException;
import com.localservice.backend.model.User;
import com.localservice.backend.repository.ServiceRepository;
import com.localservice.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceManagementServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ServiceManagementService serviceManagementService;

    private User testProvider;

    @BeforeEach
    void setUp() {
        testProvider = new User();
        testProvider.setId(1L);
        testProvider.setName("Test Provider");
        testProvider.setEmail("provider@example.com");
        testProvider.setRole("PROVIDER");
    }

    @Test
    void createService_shouldSucceed_whenProviderExists() {
        // Arrange: set up fake data and fake behavior
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setTitle("Test Service");
        dto.setPrice(100.0);
        dto.setCategory("Testing");

        com.localservice.backend.model.Service savedService = new com.localservice.backend.model.Service();
        savedService.setId(1L);
        savedService.setTitle("Test Service");
        savedService.setPrice(100.0);
        savedService.setCategory("Testing");
        savedService.setProvider(testProvider);

        when(userRepository.findByEmail("provider@example.com"))
                .thenReturn(Optional.of(testProvider));
        when(serviceRepository.save(org.mockito.ArgumentMatchers.any()))
                .thenReturn(savedService);

        // Act: call the actual method under test
        ServiceResponseDTO result = serviceManagementService.createService(dto, "provider@example.com");

        // Assert: verify the outcome is correct
        assertThat(result.getTitle()).isEqualTo("Test Service");
        assertThat(result.getProviderName()).isEqualTo("Test Provider");
        assertThat(result.getPrice()).isEqualTo(100.0);
    }

    @Test
    void createService_shouldThrow_whenProviderNotFound() {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setTitle("Test Service");
        dto.setPrice(100.0);

        when(userRepository.findByEmail("ghost@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> serviceManagementService.createService(dto, "ghost@example.com"));
    }

    @Test
    void getServiceById_shouldThrow_whenServiceNotFound() {
        when(serviceRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceManagementService.getServiceById(999L));
    }
}