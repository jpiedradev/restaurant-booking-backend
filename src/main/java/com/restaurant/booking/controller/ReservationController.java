package com.restaurant.booking.controller;

import com.restaurant.booking.dto.CreateReservationRequest;
import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.enums.ReservationStatus;
import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.UserRepository;
import com.restaurant.booking.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    private final UserRepository userRepository;

    /**
     * GET /api/reservations
     * ADMIN y STAFF: Ven todas las reservas
     * CUSTOMER: Solo ve sus propias reservas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public List<ReservationDTO> getAllReservations(Authentication authentication) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // Si es CUSTOMER, solo ve sus reservas
        if (role.equals("ROLE_CUSTOMER")) {
            return reservationService.getReservationsByUsername(username);
        }

        // ADMIN y STAFF ven todas
        return reservationService.getAllReservations();
    }

    /**
     * GET /api/reservations/{id}
     * Obtener una reserva específica
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<ReservationDTO> getReservationById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        ReservationDTO reservation = reservationService.getReservationById(id);

        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        // Si es CUSTOMER, verificar que sea su reserva
        if (role.equals("ROLE_CUSTOMER") && !reservation.getUserName().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(reservation);
    }

    /**
     * GET /api/reservations/my-reservations
     * Obtener reservas del usuario actual (CUSTOMER)
     */
    @GetMapping("/my-reservations")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ReservationDTO> getMyReservations(Authentication authentication) {
        String username = authentication.getName();
        return reservationService.getReservationsByUsername(username);
    }

    /**
     * GET /api/reservations/user/{userId}
     * Solo ADMIN y STAFF pueden ver reservas de un usuario específico
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<ReservationDTO> getReservationsByUserId(@PathVariable Long userId) {
        return reservationService.getReservationsByUserId(userId);
    }

    /**
     * GET /api/reservations/date/{date}
     * Solo ADMIN y STAFF
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<ReservationDTO> getReservationsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return reservationService.getReservationsByDate(date);
    }

    /**
     * GET /api/reservations/between
     * Solo ADMIN y STAFF
     */
    @GetMapping("/between")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<ReservationDTO> getReservationsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return reservationService.getReservationsBetweenDates(startDate, endDate);
    }

    /**
     * GET /api/reservations/today/confirmed
     * Solo ADMIN y STAFF
     */
    @GetMapping("/today/confirmed")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<ReservationDTO> getTodayConfirmedReservations() {
        return reservationService.getTodayConfirmedReservations();
    }

    /**
     * GET /api/reservations/check-availability
     * Público (sin autenticación)
     */
    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Long tableId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam LocalTime time
    ) {
        boolean available = reservationService.isTableAvailable(tableId, date, time);
        return ResponseEntity.ok(available);
    }

    /**
     * POST /api/reservations
     * Todos los roles autenticados pueden crear reservas
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody CreateReservationRequest request
    ) {
        ReservationDTO reservation = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    /**
     * PATCH /api/reservations/{id}/status
     * Solo ADMIN y STAFF pueden cambiar estados
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ReservationDTO> updateReservationStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status
    ) {
        ReservationDTO reservation = reservationService.updateReservationStatus(id, status);
        return ResponseEntity.ok(reservation);
    }

    /**
     * PATCH /api/reservations/{id}/cancel
     * CUSTOMER solo puede cancelar sus propias reservas
     * ADMIN y STAFF pueden cancelar cualquiera
     */
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ResponseEntity<ReservationDTO> cancelReservation(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String username = authentication.getName();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        ReservationDTO reservation = reservationService.getReservationById(id);

        // Si es CUSTOMER, verificar que sea su reserva
        if (role.equals("ROLE_CUSTOMER")) {
            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            if (!reservation.getUserId().equals(currentUser.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        ReservationDTO cancelled = reservationService.cancelReservation(id);
        return ResponseEntity.ok(cancelled);
    }

    /**
     * DELETE /api/reservations/{id}
     * Solo ADMIN puede eliminar reservas
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}