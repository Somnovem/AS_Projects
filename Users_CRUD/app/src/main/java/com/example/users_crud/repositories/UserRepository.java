package com.example.users_crud.repositories;

import com.example.users_crud.models.UserModel;

import java.util.ArrayList;
import java.util.UUID;

public class UserRepository {

    ArrayList<UserModel> data;

    public static UserModel createModel() {
        UserModel newModel = new UserModel();
        newModel.setId(UUID.randomUUID());
        return newModel;
    }

    public void add(UserModel model) {
        data.add(model);
    }


    private void seed() {
        UserModel john = new UserModel();
        john.setId(UUID.randomUUID());
        john.setFirstname("John");
        john.setLastname("Doe");
        john.setAge(24);
        data.add(john);

        UserModel steve = new UserModel();
        steve.setId(UUID.randomUUID());
        steve.setFirstname("Steve");
        steve.setLastname("Jenkins");
        steve.setAge(31);
        data.add(steve);

        UserModel maria = new UserModel();
        maria.setId(UUID.randomUUID());
        maria.setFirstname("Maria");
        maria.setLastname("Garcon");
        maria.setAge(27);
        data.add(maria);
    }

    private UserRepository() {
        data = new ArrayList<>();
        seed();
    }

    private static UserRepository instance;

    public static UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    public ArrayList<UserModel> getData() {
        return data;
    }
}