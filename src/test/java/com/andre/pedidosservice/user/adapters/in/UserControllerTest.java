package com.andre.pedidosservice.user.adapters.in;

import com.andre.pedidosservice.user.core.UserStatus;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.dtos.*;
import com.andre.pedidosservice.user.gateways.in.UserAuthenticationService;
import com.andre.pedidosservice.user.gateways.in.UserServiceGateway;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserServiceGateway serviceGateway;
    @Mock
    UserAuthenticationService authentication;
    @Mock
    UserMapper mapper;

    private MockMvc mockMvc;
    private String url;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private UserRequestDTO userRequestDTO;
    private UserLoginDTO userLoginDTO;
    private UserResponseDTO userResponseDTO;
    private UserDomain userDomain;

    private String json;
    private String loginJson;
    private final String id = "123";

    @BeforeEach
    void setup() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).alwaysDo(print()).build();
        url = "/user";

        userRequestDTO = UserRequestDTOFixture.build("André", "andre@gmail.com", "senha123");
        userLoginDTO = UserLoginDTOFixture.build("andre@gmail.com", "senha123");
        userResponseDTO = UserResponseDTOFixture.build("123", "André", "andre@gmail.com", UserStatus.CLIENT);
        userDomain = new UserDomain("André", "andre@gmail.com", "senha123", "123", UserStatus.CLIENT);

        json = objectMapper.writeValueAsString(userRequestDTO);
        loginJson = objectMapper.writeValueAsString(userLoginDTO);
    }

    @Test
    void should_CreateUser_when_DataIsValid() throws Exception {
        when(serviceGateway.createUser("André", "andre@gmail.com", "senha123")).thenReturn(userDomain);
        when(mapper.domainToResponse(userDomain)).thenReturn(userResponseDTO);
        // Faz o teste em um http
        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk());

        verify(serviceGateway).createUser("André", "andre@gmail.com", "senha123");
        verify(mapper).domainToResponse(userDomain);
        verifyNoMoreInteractions(serviceGateway);
    }

    @Test
    void should_ReturnBadRequest_when_CreateUserJsonIsNull() throws Exception {

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(serviceGateway);
    }

    @Test
    void should_ReturnToken_when_LoginIsValid() throws Exception {
        when(mapper.loginToDomain(userLoginDTO)).thenReturn(userDomain);
        when(authentication.login(userDomain)).thenReturn("Bearer token-valido");

        mockMvc.perform(post(url + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(loginJson)
        ).andExpect(status().isOk());

        verify(mapper).loginToDomain(userLoginDTO);
        verify(authentication).login(userDomain);
        verifyNoMoreInteractions(authentication);
    }

    @Test
    void should_ReturnBadRequest_when_LoginJsonIsNull() throws Exception {
        mockMvc.perform(post(url + "/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());

        verifyNoInteractions(authentication);
    }

    @Test
    void should_UpdateUserStatus_when_DataIsValid() throws Exception {
        when(serviceGateway.updateUserStatus(id, UserStatus.ADMIN)).thenReturn(userDomain);
        when(mapper.domainToResponse(userDomain)).thenReturn(userResponseDTO);

        mockMvc.perform(patch(url + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .param("status", "ADMIN")
        ).andExpect(status().isOk());

        verify(serviceGateway).updateUserStatus(id, UserStatus.ADMIN);
        verify(mapper).domainToResponse(userDomain);
        verifyNoMoreInteractions(serviceGateway);
    }

    @Test
    void should_ReturnUser_when_IdExists() throws Exception {
        when(serviceGateway.getUserById(id)).thenReturn(userDomain);
        when(mapper.domainToResponse(userDomain)).thenReturn(userResponseDTO);

        mockMvc.perform(get(url + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(serviceGateway).getUserById(id);
        verify(mapper).domainToResponse(userDomain);
        verifyNoMoreInteractions(serviceGateway);
    }

    @Test
    void should_DeleteUser_when_IdExists() throws Exception {
        mockMvc.perform(delete(url + "/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        verify(serviceGateway).deleteUser(id);
        verifyNoMoreInteractions(serviceGateway);
    }
}
