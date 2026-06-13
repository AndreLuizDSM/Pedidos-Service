package com.andre.pedidosservice.user.mapper;

import com.andre.pedidosservice.user.core.UserStatus;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.dtos.*;
import com.andre.pedidosservice.user.entities.UserEntity;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class UserMapperTest {

    UserMapper mapper;

    UserEntity userEntity;
    UserDomain userDomain;
    UserResponseDTO userResponseFixture;
    UserRequestDTO userRequest;
    UserLoginDTO userLogin;




    @BeforeEach
    public void setup() {
        mapper = Mappers.getMapper(UserMapper.class);

        userEntity =
                UserEntity.builder().id("123").email("andre@email.com").name("André").password("senha123").status(UserStatus.CLIENT).build();
        userDomain = new UserDomain("André", "andre@email.com", "senha123", "123", UserStatus.CLIENT);
        userResponseFixture = UserResponseDTOFixture.build("123", "André", "andre@email.com", UserStatus.CLIENT);
        userRequest = UserRequestDTOFixture.build("André", "andre@email.com", "senha123");
        userLogin = UserLoginDTOFixture.build("andre@email.com", "senha123");
    }

    @Test
    void should_ReturnEntity_when_ConvertFromDomain(){
        UserEntity entity = mapper.domainToEntity(userDomain);

        assertEquals(userEntity, entity);
    }

    @Test
    void should_ReturnResponse_when_ConvertFromDomain(){
        UserResponseDTO dto = mapper.domainToResponse(userDomain);
        assertEquals(userResponseFixture, dto);
    }

    @Test
    void should_ReturnDomain_when_ConvertFromLogin(){

        UserDomain domain = mapper.loginToDomain(userLogin);
        // Não devo comparar objetos , mas valores , pois o objeto retornado de login é incompleto.
        assertEquals("andre@email.com", domain.getEmail());
        assertEquals("senha123", domain.getPassword());
        assertNull(domain.getName());   // login não deve passar nome, nem status e id
        assertNull(domain.getStatus());
        assertNull(domain.getId());

    }

    @Test
    void should_ReturnDomain_when_ConvertFromEntity(){
        UserDomain domain = mapper.entityToDomain(userEntity);
        assertEquals(userDomain, domain);
    }

}
