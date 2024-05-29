package com.demo.repository;

import com.demo.model.BookedRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<BookedRoom, Long> {

    List<BookedRoom> findByRoomId(Long roomId);

    @Query("SELECT br FROM BookedRoom br WHERE br.confimationcode = :confimationcode")
    Optional<BookedRoom> findByConfirmationCode(@Param("confimationcode") String confimationcode);

    @Query("SELECT br FROM BookedRoom br WHERE br.email = :email")
    List<BookedRoom> findBookingByGuestEmail(@Param("email") String email);
}
