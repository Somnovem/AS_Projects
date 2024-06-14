package com.example.musicplayer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String TAG = "MainActivity";
    private TextView loadingText;
    private RecyclerView recyclerView;
    private Button buttonShuffle;
    private SongAdapter songAdapter;
    private List<Song> songList;
    private ExecutorService executorService;
    private Handler handler;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingText = findViewById(R.id.loading_text);
        recyclerView = findViewById(R.id.recycler_view);
        buttonShuffle = findViewById(R.id.button_shuffle);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        songList = new ArrayList<>();
        songAdapter = new SongAdapter(songList, new SongAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Song song) {
                Intent intent = new Intent(MainActivity.this, SongPlayerActivity.class);
                intent.putParcelableArrayListExtra("playlist", new ArrayList<>(songList));
                intent.putExtra("currentIndex", songList.indexOf(song));
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(songAdapter);

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
        else if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
        else {
            loadSongs();
        }

        buttonShuffle.setOnClickListener(v -> shufflePlaylist());
    }

    private void loadSongs() {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Song> songs = new ArrayList<>();
                File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (downloadsFolder.exists() && downloadsFolder.isDirectory()) {
                    for (File file : Objects.requireNonNull(downloadsFolder.listFiles())) {
                        if (file.isFile() && file.getName().endsWith(".mp3")) {
                            String title = file.getName();
                            String artist = "Unknown Artist";
                            String filePath = file.getPath();

                            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                            retriever.setDataSource(filePath);

                            String titleFromMetadata = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                            String artistFromMetadata = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                            Log.d("TAG","Artist: " + artistFromMetadata);

                            if (titleFromMetadata != null) {
                                title = titleFromMetadata;
                            }
                            if (artistFromMetadata != null) {
                                artist = artistFromMetadata;
                            }

                            songs.add(new Song(title, artist, filePath));

                            try {
                                retriever.release();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                // Update UI on the main thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        songList.clear();
                        songList.addAll(songs);
                        songAdapter.notifyDataSetChanged();
                        loadingText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void shufflePlaylist() {
        Intent intent = new Intent(MainActivity.this, SongPlayerActivity.class);
        ArrayList<Song> shuffledPlaylist = new ArrayList<>(songList);
        Collections.shuffle(shuffledPlaylist);
        intent.putParcelableArrayListExtra("playlist", shuffledPlaylist);
        intent.putExtra("currentIndex", 0);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
