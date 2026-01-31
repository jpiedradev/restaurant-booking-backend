package com.restaurant.booking.repository;

import com.restaurant.booking.enums.TableStatus;
import com.restaurant.booking.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    List<RestaurantTable> findByStatus(TableStatus status);

    List<RestaurantTable> findByCapacityGreaterThanEqualAndStatus(Integer capacity, TableStatus status);

    boolean existsByTableNumber(Integer tableNumber);

    @Query("SELECT t FROM RestaurantTable t WHERE t.status = 'AVAILABLE' ORDER BY t.capacity ASC")
    List<RestaurantTable> findAvailableTables();
}