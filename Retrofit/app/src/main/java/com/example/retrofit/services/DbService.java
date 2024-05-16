package com.example.retrofit.services;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.app.AppCompatActivity;

public class DbService {

    private SQLiteDatabase db;

    private static DbService instance;

    private static String dbName = "app.db";

    AppCompatActivity activity;

    private DbService(AppCompatActivity activity) {
        this.activity = activity;
        db = this.activity.getBaseContext().openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
    }

    public static DbService getInstance(AppCompatActivity activity) {
        if (instance == null) {
            instance = new DbService(activity);
        }
        return instance;
    }

    public void execSQL (String sqlQuery) {
        db.execSQL(sqlQuery);
    }

    public Cursor rawQuery(String sqlQuery) {
        return db.rawQuery(sqlQuery, null);
    }



    public static DbService getInstance() throws Exception {
        if (instance == null) {
            throw new Exception(" DB Not Found ");
        }
        return instance;
    }
}
