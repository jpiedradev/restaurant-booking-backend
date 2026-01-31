package com.restaurant.booking.dto;

import com.restaurant.booking.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String userPhone;
    private Long tableId;
    private Integer tableNumber;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer guests;
    private ReservationStatus status;
    private String specialRequests;
}