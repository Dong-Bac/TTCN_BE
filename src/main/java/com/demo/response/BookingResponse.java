package com.demo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {

    private Long id;
    private LocalDate checkindate;
    private LocalDate checkoutdate;
    private String user;
    private String email;
    private int totalguest;
    private String confimationcode;
    private RoomResponse room;

    public BookingResponse(Long id, LocalDate checkindate, LocalDate checkoutdate,
                           String confimationcode) {
        this.id = id;
        this.checkindate = checkindate;
        this.checkoutdate= checkoutdate;
        this.confimationcode = confimationcode;
    }



}
