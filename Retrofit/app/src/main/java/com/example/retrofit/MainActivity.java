package com.example.retrofit;

import android.os.Bundle;


import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.retrofit.models.EntityModel;
import com.example.retrofit.repositories.EntityRepository;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        try {
            EntityRepository rep = EntityRepository.getInstance();
            EntityModel model = EntityRepository.createModel();
            model.setId(10);
            model.setName("Hello World");
            rep.create(model);

            ArrayList<EntityModel> models = rep.readAll();
            rep.readAllFromNet(new Callback<ArrayList<EntityModel>>() {
                @Override
                public void onResponse(Call<ArrayList<EntityModel>> call, Response<ArrayList<EntityModel>> response) {
                    if (response.isSuccessful()) {
                        ArrayList<EntityModel> entityModels =  new ArrayList<>();
                        entityModels.addAll(response.body());
                        for (int i = 0; i < entityModels.size(); i++) {
                            Log.d("test_models", entityModels.get(i).getName());
                            // findViewById();
                        }
                    } else {
                        // Handle exceptions
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<EntityModel>> call, Throwable t) {
                    // Handle exceptions
                }
            });

            EntityModel modelById = rep.getById(10);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}