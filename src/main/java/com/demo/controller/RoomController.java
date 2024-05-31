package com.demo.controller;

import com.demo.exception.PhotoRetrievalException;
import com.demo.exception.ResourceNotFoundException;
import com.demo.model.BookedRoom;
import com.demo.model.Room;
import com.demo.response.BookingResponse;
import com.demo.response.RoomResponse;
import com.demo.service.BookingService;
import com.demo.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5500/")
public class RoomController {

    @Autowired
    private RoomService roomService;
    @Autowired
    private BookingService bookingService;

    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(
            @RequestParam("photo") MultipartFile photo,
            @RequestParam("roomName") String roomName,
            @RequestParam("roomType") String roomType,
            @RequestParam("roomPrice") BigDecimal roomPrice,
            @RequestParam("description") String description) throws SQLException, IOException {

        // Assuming that addNewRoom method takes description as a parameter
        Room savedRoom = roomService.addNewRoom(photo,roomName, roomType, roomPrice, description);
        RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomname(), savedRoom.getRoomtype(), savedRoom.getPrice(), savedRoom.getDescription());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/room-types")
    public ResponseEntity<List<String>> getRoomType(){
        List<String> room=roomService.getAllRoomTypes();
        return ResponseEntity.ok(room);
    }
    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException {
        List<Room> rooms = roomService.getAllRoom();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : rooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            String base64Photo = (photoBytes != null && photoBytes.length > 0) ? Base64.encodeBase64String(photoBytes) : null;
            RoomResponse roomResponse = getRoomResponse(room);
            roomResponse.setImage(base64Photo);
            roomResponses.add(roomResponse);
        }
        return ResponseEntity.ok(roomResponses);
    }
    @PutMapping("/update/{roomId}")
    public ResponseEntity<Room> updateRoom(@PathVariable("roomId") Long roomId,
                                           @RequestParam(required = false) MultipartFile photo,
                                           @RequestParam(required = false) String roomName,
                                           @RequestParam(required = false) String roomType,
                                           @RequestParam(required = false) BigDecimal roomPrice,
                                           @RequestParam(required = false) String description
                                           ) throws SQLException, IOException {
        byte[] photoBytes=photo!=null &&!photo.isEmpty()?
                photo.getBytes(): roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob=photoBytes!=null&& photoBytes.length>0 ? new SerialBlob(photoBytes):null;
        Room theRoom=roomService.updateRoom(roomId,roomName, roomType, roomPrice, description, photoBytes);
        theRoom.setImage(photoBlob);
        RoomResponse roomResponse= new RoomResponse();
        return ResponseEntity.ok(theRoom);
    }
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable Long roomId){
        Optional<Room> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room -> {
            RoomResponse roomResponse = getRoomResponse(room);
            return  ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }

    @DeleteMapping("delete/room/{roomId}")
    public void deleteRoomById(@PathVariable("roomId") Long roomId){
        roomService.deleteRoom(roomId);
    }

//    @GetMapping("/available-rooms")
//    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
//            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkInDate,
//            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate checkOutDate,
//            @RequestParam("roomType") String roomType) throws SQLException {
//        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
//        List<RoomResponse> roomResponses = new ArrayList<>();
//        for (Room room : availableRooms){
//            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
//            if (photoBytes != null && photoBytes.length > 0){
//                String photoBase64 = Base64.encodeBase64String(photoBytes);
//                RoomResponse roomResponse = getRoomResponse(room);
//                roomResponse.setImage(photoBase64);
//                roomResponses.add(roomResponse);
//            }
//        }
//        if(roomResponses.isEmpty()){
//            return ResponseEntity.noContent().build();
//        }else{
//            return ResponseEntity.ok(roomResponses);
//        }
//    }
//
//    public RoomResponse getRoomResponse(Room room){
//        List<BookedRoom> bookings=bookingService.getAllBookingsByRoomId(room.getId());
//        List<BookingResponse> bookingInfo=bookings.stream()
//                .map(booking-> new BookingResponse(booking.getBookingId(),
//                        booking.getCheckindate(),booking.getCheckoutdate(), booking.getConfimationcode())).toList();
//        byte[] photoBytes=null;
//        Blob photoBlob= room.getImage();
//        if(photoBlob!=null){
//            try{
//                photoBytes=photoBlob.getBytes(1,(int) photoBlob.length());
//            }catch (SQLException e){
//                throw new PhotoRetrievalException("Error retrieving photo");
//            }
//        }
//
//        return new RoomResponse(room.getId(), room.getRoomname(), room.getRoomtype(), room.getPrice(), room.getDescription(), room.getIsBooked(), photoBytes, bookingInfo);
//    }

    @GetMapping("/available-rooms")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @RequestParam("checkInDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam("checkOutDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam("roomType") String roomType) throws SQLException {
        List<Room> availableRooms = roomService.getAvailableRooms(checkInDate, checkOutDate, roomType);
        List<RoomResponse> roomResponses = new ArrayList<>();
        for (Room room : availableRooms) {
            byte[] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            RoomResponse roomResponse = getRoomResponse(room);
            if (photoBytes != null && photoBytes.length > 0) {
                String photoBase64 = Base64.encodeBase64String(photoBytes);
                roomResponse.setImage(photoBase64);
            }
            roomResponses.add(roomResponse);
        }
        if (roomResponses.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(roomResponses);
        }
    }

    public RoomResponse getRoomResponse(Room room) {
        List<BookedRoom> bookings = bookingService.getAllBookingsByRoomId(room.getId());
        List<BookingResponse> bookingInfo = bookings.stream()
                .map(booking -> new BookingResponse(booking.getBookingId(),
                        booking.getCheckindate(), booking.getCheckoutdate(), booking.getConfimationcode()))
                .toList();
        byte[] photoBytes = null;
        Blob photoBlob = room.getImage();
        if (photoBlob != null) {
            try {
                photoBytes = photoBlob.getBytes(1, (int) photoBlob.length());
            } catch (SQLException e) {
                throw new PhotoRetrievalException("Error retrieving photo");
            }
        }

        return new RoomResponse(room.getId(), room.getRoomname(), room.getRoomtype(), room.getPrice(),
                room.getDescription(), room.getIsBooked(), photoBytes, bookingInfo);
    }
}
