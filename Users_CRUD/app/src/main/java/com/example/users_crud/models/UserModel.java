package com.example.users_crud.models;

import androidx.annotation.NonNull;

import java.util.UUID;

public class UserModel {

    private UUID id;

    private String firstname;
    private String lastname;

    private int age;

    public UUID getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getAge() {
        return age;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @NonNull
    @Override
    public String toString() {
        return firstname + " " + lastname + ": " + age;
    }
}