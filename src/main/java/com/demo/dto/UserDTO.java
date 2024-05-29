package com.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String phoneNumber;
    private String email;
    private String address;
    private String age;
    private String avatar;
    private List<BookedRoomDTO> bookings;
    private Collection<RoleDTO> roles;
}

