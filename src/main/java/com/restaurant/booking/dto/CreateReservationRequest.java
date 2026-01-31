package com.restaurant.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long userId;

    @NotNull(message = "El ID de mesa es obligatorio")
    private Long tableId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate reservationDate;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime reservationTime;

    @NotNull(message = "El número de comensales es obligatorio")
    @Min(value = 1, message = "Debe haber al menos 1 comensal")
    private Integer guests;

    private String specialRequests;
}