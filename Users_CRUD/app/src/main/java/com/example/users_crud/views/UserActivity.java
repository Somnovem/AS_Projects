package com.example.users_crud.views;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.users_crud.R;
import com.example.users_crud.viewmodels.UserViewModel;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_view);

        UserViewModel viewModel = new UserViewModel(this);
    }


}
