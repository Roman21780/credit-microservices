package com.example.creditapplicationservice.repository;

import com.example.creditapplicationservice.model.CreditApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditApplicationRepository extends JpaRepository<CreditApplication, String> {
}
