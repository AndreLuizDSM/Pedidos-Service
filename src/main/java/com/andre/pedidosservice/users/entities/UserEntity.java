package com.andre.pedidosservice.users.entities;

import com.andre.pedidosservice.users.core.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "table_user")
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "name", length = 80)
    private String name;

    @Column(name = "email", length = 80 , unique = true)
    private String email;

    @Column(name = "password", length = 80)
    private String password;

    @Column(name = "status")
    private UserStatus status;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                // () -> status.name()    1º maneira
                // new SimpleGrantedAuthority(status.name())    2º maneira
        );
    }

    @Override
    public String getUsername() { return id;
    }

}
