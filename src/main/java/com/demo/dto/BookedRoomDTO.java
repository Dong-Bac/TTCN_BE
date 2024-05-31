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
    private LocalDate checkindate;
    private LocalDate checkoutdate;
    private String guestName;
    private String guestEmail;
    private int numberOfGuests;

}
