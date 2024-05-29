package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookedRoomDTO {
    private Long bookingId;
    private LocalDate checkindate;
    private LocalDate checkoutdate;
    private String username;
    private String email;
    private int totalguest;
    private String confimationcode;
    private RoomDTO room;
}
