package com.andre.pedidosservice.users.dtos;

import com.andre.pedidosservice.users.core.UserStatus;

public record UserResponseDTO (
        String id,
        String name,
        String email,
        UserStatus status
){

}
