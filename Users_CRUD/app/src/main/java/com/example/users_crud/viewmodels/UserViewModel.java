package com.example.users_crud.viewmodels;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.users_crud.R;
import com.example.users_crud.adapters.ListViewUserAdapter;
import com.example.users_crud.adapters.SpinnerUserAdapter;
import com.example.users_crud.models.UserModel;
import com.example.users_crud.repositories.UserRepository;
import com.example.users_crud.views.UserActivity;

public class UserViewModel {

    private final UserRepository repository;
    private final UserActivity activity;

    ListViewUserAdapter listViewAdapter;

    public UserViewModel(UserActivity activity) {
        this.activity  = activity;
        this.repository = UserRepository.getInstance();

        listView = activity.findViewById(R.id.lst_users);

        createList();

        createSpinner();
    }

    private void createSpinner() {
        Spinner spinner = activity.findViewById(R.id.spinner_users);

        SpinnerUserAdapter adapter = new SpinnerUserAdapter(
                this.activity, R.layout.user_spinner_item_view, repository.getData());

        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        spinner.setAdapter(adapter);
    }

    public void createList() {
        listViewAdapter = new ListViewUserAdapter(this.activity, R.layout.user_list_item_view, repository.getData());
        listView.setAdapter(listViewAdapter);

        EditText txtFirstname = activity.findViewById(R.id.user_input_firstname);
        EditText txtLastname = activity.findViewById(R.id.user_input_lastname);
        EditText txtAge = activity.findViewById(R.id.user_input_age);

        Button create = activity.findViewById(R.id.user_btn_create);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel newModel = UserRepository.createModel();
                newModel.setFirstname(txtFirstname.getText().toString());
                newModel.setLastname(txtLastname.getText().toString());
                newModel.setAge(Integer.parseInt(txtAge.getText().toString()));
                repository.add(newModel);
                listViewAdapter.notifyDataSetChanged();

                txtAge.setText(null);
                txtFirstname.setText(null);
                txtLastname.setText(null);
            }
        });

    }


    ListView listView;
}