package com.demo.service;

import com.demo.exception.InternalServerException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.exception.RoomNameExistsException;
import com.demo.model.Room;
import com.demo.repository.RoomRepository;
import com.zaxxer.hikari.util.FastList;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public Room addNewRoom(MultipartFile file, String roomName, String roomType, BigDecimal roomPrice, String description) throws SQLException, IOException {
        // Kiểm tra xem roomname đã tồn tại trong cơ sở dữ liệu chưa
        Room existingRoom = roomRepository.findByRoomname(roomName);
        if (existingRoom != null) {
            // Nếu roomname đã tồn tại, bạn có thể thực hiện xử lý phù hợp ở đây, ví dụ: throw một exception hoặc trả về null
            throw new RoomNameExistsException("Roomname already exists: " + roomName);
        }

        Room room = new Room();
        room.setRoomtype(roomType);
        room.setPrice(roomPrice);
        room.setDescription(description); // Set description
        room.setRoomname(roomName);

        if (!file.isEmpty()) {
            byte[] photoBytes = file.getBytes(); // Chuyển nội dung của "MultipartFile" thành một mảng byte
            Blob photoBlob = new SerialBlob(photoBytes); // Lưu trữ dữ liệu nhị phân trong cơ sở dữ liệu
            room.setImage(photoBlob);
        }
        return roomRepository.save(room);
    }


    @Override
    public List<Room> getAllRoom() {
        return roomRepository.findAll();
    }


    @Override
    public Optional<Room> getRoomById(Long roomId) {
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<String> getAllRoomTypes() {
        return roomRepository.findDistinctRoomTypes();
    }

    public List<Room> getAvailableRooms(LocalDate checkindate, LocalDate checkoutdate, String roomtype) {
        return roomRepository.findAvailableRoomsByDatesAndType(checkindate, checkoutdate, roomtype);
    }

    public byte[] getRoomPhotoByRoomId(Long roomId) {
        Optional<Room> optionalRoom = roomRepository.findById(roomId);
        if (optionalRoom.isPresent() && optionalRoom.get().getImage() != null) {
            try {
                return optionalRoom.get().getImage().getBytes(1, (int) optionalRoom.get().getImage().length());
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving room image", e);
            }
        }
        return null;
    }

    @Override
    public Room updateRoom(Long roomId, String roomName, String roomType, BigDecimal roomPrice, String description, byte[] photoBytes) {
        Room room=roomRepository.findById(roomId).get();

        if(roomName!=null) room.setRoomname(roomName);
        if(roomType!=null) room.setRoomtype(roomType);
        if(roomPrice!=null) room.setPrice(roomPrice);
        if(description!=null) room.setDescription(description);
        if (photoBytes!=null && photoBytes.length>0){
            try{
                room.setImage(new SerialBlob(photoBytes));
            } catch (SerialException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new InternalServerException("Fail updating room");
            }
        }
        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long roomId) {
        Optional<Room> room=roomRepository.findById(roomId);
        if(room.isPresent()){
            roomRepository.delete(room.get());
        }
    }
}
