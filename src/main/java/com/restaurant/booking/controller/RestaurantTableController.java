package com.restaurant.booking.controller;

import com.restaurant.booking.dto.TableDTO;
import com.restaurant.booking.enums.TableStatus;
import com.restaurant.booking.service.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Obtiene todas las mesas
     */
    @GetMapping
    public ResponseEntity<List<TableDTO>> getAllTables() {
        List<TableDTO> tables = tableService.getAllTables();
        return ResponseEntity.ok(tables);
    }

    /**
     * GET /api/tables/{id}
     * Obtiene una mesa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TableDTO> getTableById(@PathVariable Long id) {
        try {
            TableDTO table = tableService.getTableById(id);
            return ResponseEntity.ok(table);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/tables/available
     * Obtiene todas las mesas disponibles
     */
    @GetMapping("/available")
    public ResponseEntity<List<TableDTO>> getAvailableTables() {
        List<TableDTO> tables = tableService.getAvailableTables();
        return ResponseEntity.ok(tables);
    }

    /**
     * GET /api/tables/available/{guests}
     * Obtiene mesas disponibles con capacidad para X personas
     */
    @GetMapping("/available/{guests}")
    public ResponseEntity<List<TableDTO>> getAvailableTablesByCapacity(@PathVariable Integer guests) {
        List<TableDTO> tables = tableService.getAvailableTablesByCapacity(guests);
        return ResponseEntity.ok(tables);
    }

    /**
     * POST /api/tables
     * Crea una nueva mesa
     */
    @PostMapping
    public ResponseEntity<TableDTO> createTable(@Valid @RequestBody TableDTO tableDTO) {
        try {
            TableDTO createdTable = tableService.createTable(tableDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTable);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/tables/{id}
     * Actualiza una mesa existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<TableDTO> updateTable(
            @PathVariable Long id,
            @Valid @RequestBody TableDTO tableDTO) {
        try {
            TableDTO updatedTable = tableService.updateTable(id, tableDTO);
            return ResponseEntity.ok(updatedTable);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * PATCH /api/tables/{id}/status
     * Actualiza solo el estado de una mesa
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TableDTO> updateTableStatus(
            @PathVariable Long id,
            @RequestParam TableStatus status) {
        try {
            TableDTO updatedTable = tableService.updateTableStatus(id, status);
            return ResponseEntity.ok(updatedTable);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/tables/{id}
     * Elimina una mesa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        try {
            tableService.deleteTable(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}