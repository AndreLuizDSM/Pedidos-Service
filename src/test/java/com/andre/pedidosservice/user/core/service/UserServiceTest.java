package com.andre.pedidosservice.user.core.service;

import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.user.core.UserStatus;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.gateways.out.UserRepositoryGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepositoryGateway repository;

    private UserDomain userDomain;

    @BeforeEach
    void setup() {
        userDomain = new UserDomain("André", "andre@email.com", "senha123", "123", UserStatus.CLIENT);
    }

                        // createUser

    @Test
    void createUser_success() {
        when(repository.existsByEmail("andre@email.com")).thenReturn(false);
        // Na service , eu gero outro objeto com newUser, porém eu não uso esses métodos nos tests,
        // então o uso do "any" resolve o problema de referência de objeto diferente.
        when(repository.save(any(UserDomain.class))).thenReturn(userDomain);

        UserDomain result = userService.createUser("André", "andre@email.com", "senha123");

        assertNotNull(result);
        // Comparar o conteúdo , e não o objeto , pois na SERVICE, é passado objetos diferentes
        assertEquals("andre@email.com", result.getEmail());

        verify(repository).existsByEmail("andre@email.com");
        verify(repository).save(any(UserDomain.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void createUser_emailDuplicado_lancaInvalidRequestException() {
        when(repository.existsByEmail("andre@email.com")).thenReturn(true);

        InvalidRequestException e = assertThrows(InvalidRequestException.class,
                () -> userService.createUser("André", "andre@email.com", "senha123"));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Email já existe"));

        verify(repository).existsByEmail("andre@email.com");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void createUser_emailInvalido_lancaInvalidRequestException() {
        // UserDomain.newUser valida o formato do email ANTES de tocar no repositório
        InvalidRequestException e = assertThrows(InvalidRequestException.class,
                () -> userService.createUser("André", "email-invalido", "senha123"));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Email invalido"));

        verifyNoInteractions(repository);
    }

                        // getUserById

    @Test
    void getUserById_success() {
        when(repository.findById("123")).thenReturn(Optional.of(userDomain));

        UserDomain result = userService.getUserById("123");

        assertNotNull(result);
        // Verifica o ID , pois o objeto que será retornado do findById, pode ter dados diferentes.
        // Então é melhor testar se os dados são equivalentes.
        assertEquals("123", result.getId());

        verify(repository).findById("123");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void getUserById_naoEncontrado_lancaResourceNotFoundException() {
        when(repository.findById("123")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById("123"));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Usuário não encontrado123"));

        verify(repository).findById("123");
        verifyNoMoreInteractions(repository);
    }

                        // deleteUser

    @Test
    void deleteUser_success() {
        when(repository.findById("123")).thenReturn(Optional.of(userDomain));

        userService.deleteUser("123");

        verify(repository).findById("123");
        verify(repository).deleteById("123");
        verifyNoMoreInteractions(repository);
    }

    @Test
    void deleteUser_naoEncontrado_lancaResourceNotFoundException() {
        when(repository.findById("123")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("123"));

        verify(repository).findById("123");
        verifyNoMoreInteractions(repository);
    }

                        // updateUserStatus

    @Test
    void updateUserStatus_success() {
        when(repository.updateStatus(anyString(), any(UserStatus.class))).thenReturn(userDomain);

        UserDomain result = userService.updateUserStatus("123", UserStatus.ADMIN);

        assertNotNull(result);
        verify(repository).updateStatus("123", UserStatus.ADMIN);
        verifyNoMoreInteractions(repository);
    }
}
