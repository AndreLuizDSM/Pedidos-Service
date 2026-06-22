package com.andre.pedidosservice.user.adapters.in;

import com.andre.pedidosservice.security.SecurityConfig;
import com.andre.pedidosservice.user.core.domain.UserDomain;
import com.andre.pedidosservice.user.dtos.UserLoginDTO;
import com.andre.pedidosservice.user.dtos.UserMapper;
import com.andre.pedidosservice.user.dtos.UserRequestDTO;
import com.andre.pedidosservice.user.dtos.UserResponseDTO;
import com.andre.pedidosservice.user.core.UserStatus;
import com.andre.pedidosservice.user.gateways.in.UserAuthenticationService;
import com.andre.pedidosservice.user.gateways.in.UserServiceGateway;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Users")
@SecurityRequirement(name = SecurityConfig.SECURITY_SCHEME)
public class UserController {

    private final UserServiceGateway serviceGateway;
    private final UserAuthenticationService authentication;
    private final UserMapper mapper;

    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Login the user")
    @ApiResponse(responseCode = "200" , description = "Login sucess")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginDTO dto) {
        return ResponseEntity.ok(authentication.login(mapper.loginToDomain(dto)));
    }

    @PostMapping
    @Operation(summary = "Create user", description = "Register a new user in the system")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserDomain domain = serviceGateway.createUser(dto.name(), dto.email(), dto.password());
        return ResponseEntity.ok(mapper.domainToResponse(domain));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update user status", description = "Update the status of an existing user")
    @ApiResponse(responseCode = "200", description = "User status updated successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponseDTO> updateUserStatus(@PathVariable("id") String id,
                                                            @RequestParam("status") UserStatus status) {
        return ResponseEntity.ok(mapper.domainToResponse(serviceGateway.updateUserStatus(id, status)));
    }


    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "Retrieve a user by its identifier")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable("id") String id) {
        return ResponseEntity.ok(mapper.domainToResponse(serviceGateway.getUserById(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by its identifier (ADMIN only)")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden: requires ADMIN role")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") String id) {
        serviceGateway.deleteUser(id);
        return ResponseEntity.ok().build();

    }
}
