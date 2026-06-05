package com.andre.pedidosservice.user.dtos;

import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.entities.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity domainToEntity (UserDomain dto);
    UserResponseDTO domainToResponse (UserDomain domain);
    UserDomain requestToDomain (UserRequestDTO dto);
    UserDomain loginToDomain (UserLoginDTO dto);
    UserDomain entityToDomain (UserEntity entity);

}
