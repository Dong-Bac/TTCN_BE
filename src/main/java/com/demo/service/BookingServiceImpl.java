package com.demo.service;

import com.demo.dto.BookedRoomDTO;
import com.demo.exception.InvalidBookingRequestException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.model.BookedRoom;
import com.demo.model.Room;
import com.demo.model.User;
import com.demo.repository.BookingRepository;
import com.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


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
    public void cancelBooking(Long BookingId) {
        bookingRepository.deleteById(BookingId);

    }

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId) {
        return bookingRepository.findByRoomId(roomId);
    }

    @Override
    public String saveBooking(Long roomId, BookedRoom bookingRequest) {
        if (bookingRequest.getCheckoutdate().isBefore(bookingRequest.getCheckindate())){
            throw new InvalidBookingRequestException("Check-in date must come before check-out date");
        }
        Room room = roomService.getRoomById(roomId).get();
        List<BookedRoom> existingBookings = room.getBookings();
        boolean roomIsAvailable = roomIsAvailable(bookingRequest,existingBookings);
        if (roomIsAvailable){
            room.addBooking(bookingRequest);
            bookingRepository.save(bookingRequest);
        }else{
            throw  new InvalidBookingRequestException("Sorry, This room is not available for the selected dates;");
        }
        return bookingRequest.getConfimationcode();
    }



    @Override
    public BookedRoom findByBookingConfirmationCode(String confirmCode) {

        return bookingRepository.findByConfirmationCode(confirmCode).
                orElseThrow(()-> new ResourceNotFoundException("Not booking found with confirmCode"));
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
                        bookingRequest.getCheckindate().equals(existingBooking.getCheckindate())
                                || bookingRequest.getCheckoutdate().isBefore(existingBooking.getCheckoutdate())
                                || (bookingRequest.getCheckindate().isAfter(existingBooking.getCheckindate())
                                && bookingRequest.getCheckoutdate().isBefore(existingBooking.getCheckoutdate()))
                                || (bookingRequest.getCheckindate().isBefore(existingBooking.getCheckindate())

                                && bookingRequest.getCheckoutdate().equals(existingBooking.getCheckoutdate()))
                                || (bookingRequest.getCheckindate().isBefore(existingBooking.getCheckindate())

                                && bookingRequest.getCheckoutdate().isAfter(existingBooking.getCheckoutdate()))

                                || (bookingRequest.getCheckindate().equals(existingBooking.getCheckoutdate())
                                && bookingRequest.getCheckoutdate().equals(existingBooking.getCheckindate()))

                                || (bookingRequest.getCheckindate().equals(existingBooking.getCheckoutdate())
                                && bookingRequest.getCheckoutdate().equals(bookingRequest.getCheckindate()))
                );
    }


}
