package com.example.seven_segment_timer;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private boolean[][] segmentStates = {
            {true, true, true, true, true, true, false},
            {false, true, true, false, false, false, false},
            {true, true, false, true, true, false, true},
            {true, true, true, true, false, false, true},
            {false, true, true, false, false, true, true},
            {true, false, true, true, false, true, true},
            {true, false, true, true, true, true, true},
            {true, true, true, false, false, false, false},
            {true, true, true, true, true, true, true},
            {true, true, true, true, false, true, true}
    };

    private ArrayList<Button> segments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Button btnA = findViewById(R.id.btnA);
        Button btnB = findViewById(R.id.btnB);
        Button btnC = findViewById(R.id.btnC);
        Button btnD = findViewById(R.id.btnD);
        Button btnE = findViewById(R.id.btnE);
        Button btnF = findViewById(R.id.btnF);
        Button btnG = findViewById(R.id.btnG);

        segments.add(btnA);
        segments.add(btnB);
        segments.add(btnC);
        segments.add(btnD);
        segments.add(btnE);
        segments.add(btnF);
        segments.add(btnG);

        setDigit(9);
        int delayCounter = 2;
        for (int i = 9; i >= 0; i--) {
            Handler handler = new Handler();
            int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setDigit(finalI);
                }
            }, 1000L *delayCounter++);
        }
    }

    private void setDigit(int digit) {
        if (digit < 0 || digit > 9) return;
        for (int i = 0; i < 7; i++) {
            if(segmentStates[digit][i]) segments.get(i).setBackgroundColor(Color.RED);
            else segments.get(i).setBackgroundColor(Color.WHITE);
        }
    }
}