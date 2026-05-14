package com.andre.pedidosservice.users.core.domain;


import com.andre.pedidosservice.orders.core.domain.OrderDomain;

public class UserDomain {

    private String nome;
    private String email;
    private String senha;
    private String id;
    private String status;
    private OrderDomain orders;

    public UserDomain(String nome, String email, String senha, String id, String status, OrderDomain orders) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.id = id;
        this.status = status;
        this.orders = orders;
    }


    public UserDomain() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OrderDomain getOrders() {
        return orders;
    }

    public void setOrders(OrderDomain orders) {
        this.orders = orders;
    }
}
