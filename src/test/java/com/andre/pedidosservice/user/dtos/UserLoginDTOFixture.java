package com.andre.pedidosservice.user.dtos;

public class UserLoginDTOFixture {

    public UserLoginDTO build(String email,
                              String password){
        return new UserLoginDTO(email, password);
    }
}
