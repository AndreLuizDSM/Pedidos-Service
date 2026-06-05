package com.andre.pedidosservice.user.core.domain;


import com.andre.pedidosservice.exception.exceptions.InvalidRequestException;
import com.andre.pedidosservice.order.core.domain.OrderDomain;
import com.andre.pedidosservice.user.core.UserStatus;

import java.util.ArrayList;
import java.util.List;

public class UserDomain {

    private String name;
    private String email;
    private String password;
    private String id;
    private UserStatus status;

    //OneToMany
    private List<OrderDomain> order = new ArrayList<>();


    public UserDomain(String name, String email, String password, String id, UserStatus status) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.id = id;
        this.status = status;
    }

    public UserDomain(OrderDomain order, UserStatus status, String id, String password, String email, String name) {
        this.order.add(order);

        this.status = status;
        this.id = id;
        this.password = password;
        this.email = email;
        this.name = name;
    }

    public UserDomain() {
    }

    public static UserDomain newUser(String name, String email, String password){
        if(!email.contains("@") || !email.contains(".")){
            throw new InvalidRequestException("Email invalido");
        }
        UserDomain domain = new UserDomain();
        domain.setName(name);
        domain.setEmail(email);
        domain.setPassword(password);
        domain.setStatus(UserStatus.CLIENT);

        return domain;
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

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public List<OrderDomain> getOrder() {
        return order;
    }

    public void setOrder(List<OrderDomain> order) {
        this.order = order;
    }
}
