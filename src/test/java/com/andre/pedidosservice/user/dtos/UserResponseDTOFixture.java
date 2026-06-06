package com.andre.pedidosservice.user.dtos;

import com.andre.pedidosservice.user.core.UserStatus;

public class UserResponseDTOFixture {

    public UserResponseDTO build(String id,
                                 String name,
                                 String email,
                                 UserStatus status){
        return new UserResponseDTO(id, name, email, status);
    }
}
