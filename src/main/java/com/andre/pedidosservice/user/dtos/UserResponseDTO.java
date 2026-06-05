package com.andre.pedidosservice.user.dtos;

import com.andre.pedidosservice.user.core.UserStatus;

public record UserResponseDTO (
        String id,
        String name,
        String email,
        UserStatus status
){

}
