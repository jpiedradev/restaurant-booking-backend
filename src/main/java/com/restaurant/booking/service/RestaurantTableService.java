package com.restaurant.booking.service;

import com.restaurant.booking.dto.TableDTO;
import com.restaurant.booking.enums.TableStatus;
import com.restaurant.booking.model.RestaurantTable;
import com.restaurant.booking.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantTableService {

    private final RestaurantTableRepository tableRepository;

    // ==================== MÉTODOS CRUD ====================

    /**
     * Obtiene todas las mesas
     * @return Lista de TableDTO
     */
    public List<TableDTO> getAllTables() {
        List<RestaurantTable> tables = tableRepository.findAll();
        return tables.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una mesa por ID
     * @param id ID de la mesa
     * @return TableDTO
     * @throws RuntimeException si no existe
     */
    public TableDTO getTableById(Long id) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));
        return convertToDTO(table);
    }

    /**
     * Crea una nueva mesa
     * @param tableDTO Datos de la mesa
     * @return TableDTO de la mesa creada
     */
    public TableDTO createTable(TableDTO tableDTO) {
        // Validar que no exista una mesa con ese número
        if (tableRepository.existsByTableNumber(tableDTO.getTableNumber())) {
            throw new RuntimeException("Ya existe una mesa con el número: " + tableDTO.getTableNumber());
        }

        RestaurantTable table = convertToEntity(tableDTO);
        RestaurantTable savedTable = tableRepository.save(table);
        return convertToDTO(savedTable);
    }

    /**
     * Actualiza una mesa existente
     * @param id ID de la mesa
     * @param tableDTO Nuevos datos
     * @return TableDTO actualizado
     */
    public TableDTO updateTable(Long id, TableDTO tableDTO) {
        RestaurantTable existingTable = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));

        // Validar cambio de número de mesa
        if (!existingTable.getTableNumber().equals(tableDTO.getTableNumber())) {
            if (tableRepository.existsByTableNumber(tableDTO.getTableNumber())) {
                throw new RuntimeException("Ya existe una mesa con el número: " + tableDTO.getTableNumber());
            }
        }

        // Actualizar campos
        existingTable.setTableNumber(tableDTO.getTableNumber());
        existingTable.setCapacity(tableDTO.getCapacity());
        existingTable.setLocation(tableDTO.getLocation());
        existingTable.setStatus(tableDTO.getStatus());
        existingTable.setDescription(tableDTO.getDescription());

        RestaurantTable updatedTable = tableRepository.save(existingTable);
        return convertToDTO(updatedTable);
    }

    /**
     * Elimina una mesa
     * @param id ID de la mesa
     */
    public void deleteTable(Long id) {
        if (!tableRepository.existsById(id)) {
            throw new RuntimeException("Mesa no encontrada con ID: " + id);
        }
        tableRepository.deleteById(id);
    }

    // ==================== MÉTODOS DE NEGOCIO ====================

    /**
     * Obtiene todas las mesas disponibles
     * @return Lista de mesas disponibles
     */
    public List<TableDTO> getAvailableTables() {
        List<RestaurantTable> tables = tableRepository.findAvailableTables();
        return tables.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene mesas disponibles con capacidad suficiente
     * @param guests Número de comensales
     * @return Lista de mesas adecuadas
     */
    public List<TableDTO> getAvailableTablesByCapacity(Integer guests) {
        List<RestaurantTable> tables = tableRepository
                .findByCapacityGreaterThanEqualAndStatus(guests, TableStatus.AVAILABLE);
        return tables.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cambia el estado de una mesa
     * @param id ID de la mesa
     * @param status Nuevo estado
     * @return TableDTO actualizado
     */
    public TableDTO updateTableStatus(Long id, TableStatus status) {
        RestaurantTable table = tableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con ID: " + id));

        table.setStatus(status);
        RestaurantTable updatedTable = tableRepository.save(table);
        return convertToDTO(updatedTable);
    }

    // ==================== CONVERSIONES ====================

    /**
     * Convierte Entity a DTO
     * @param table Entity
     * @return DTO
     */
    private TableDTO convertToDTO(RestaurantTable table) {
        TableDTO dto = new TableDTO();
        dto.setId(table.getId());
        dto.setTableNumber(table.getTableNumber());
        dto.setCapacity(table.getCapacity());
        dto.setLocation(table.getLocation());
        dto.setStatus(table.getStatus());
        dto.setDescription(table.getDescription());
        return dto;
    }

    /**
     * Convierte DTO a Entity
     * @param dto DTO
     * @return Entity
     */
    private RestaurantTable convertToEntity(TableDTO dto) {
        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(dto.getTableNumber());
        table.setCapacity(dto.getCapacity());
        table.setLocation(dto.getLocation());
        table.setStatus(dto.getStatus() != null ? dto.getStatus() : TableStatus.AVAILABLE);
        table.setDescription(dto.getDescription());
        return table;
    }
}