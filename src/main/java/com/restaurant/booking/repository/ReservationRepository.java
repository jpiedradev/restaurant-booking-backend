package com.restaurant.booking.repository;

import com.restaurant.booking.enums.ReservationStatus;
import com.restaurant.booking.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    List<Reservation> findByReservationDate(LocalDate date);

    List<Reservation> findByReservationDateAndStatus(LocalDate date, ReservationStatus status);

    @Query("SELECT r FROM Reservation r WHERE r.table.id = :tableId " +
            "AND r.reservationDate = :date " +
            "AND r.reservationTime = :time " +
            "AND r.status IN ('PENDING', 'CONFIRMED', 'SEATED')")
    List<Reservation> findConflictingReservations(
            @Param("tableId") Long tableId,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );

    @Query("SELECT r FROM Reservation r WHERE r.reservationDate >= :startDate " +
            "AND r.reservationDate <= :endDate ORDER BY r.reservationDate, r.reservationTime")
    List<Reservation> findReservationsBetweenDates(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}