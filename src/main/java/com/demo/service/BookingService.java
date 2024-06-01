package com.demo.service;

import com.demo.dto.BookedRoomDTO;
import com.demo.model.BookedRoom;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    void cancelBooking(Long BookingId);

    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    String saveBooking(Long roomId, LocalDate checkindate, LocalDate checkoutdate, String email, int totalguest);

    BookedRoom findByBookingConfirmationCode(String confirmCode);

    BookedRoom findByBookingId(Long id);

    List<BookedRoom> getAllBookings();

    List<BookedRoom> getBookingsByUserEmail(String email);

    List<BookedRoom> getBookingsByUserId(Long userId);

}
