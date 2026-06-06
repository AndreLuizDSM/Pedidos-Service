package com.andre.pedidosservice.user.dtos;

public class UserRequestDTOFixture {

    public UserRequestDTO build(String name,
                                String email,
                                String password){
        return new UserRequestDTO(name, email, password);
    }
}
