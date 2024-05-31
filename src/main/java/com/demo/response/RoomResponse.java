package com.demo.response;

import com.demo.model.BookedRoom;
import com.demo.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private int roomnumber;
    private String roomname;
    private String roomtype;
    private BigDecimal price;
    private String description;
    private Boolean isBooked;
    private String image;
    private List<BookingResponse> bookings;
    public RoomResponse(Long id,String roomname, String roomtype, BigDecimal price,String description){
        this.id=id;
        this.roomtype=roomtype;
        this.roomtype=roomname;
        this.price= price;
        this.description=description;
    }

    public RoomResponse(Long id, String roomname, String roomtype, BigDecimal price, String description, boolean isBooked,
                        byte[] photoBytes , List<BookingResponse> bookings) {
        this.id = id;
        this.roomname=roomname;
        this.roomtype = roomtype;
        this.price = price;
        this.description=description;
        this.isBooked = isBooked;
        this.image = photoBytes != null ?
                Base64.encodeBase64String(photoBytes) : null;
        this.bookings = bookings;
    }
}
