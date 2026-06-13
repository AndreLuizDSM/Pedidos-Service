package com.andre.pedidosservice.user.dtos;

public class UserLoginDTOFixture {

    public static UserLoginDTO build(String email,
                              String password){
        return new UserLoginDTO(email, password);
    }
}
