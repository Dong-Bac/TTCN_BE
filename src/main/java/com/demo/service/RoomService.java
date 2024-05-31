package com.demo.service;

import com.demo.model.Room;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {

    Room addNewRoom(MultipartFile file,String roomName, String roomType, BigDecimal roomPrice, String description) throws SQLException, IOException;
    List<Room> getAllRoom();
    Optional<Room> getRoomById(Long roomId);
    List<String> getAllRoomTypes();
    byte[] getRoomPhotoByRoomId(Long roomId) throws SQLException;
    List<Room> getAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
    Room updateRoom(Long roomId, String roomName, String roomType, BigDecimal roomPrice, String description, byte[] photoBytes);
    void deleteRoom(Long roomId);
}
