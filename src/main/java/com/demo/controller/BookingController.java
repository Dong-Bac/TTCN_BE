package com.demo.controller;


import com.demo.dto.BookedRoomDTO;
import com.demo.exception.InvalidBookingRequestException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.model.BookedRoom;
import com.demo.model.Room;
import com.demo.response.BookingResponse;
import com.demo.response.RoomResponse;
import com.demo.service.BookingService;
import com.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookingResponses = bookingService.getAllBookings()
                .stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingResponses);
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable Long roomId,
                                         @RequestBody BookedRoom bookingRequest){
        try{
            String confirmationCode = bookingService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok(
                    "Room booked successfully");

        }catch (InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable String confirmationCode) {
        try {
            BookedRoom booking = bookingService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = convertToBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @GetMapping("/user/{email}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserEmail(@PathVariable String email) {
        List<BookingResponse> bookingResponses = bookingService.getBookingsByUserEmail(email)
                .stream()
                .map(this::convertToBookingResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(bookingResponses);
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok("Booking with ID " + bookingId + " has been canceled successfully.");
    }

    private BookingResponse convertToBookingResponse(BookedRoom booking) {
        RoomResponse room = new RoomResponse(
                booking.getRoom().getId(),
                booking.getRoom().getRoomname(),
                booking.getRoom().getRoomtype(),
                booking.getRoom().getPrice(),
                booking.getRoom().getDescription()
        );
        return new BookingResponse(
                booking.getBookingId(),
                booking.getCheckindate(),
                booking.getCheckoutdate(),
                booking.getUsername(),
                booking.getEmail(),
                booking.getTotalguest(),
                booking.getConfimationcode(),
                room
        );
    }


}
