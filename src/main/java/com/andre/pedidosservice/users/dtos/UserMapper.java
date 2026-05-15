package com.andre.pedidosservice.users.dtos;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity domainToEntity (UserDomain dto);
    UserResponseDTO domainToResponse (UserDomain domain);
    UserDomain requestToDomain (UserRequestDTO dto);
    UserDomain loginToDomain (UserLoginDTO dto);
    UserDomain entityToDomain (UserEntity entity);
}
