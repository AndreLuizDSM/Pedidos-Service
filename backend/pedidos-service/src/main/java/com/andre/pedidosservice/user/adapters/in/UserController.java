package com.andre.pedidosservice.user.adapters.in;

import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.dtos.UserLoginDTO;
import com.andre.pedidosservice.user.dtos.UserMapper;
import com.andre.pedidosservice.user.dtos.UserRequestDTO;
import com.andre.pedidosservice.user.dtos.UserResponseDTO;
import com.andre.pedidosservice.user.core.UserStatus;
import com.andre.pedidosservice.user.gateways.in.UserAuthenticationService;
import com.andre.pedidosservice.user.gateways.in.UserServiceGateway;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User registration and login")
public class UserController {

    private final UserServiceGateway serviceGateway;
    private final UserAuthenticationService authentication;
    private final UserMapper mapper;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(authentication.login(mapper.loginToDomain(dto)));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserDomain domain = serviceGateway.createUser(dto.name(), dto.email(), dto.password());
        return ResponseEntity.ok(mapper.domainToResponse(domain));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUserStatus(@PathVariable("id") String id,
                                                            @RequestParam("status") UserStatus status) {
        return ResponseEntity.ok(mapper.domainToResponse(serviceGateway.updateUserStatus(id, status)));
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
