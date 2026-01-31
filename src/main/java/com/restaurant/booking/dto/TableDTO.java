package com.restaurant.booking.dto;

import com.restaurant.booking.enums.TableLocation;
import com.restaurant.booking.enums.TableStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    private Long id;
    private Integer tableNumber;
    private Integer capacity;
    private TableLocation location;
    private TableStatus status;
    private String description;
}