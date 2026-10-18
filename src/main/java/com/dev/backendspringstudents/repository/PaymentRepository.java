package com.dev.backendspringstudents.repository;

import com.dev.backendspringstudents.entity.Payment;
import com.dev.backendspringstudents.enums.PaymentStatus;
import com.dev.backendspringstudents.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
 List<Payment> findByStudentCode(String code);
 Payment findByStatus(PaymentStatus status);
 Payment findByType(PaymentType type);
}
