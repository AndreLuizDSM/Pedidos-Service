package com.andre.pedidosservice.user.adapters.out;

import com.andre.pedidosservice.exception.exceptions.ResourceNotFoundException;
import com.andre.pedidosservice.user.core.UserStatus;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.dtos.UserMapper;
import com.andre.pedidosservice.user.entities.UserEntity;
import com.andre.pedidosservice.user.ports.out.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @InjectMocks
    private UserRepositoryGatewayAdapter adapter;

    @Mock
    private UserJpaRepository jpaRepository;
    @Mock
    private UserMapper mapper;
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserDomain userDomain;
    private UserEntity userEntity;

    @BeforeEach
    void setup() {
        userDomain = new UserDomain("André", "andre@email.com", "senha123", "123", UserStatus.CLIENT);
        userEntity = UserEntity.builder()
                .id("123").name("André").email("andre@email.com")
                .password("senha123").status(UserStatus.CLIENT).build();
    }

                        // save

    @Test
    void should_EncodePasswordAndSave_when_SaveIsCalled() {
        when(mapper.domainToEntity(userDomain)).thenReturn(userEntity);
        when(passwordEncoder.encode("senha123")).thenReturn("senha-encriptada");
        when(mapper.entityToDomain(userEntity)).thenReturn(userDomain);

        UserDomain result = adapter.save(userDomain);

        assertNotNull(result);
        assertEquals(userDomain, result);
        // A senha da entity deve ter sido trocada pela versão encriptada antes de persistir
        assertEquals("senha-encriptada", userEntity.getPassword());

        verify(mapper).domainToEntity(userDomain);
        verify(passwordEncoder).encode("senha123");
        verify(jpaRepository).save(userEntity);
        verify(mapper).entityToDomain(userEntity);
        verifyNoMoreInteractions(jpaRepository);
    }

                        // findById

    @Test
    void should_ReturnUser_when_IdExists() {
        when(jpaRepository.findById("123")).thenReturn(Optional.of(userEntity));
        when(mapper.entityToDomain(userEntity)).thenReturn(userDomain);

        Optional<UserDomain> result = adapter.findById("123");

        assertTrue(result.isPresent());
        assertEquals(userDomain, result.get());

        verify(jpaRepository).findById("123");
        verify(mapper).entityToDomain(userEntity);
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    void should_ReturnEmpty_when_IdDoesNotExist() {
        when(jpaRepository.findById("123")).thenReturn(Optional.empty());

        Optional<UserDomain> result = adapter.findById("123");

        assertFalse(result.isPresent());

        verify(jpaRepository).findById("123");
        verifyNoMoreInteractions(jpaRepository);
    }

                        // existsByEmail

    @Test
    void should_ReturnTrue_when_EmailExists() {
        when(jpaRepository.existsByEmail("andre@email.com")).thenReturn(true);

        boolean result = adapter.existsByEmail("andre@email.com");

        assertTrue(result);
        verify(jpaRepository).existsByEmail("andre@email.com");
        verifyNoMoreInteractions(jpaRepository);
    }

                        // deleteById

    @Test
    void should_DeleteUser_when_IdExists() {
        when(jpaRepository.findById("123")).thenReturn(Optional.of(userEntity));
        when(mapper.entityToDomain(userEntity)).thenReturn(userDomain);

        adapter.deleteById("123");

        verify(jpaRepository).findById("123");
        verify(jpaRepository).deleteById("123");
    }

    @Test
    void should_ThrowResourceNotFoundException_when_DeleteIdDoesNotExist() {
        when(jpaRepository.findById("123")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> adapter.deleteById("123"));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Usuário não encontrado 123"));

        verify(jpaRepository).findById("123");
        // não deve tentar deletar quando o usuário não existe
        verify(jpaRepository, never()).deleteById("123");
    }

                        // updateStatus

    @Test
    void should_UpdateStatus_when_IdExists() {
        when(jpaRepository.findById("123")).thenReturn(Optional.of(userEntity));
        when(jpaRepository.save(userEntity)).thenReturn(userEntity);
        when(mapper.entityToDomain(userEntity)).thenReturn(userDomain);

        UserDomain result = adapter.updateStatus("123", UserStatus.ADMIN);

        assertNotNull(result);
        assertEquals(userDomain, result);
        // O status da entity deve ter sido atualizado antes de salvar
        assertEquals(UserStatus.ADMIN, userEntity.getStatus());

        verify(jpaRepository).findById("123");
        verify(jpaRepository).save(userEntity);
        verify(mapper).entityToDomain(userEntity);
        verifyNoMoreInteractions(jpaRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_when_UpdateStatusIdDoesNotExist() {
        when(jpaRepository.findById("123")).thenReturn(Optional.empty());

        ResourceNotFoundException e = assertThrows(ResourceNotFoundException.class,
                () -> adapter.updateStatus("123", UserStatus.ADMIN));

        assertThat(e, notNullValue());
        assertThat(e.getMessage(), is("Usuário não encontrado 123"));

        verify(jpaRepository).findById("123");
        verify(jpaRepository, never()).save(any(UserEntity.class));
    }
}
