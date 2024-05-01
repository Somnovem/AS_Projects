package com.example.custom_notification;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private final String CHANNEL_ID = "MY_CHANNEL_ID";

    private PendingIntent pendingIntent;

    private NotificationManager notificationManager;

    private EditText inputEditNotification;

    private void setupNotifications() {
        Intent intentNotification = new Intent(this, MainActivity.class);

        this.pendingIntent = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);

        this.notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Custom Notification App", NotificationManager.IMPORTANCE_DEFAULT);

        notificationManager.createNotificationChannel(notificationChannel);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        setupNotifications();

        inputEditNotification = findViewById(R.id.inputNotificationText);
    }

    public void btnSendNotification_click(View view) {
        String notificationText = inputEditNotification.getText().toString();

        if (notificationText.isEmpty()) return;

        NotificationCompat.Builder builderNotificationCompat = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(this.pendingIntent)
                .setContentTitle("Custom Notification")
                .setContentText(notificationText)
                .setSmallIcon(android.R.drawable.btn_star_big_on);

        this.notificationManager.notify(0, builderNotificationCompat.build());
    }

    public void btnEditNotification_click(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your notification text:");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                inputEditNotification.setText(input.getText().toString());
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}