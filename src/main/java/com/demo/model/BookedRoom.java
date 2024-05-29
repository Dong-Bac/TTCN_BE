package com.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookedRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @Column(name = "check_in")
    private LocalDate checkindate;

    @Column(name = "check_out")
    private LocalDate checkoutdate;

    @Column(name="username")
    private String username;

    @Column(name="email")
    private String email;

    @Column(name="total_guest")
    private int totalguest;

    @Column(name = "confirmation_Code")
    private String confimationcode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    public void setConfimationCode(String confimationcode){
        this.confimationcode=confimationcode;
    }



}
