package com.andre.pedidosservice.users.adapters.out;

import com.andre.pedidosservice.users.core.domain.UserDomain;
import com.andre.pedidosservice.users.gateways.out.IUserServiceGateway;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    public final IUserServiceGateway serviceGateway;

    @PostMapping("/login")
    public ResponseEntity<String> login (@Valid @RequestBody UserLoginDTO dto){
        return ResponseEntity.ok(serviceGateway.login(dto));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> saveUser (@Valid @RequestBody UserRequestDTO dto){
        return ResponseEntity.ok(serviceGateway.saveUser(dto))
    }

    @GetMapping
    public ResponseEntity<UserResponseDTO> getUserById (@PathVariable("id") String id){
        return ResponseEntity.ok(serviceGateway.saveUser(dto));
    }

    public ResponseEntity<Void> deleteUserById(@PathVariable("id") String id){
        return ResponseEntity.ok().build();

    }
}
