package com.demo.repository;

import com.demo.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT DISTINCT r.roomtype FROM Room r")
    List<String> findDistinctRoomTypes();

    @Query("SELECT r FROM Room r WHERE r.roomtype LIKE %:roomtype% AND r.id NOT IN (" +
            "SELECT br.room.id FROM BookedRoom br " +
            "WHERE ((br.checkindate <= :checkoutdate) AND (br.checkoutdate >= :checkindate)))")
    List<Room> findAvailableRoomsByDatesAndType(
            @Param("checkindate") LocalDate checkindate,
            @Param("checkoutdate") LocalDate checkoutdate,
            @Param("roomtype") String roomtype
    );

    Room findByRoomname(String roomname);
}
