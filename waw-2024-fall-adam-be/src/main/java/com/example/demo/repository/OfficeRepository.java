package com.example.demo.repository;

import com.example.demo.model.OfficeEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OfficeRepository extends JpaRepository<OfficeEntity, Integer> {

    Page<OfficeEntity> findAll(Pageable pageable);

    @Query("SELECT o FROM OfficeEntity o WHERE LOWER(o.name) = LOWER(:officeName)")
    Optional<OfficeEntity> findByName(String officeName);
}
