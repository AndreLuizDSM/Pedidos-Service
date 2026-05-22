package com.andre.pedidosservice.users.adapters.in;

import com.andre.pedidosservice.users.dtos.UserLoginDTO;
import com.andre.pedidosservice.users.dtos.UserMapper;
import com.andre.pedidosservice.users.dtos.UserRequestDTO;
import com.andre.pedidosservice.users.dtos.UserResponseDTO;
import com.andre.pedidosservice.users.core.UserStatus;
import com.andre.pedidosservice.users.gateways.in.IUserAuthenticationIn;
import com.andre.pedidosservice.users.gateways.in.IUserServiceGateway;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final IUserServiceGateway serviceGateway;
    private final IUserAuthenticationIn authentication;
    private final UserMapper mapper;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(authentication.login(mapper.loginToDomain(dto)));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> saveUser(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(mapper.domainToResponse(serviceGateway.saveUser(mapper.requestToDomain(dto))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> patchUserStatus(@PathVariable("id") String id,
                                                           @RequestParam("status") UserStatus status) {
        return ResponseEntity.ok(mapper.domainToResponse(serviceGateway.patchStatus(id, status)));
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") String id) {
        return ResponseEntity.ok(mapper.domainToResponse(serviceGateway.getUserById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") String id) {
        serviceGateway.deleteUser(id);
        return ResponseEntity.ok().build();

    }
}
