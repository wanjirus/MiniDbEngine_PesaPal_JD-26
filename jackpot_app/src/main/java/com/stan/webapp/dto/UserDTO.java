package com.stan.webapp.dto;

public class UserDTO {
    public int id;
    public String name;
    public int age;

    public UserDTO() {}

    public UserDTO(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
