package com.andre.pedidosservice.users.adapters.in;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.dtos.UserLoginDTO;
import com.andre.pedidosservice.users.dtos.UserMapper;
import com.andre.pedidosservice.users.dtos.UserRequestDTO;
import com.andre.pedidosservice.users.dtos.UserResponseDTO;
import com.andre.pedidosservice.users.core.UserStatus;
import com.andre.pedidosservice.users.gateways.in.UserAuthenticationService;
import com.andre.pedidosservice.users.gateways.in.UserServiceGateway;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
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
        System.out.println(id);
        return ResponseEntity.ok().build();

    }
}
