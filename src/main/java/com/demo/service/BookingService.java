package com.demo.service;

import com.demo.dto.BookedRoomDTO;
import com.demo.model.BookedRoom;

import java.util.List;

public interface BookingService {

    void cancelBooking(Long BookingId);

    List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    String saveBooking(Long roomId, BookedRoom bookingRequest);

    BookedRoom findByBookingConfirmationCode(String confirmCode);

    List<BookedRoom> getAllBookings();

    List<BookedRoom> getBookingsByUserEmail(String email);

    List<BookedRoom> getBookingsByUserId(Long userId);
}
