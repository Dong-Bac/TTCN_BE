package com.demo.service;

import com.demo.dto.BookedRoomDTO;
import com.demo.exception.InvalidBookingRequestException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.model.BookedRoom;
import com.demo.model.Room;
import com.demo.model.User;
import com.demo.repository.BookingRepository;
import com.demo.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService{

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoomService roomService;

    @Override
    public void cancelBooking(Long bookingId) {
        Room room = bookingRepository.findRoomByBookingId(bookingId);
        Optional<BookedRoom> bookedRoomOptional = bookingRepository.findById(bookingId);
        if (bookedRoomOptional.isPresent()) {
            BookedRoom bookedRoom = bookedRoomOptional.get();
            room.deleteBooking(bookedRoom);
            bookingRepository.deleteById(bookingId);
        } else {
            throw new EntityNotFoundException("Booking with id " + bookingId + " not found");
        }
    }

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public String saveBooking(Long roomId, LocalDate checkindate, LocalDate checkoutdate, String email, int totalguest) {
        BookedRoom bookingRequest = new BookedRoom(checkindate, checkoutdate, email, totalguest);
        if (bookingRequest.getCheckoutdate().isBefore(bookingRequest.getCheckindate())) {
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        Room room = roomService.getRoomById(roomId).orElseThrow(() ->
                new ResourceNotFoundException("Room not found with ID " + roomId)
        );
        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest, existingBookings);
        if (roomIsAvailable) {
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        } else {
            throw new InvalidBookingRequestException("Sorry, This room is not available for the selected dates;");
        }
        return bookingRequest.getConfimationcode();
    }


    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmCode) {

        return bookingRepository.findByConfirmationCode(confirmCode).
                orElseThrow(()-> new ResourceNotFoundException("Not booking found with confirmCode"));
    }

    @Override
    public BookedRoom findByBookingId(Long BookingId) {
        Optional<BookedRoom> bookedRoom=bookingRepository.findById(BookingId);
        return  bookedRoom.get();
    }

    @Override
    public List<BookedRoom> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public List<BookedRoom> getBookingsByUserEmail(String email) {
        return bookingRepository.findBookingByGuestEmail(email);
    }

    @Override
    public List<BookedRoom> getBookingsByUserId(Long userId) {

        Optional<User> userOptional=userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Retrieve bookings associated with the user
            List<BookedRoom> bookings=user.getBookings();

            // Return the list of bookings
            return bookings;
        } else {
            // Handle the case where the user does not exist
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
    }

    private boolean roomIsAvailable(BookedRoom bookingRequest, List<BookedRoom> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        (bookingRequest.getCheckindate().isBefore(existingBooking.getCheckoutdate()) &&
                                bookingRequest.getCheckoutdate().isAfter(existingBooking.getCheckindate()))
                );
    }

    public void cancelExpiredBookings(){
        LocalDate today=LocalDate.now();
        List<BookedRoom> expiredBookings=bookingRepository.findAll().stream()
                .filter(booking->booking.getCheckoutdate().isBefore(today))
                .collect(Collectors.toList());
        for (BookedRoom booking : expiredBookings) {
            cancelBooking(booking.getBookingId());
        }

    }

}
