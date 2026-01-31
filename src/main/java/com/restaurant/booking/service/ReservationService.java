package com.restaurant.booking.service;

import com.restaurant.booking.dto.CreateReservationRequest;
import com.restaurant.booking.dto.ReservationDTO;
import com.restaurant.booking.enums.ReservationStatus;
import com.restaurant.booking.enums.TableStatus;
import com.restaurant.booking.model.Reservation;
import com.restaurant.booking.model.RestaurantTable;
import com.restaurant.booking.model.User;
import com.restaurant.booking.repository.ReservationRepository;
import com.restaurant.booking.repository.RestaurantTableRepository;
import com.restaurant.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;

    // ==================== MÉTODOS CRUD ====================

    /**
     * Obtiene todas las reservas
     */
    public List<ReservationDTO> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una reserva por ID
     */
    public ReservationDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));
        return convertToDTO(reservation);
    }

    /**
     * Crea una nueva reserva
     */
    public ReservationDTO createReservation(CreateReservationRequest request) {
        // 1. Validar que el usuario existe
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getUserId()));

        // 2. Validar que la mesa existe
        RestaurantTable table = tableRepository.findById(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + request.getTableId()));

        // 3. Validar que la fecha no sea en el pasado
        if (request.getReservationDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se puede reservar para una fecha pasada");
        }

        // 4. Validar que la mesa tenga capacidad suficiente
        if (table.getCapacity() < request.getGuests()) {
            throw new RuntimeException("La mesa no tiene capacidad suficiente. Capacidad: "
                    + table.getCapacity() + ", Solicitado: " + request.getGuests());
        }

        // 5. Validar que no haya conflictos de reserva
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                request.getTableId(),
                request.getReservationDate(),
                request.getReservationTime()
        );

        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Ya existe una reserva para esta mesa en este horario");
        }

        // 6. Crear la reserva
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setGuests(request.getGuests());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);
        return convertToDTO(savedReservation);
    }

    /**
     * Actualiza el estado de una reserva
     */
    public ReservationDTO updateReservationStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con ID: " + id));

        reservation.setStatus(status);

        // Si se cancela o completa, liberar la mesa
        if (status == ReservationStatus.CANCELLED ||
                status == ReservationStatus.COMPLETED ||
                status == ReservationStatus.NO_SHOW) {

            RestaurantTable table = reservation.getTable();
            table.setStatus(TableStatus.AVAILABLE);
            tableRepository.save(table);
        }

        // Si se confirma o se sienta, marcar mesa como ocupada/reservada
        if (status == ReservationStatus.CONFIRMED) {
            RestaurantTable table = reservation.getTable();
            table.setStatus(TableStatus.RESERVED);
            tableRepository.save(table);
        }

        if (status == ReservationStatus.SEATED) {
            RestaurantTable table = reservation.getTable();
            table.setStatus(TableStatus.OCCUPIED);
            tableRepository.save(table);
        }

        Reservation updatedReservation = reservationRepository.save(reservation);
        return convertToDTO(updatedReservation);
    }

    /**
     * Cancela una reserva
     */
    public ReservationDTO cancelReservation(Long id) {
        return updateReservationStatus(id, ReservationStatus.CANCELLED);
    }

    /**
     * Elimina una reserva
     */
    public void deleteReservation(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new RuntimeException("Reserva no encontrada con ID: " + id);
        }
        reservationRepository.deleteById(id);
    }

    // ==================== MÉTODOS DE CONSULTA ====================

    /**
     * Obtiene reservas por usuario
     */
    public List<ReservationDTO> getReservationsByUser(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserId(userId);
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene reservas por fecha
     */
    public List<ReservationDTO> getReservationsByDate(LocalDate date) {
        List<Reservation> reservations = reservationRepository.findByReservationDate(date);
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene reservas entre fechas (para el calendario)
     */
    public List<ReservationDTO> getReservationsBetweenDates(LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reservationRepository
                .findReservationsBetweenDates(startDate, endDate);
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene reservas confirmadas para hoy
     */
    public List<ReservationDTO> getTodayConfirmedReservations() {
        LocalDate today = LocalDate.now();
        List<Reservation> reservations = reservationRepository
                .findByReservationDateAndStatus(today, ReservationStatus.CONFIRMED);
        return reservations.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // ==================== VALIDACIÓN DE DISPONIBILIDAD ====================

    /**
     * Verifica si una mesa está disponible en una fecha y hora específica
     */
    public boolean isTableAvailable(Long tableId, LocalDate date, LocalTime time) {
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                tableId, date, time
        );
        return conflicts.isEmpty();
    }

    // ==================== CONVERSIÓN ====================

    /**
     * Convierte Entity a DTO
     */
    private ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setUserId(reservation.getUser().getId());
        dto.setUserName(reservation.getUser().getFullName());
        dto.setUserPhone(reservation.getUser().getPhone());
        dto.setTableId(reservation.getTable().getId());
        dto.setTableNumber(reservation.getTable().getTableNumber());
        dto.setReservationDate(reservation.getReservationDate());
        dto.setReservationTime(reservation.getReservationTime());
        dto.setGuests(reservation.getGuests());
        dto.setStatus(reservation.getStatus());
        dto.setSpecialRequests(reservation.getSpecialRequests());
        return dto;
    }
}