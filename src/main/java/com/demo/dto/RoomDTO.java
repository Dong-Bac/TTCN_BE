package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Long id;
    private int roomnumber;
    private String roomname;
    private String roomtype;
    private BigDecimal price;
    private String description;
    private String image; // Use a string or another appropriate type
}