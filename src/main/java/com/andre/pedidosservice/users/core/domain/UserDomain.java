package com.andre.pedidosservice.users.core.domain;


import com.andre.pedidosservice.orders.core.domain.OrderDomain;
import com.andre.pedidosservice.users.entities.StatusEnum;

public class UserDomain {

    private String name;
    private String email;
    private String password;
    private String id;
    private StatusEnum status;


    public UserDomain(String name, String email, String password, String id, StatusEnum status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.id = id;
        this.status = status;
    }


    public UserDomain() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
        this.status = status;
    }

}
