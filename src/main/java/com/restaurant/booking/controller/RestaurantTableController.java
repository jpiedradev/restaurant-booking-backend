package com.restaurant.booking.controller;

import com.restaurant.booking.dto.TableDTO;
import com.restaurant.booking.enums.TableStatus;
import com.restaurant.booking.service.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class RestaurantTableController {

    private final RestaurantTableService tableService;

    /**
     * GET /api/tables
     * ADMIN y STAFF pueden ver todas las mesas
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public List<TableDTO> getAllTables() {
        return tableService.getAllTables();
    }

    /**
     * GET /api/tables/{id}
     * ADMIN y STAFF
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<TableDTO> getTableById(@PathVariable Long id) {
        TableDTO table = tableService.getTableById(id);
        return ResponseEntity.ok(table);
    }

    /**
     * GET /api/tables/available
     * ADMIN, STAFF y CUSTOMER (para hacer reservas)
     */
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public List<TableDTO> getAvailableTables() {
        return tableService.getAvailableTables();
    }

    /**
     * GET /api/tables/available/{guests}
     * ADMIN, STAFF y CUSTOMER (para hacer reservas)
     */
    @GetMapping("/available/{guests}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public List<TableDTO> getAvailableTablesByCapacity(@PathVariable Integer guests) {
        return tableService.getAvailableTablesByCapacity(guests);
    }

    /**
     * POST /api/tables
     * Solo ADMIN puede crear mesas
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TableDTO> createTable(@Valid @RequestBody TableDTO tableDTO) {
        TableDTO created = tableService.createTable(tableDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/tables/{id}
     * Solo ADMIN puede editar mesas
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TableDTO> updateTable(
            @PathVariable Long id,
            @Valid @RequestBody TableDTO tableDTO
    ) {
        TableDTO updated = tableService.updateTable(id, tableDTO);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /api/tables/{id}/status
     * ADMIN y STAFF pueden cambiar estado de mesas
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<TableDTO> updateTableStatus(
            @PathVariable Long id,
            @RequestParam TableStatus status
    ) {
        TableDTO updated = tableService.updateTableStatus(id, status);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/tables/{id}
     * Solo ADMIN puede eliminar mesas
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}