package com.api.focusmate.model;

import javax.persistence.*;

@Entity
@Table(name = "\"user\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "telegram", unique = true)
    private Long telegram;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getTelegram() {
        return telegram;
    }

    public void setTelegram(Long telegram) {
        this.telegram = telegram;
    }

    @Override
    public String toString() {
        return "User{" +
                "token=" + token +
                ", telegram=" + telegram +
                '}';
    }
}