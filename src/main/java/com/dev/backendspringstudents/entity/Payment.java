package com.dev.backendspringstudents.entity;

import com.dev.backendspringstudents.enums.PaymentStatus;
import com.dev.backendspringstudents.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter@Getter @Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private PaymentType type ;
    private PaymentStatus status;
    private Date date;
    private String file ;
    @ManyToOne
    private Student student;
}
