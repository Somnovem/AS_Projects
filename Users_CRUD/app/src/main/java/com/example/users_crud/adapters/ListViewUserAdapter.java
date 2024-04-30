package com.example.users_crud.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.users_crud.R;
import com.example.users_crud.models.UserModel;

import java.util.ArrayList;

public class ListViewUserAdapter extends ArrayAdapter<UserModel>
{
    LayoutInflater layoutInflater;
    int layout;
    ArrayList<UserModel> models;

    public ListViewUserAdapter(@NonNull Context context, int layout, ArrayList<UserModel> models) {
        super(context, layout, models);
        this.models = models;
        this.layout = layout;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(layout, parent, false);
        }

        TextView txtFirstname = ((TextView)convertView.findViewById(R.id.user_firstname));
        TextView txtLastname = ((TextView)convertView.findViewById(R.id.user_lastname));
        TextView txtAge = ((TextView)convertView.findViewById(R.id.user_age));

        txtFirstname.setText(models.get(position).getFirstname());
        txtLastname.setText(models.get(position).getLastname());
        txtAge.setText(" => " + models.get(position).getAge());

        Button del = convertView.findViewById(R.id.user_btn_del);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                models.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }


}