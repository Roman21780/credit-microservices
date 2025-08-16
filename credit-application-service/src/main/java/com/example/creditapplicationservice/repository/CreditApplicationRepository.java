package com.example.creditapplicationservice.repository;

import com.example.creditapplicationservice.model.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, UUID> {

    @Query("SELECT c.status FROM CreditApplication c WHERE c.id = :id")
    Optional<String> findStatusById(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE CreditApplication c SET c.status = :status WHERE c.id = :applicationId")
    void updateStatus(@Param("applicationId") UUID applicationId,
                     @Param("status") CreditApplication.Status status);
}
