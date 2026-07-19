package com.localservice.backend.repository;

import com.localservice.backend.model.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    Page<Service> findByCategoryContainingIgnoreCaseAndTitleContainingIgnoreCase(
            String category, String title, Pageable pageable);

    Page<Service> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Service> findByCategoryContainingIgnoreCase(String category, Pageable pageable);
}