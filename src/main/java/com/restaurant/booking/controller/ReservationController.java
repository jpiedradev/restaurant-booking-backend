package com.restaurant.booking.controller;

import com.restaurant.booking.dto.CreateReservationRequest;
import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.enums.ReservationStatus;
import com.restaurant.booking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * GET /api/reservations
     * Obtiene todas las reservas
     */
    @GetMapping
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/reservations/{id}
     * Obtiene una reserva por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        try {
            ReservationDTO reservation = reservationService.getReservationById(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/reservations/user/{userId}
     * Obtiene reservas de un usuario
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByUser(@PathVariable Long userId) {
        List<ReservationDTO> reservations = reservationService.getReservationsByUser(userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/reservations/date/{date}
     * Obtiene reservas de una fecha específica
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<ReservationDTO>> getReservationsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ReservationDTO> reservations = reservationService.getReservationsByDate(date);
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/reservations/between
     * Obtiene reservas entre dos fechas
     */
    @GetMapping("/between")
    public ResponseEntity<List<ReservationDTO>> getReservationsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ReservationDTO> reservations = reservationService
                .getReservationsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/reservations/today/confirmed
     * Obtiene reservas confirmadas de hoy
     */
    @GetMapping("/today/confirmed")
    public ResponseEntity<List<ReservationDTO>> getTodayConfirmedReservations() {
        List<ReservationDTO> reservations = reservationService.getTodayConfirmedReservations();
        return ResponseEntity.ok(reservations);
    }

    /**
     * GET /api/reservations/check-availability
     * Verifica si una mesa está disponible
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long tableId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        boolean isAvailable = reservationService.isTableAvailable(tableId, date, time);
        return ResponseEntity.ok(isAvailable);
    }

    /**
     * POST /api/reservations
     * Crea una nueva reserva
     */
    @PostMapping
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {
        try {
            ReservationDTO reservation = reservationService.createReservation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PATCH /api/reservations/{id}/status
     * Actualiza el estado de una reserva
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservationDTO> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {
        try {
            ReservationDTO reservation = reservationService.updateReservationStatus(id, status);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PATCH /api/reservations/{id}/cancel
     * Cancela una reserva
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ReservationDTO> cancelReservation(@PathVariable Long id) {
        try {
            ReservationDTO reservation = reservationService.cancelReservation(id);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/reservations/{id}
     * Elimina una reserva
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}