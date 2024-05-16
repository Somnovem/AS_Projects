package com.example.retrofit.repositories;

import android.database.Cursor;

import com.example.retrofit.clients.MockapiClient;
import com.example.retrofit.models.EntityModel;
import com.example.retrofit.services.ApiService;
import com.example.retrofit.services.DbService;

import retrofit2.Call;
import retrofit2.Callback;

import java.util.ArrayList;

public class EntityRepository {

    private static String tableName ="entities";
    private DbService dbService;
    private ApiService apiService;

    private EntityRepository() throws Exception {
        dbService = DbService.getInstance();
        apiService = MockapiClient.getInstance().create(ApiService.class);
        migration();
    }

    private void migration() {
        dbService.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (id INTEGER, name TEXT)");
    }

    public static EntityModel createModel() {
        return new EntityModel();
    }

    public void create(EntityModel model) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(tableName);
        sb.append(" VALUES (");
        sb.append(model.getId());
        sb.append(", '");
        sb.append(model.getName());
        sb.append("');");

        dbService.execSQL(sb.toString());
    }

    public ArrayList<EntityModel>  readAll() {
        Cursor cursor = dbService.rawQuery("SELECT * FROM " + tableName);
        ArrayList<EntityModel> models = new ArrayList<>();
        while (cursor.moveToNext()){
            EntityModel model = new EntityModel();
            model.setId(cursor.getInt(0));
            model.setName(cursor.getString(1));
            models.add(model);
        }
        return models;
    }

    public void readAllFromNet(Callback<ArrayList<EntityModel>> callback) {

        ArrayList<EntityModel> entityModels = new ArrayList<>();
        Call<ArrayList<EntityModel>> call = apiService.getAll();
        call.enqueue(callback);

    }

    public EntityModel getById(int id) {
        Cursor cursor = dbService.rawQuery("SELECT * FROM " + tableName + " WHERE id =" + id);
        cursor.moveToNext();
        EntityModel model = new EntityModel();
        model.setId(cursor.getInt(0));
        model.setName(cursor.getString(1));
        return model;
    }


    private static EntityRepository instance;
    public static EntityRepository getInstance() throws Exception {
        if(instance == null) {
            instance = new EntityRepository();
        }
        return instance;
    }
}