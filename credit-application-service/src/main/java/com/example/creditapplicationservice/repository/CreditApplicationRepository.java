package com.example.creditapplicationservice.repository;

import com.example.creditapplicationservice.model.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CreditApplicationRepository extends JpaRepository<CreditApplication, UUID> {

    @Modifying
    @Query("UPDATE CreditApplication c SET c.status = :status WHERE c.applicationId = :applicationId")
    default void updateStatus(@Param("applicationId") String applicationId,
                              @Param("status") String status) {

    }
}