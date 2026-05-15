package com.andre.pedidosservice.users.dtos;

import com.andre.pedidosservice.users.entities.StatusEnum;

public record UserResponseDTO (
        String id,
        String name,
        String email,
        StatusEnum statusEnum
){

}
