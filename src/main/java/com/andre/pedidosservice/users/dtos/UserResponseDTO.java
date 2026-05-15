package com.andre.pedidosservice.users.dtos;

import com.andre.pedidosservice.users.core.StatusEnum;

public record UserResponseDTO (
        String id,
        String name,
        String email,
        StatusEnum status
){

}
