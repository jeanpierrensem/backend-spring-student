package com.dev.backendspringstudents.dto;

import com.dev.backendspringstudents.enums.PaymentType;
import lombok.*;
import java.util.Date;

@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NewPaymentDto {
    double amount;
    PaymentType type;
    Date date;
    String studentCode ;
}
