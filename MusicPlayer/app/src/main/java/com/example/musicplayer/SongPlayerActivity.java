package com.example.musicplayer;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.musicplayer.notifications.NotificationConstants;
import com.example.musicplayer.notifications.NotificationHelper;
import com.example.musicplayer.notifications.NotificationReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class SongPlayerActivity extends AppCompatActivity {

    private ImageView albumCoverImageView;
    private TextView songTitleTextView;
    private TextView songArtistTextView;
    private ImageButton btnPlayPause;
    private ImageButton btnLoop;
    private SeekBar seekBar;
    private TextView currentTimeText;
    private TextView songDurationText;

    private static MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable updateSeekBar;

    private boolean isPlaying = false;
    private List<Song> playlist;
    private int currentIndex;
    private BroadcastReceiver notificationReceiver = new NotificationReceiver();
    public static SongPlayerActivity instance = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);

        handleUI();

        handler = new Handler(Looper.getMainLooper());

        if (getIntent().getExtras() != null) {
            playlist = getIntent().getParcelableArrayListExtra("playlist");
            currentIndex = getIntent().getIntExtra("currentIndex", 0);
        }

        if (mediaPlayer == null) loadSongData();
        else {
            handleSongUI(playlist.get(currentIndex));
            mediaPlayer.setOnCompletionListener(mp -> nextSong());
            btnPlayPause.setImageResource(mediaPlayer.isPlaying() ? R.drawable.ic_pause : R.drawable.ic_play);
        }

        NotificationHelper.createNotificationChannel(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTION_PLAY_PAUSE");
        filter.addAction("ACTION_NEXT");
        filter.addAction("ACTION_PREVIOUS");
        registerReceiver(notificationReceiver, filter);

        if (instance == null) instance = this;
    }

    private void handleUI() {
        albumCoverImageView = findViewById(R.id.album_cover);
        songTitleTextView = findViewById(R.id.song_title);
        songArtistTextView = findViewById(R.id.artist_name);
        ImageButton btnPrevious = findViewById(R.id.btn_previous);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnLoop = findViewById(R.id.btn_loop);
        ImageButton btnNext = findViewById(R.id.btn_next);
        seekBar = findViewById(R.id.seek_bar);
        currentTimeText = findViewById(R.id.current_time_text);
        songDurationText = findViewById(R.id.song_duration_text);

        btnPrevious.setOnClickListener(v -> previousSong());
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnLoop.setOnClickListener(v -> toggleLooping());
        btnNext.setOnClickListener(v -> nextSong());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    currentTimeText.setText(formatTime(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    handler.removeCallbacks(updateSeekBar);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(seekBar.getProgress());
                    handler.post(updateSeekBar);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getExtras() != null) {
            playlist = intent.getParcelableArrayListExtra("playlist");
            currentIndex = intent.getIntExtra("currentIndex", 0);
        }
    }

    private void handleSongUI(Song currentSong) {
        String songPath = currentSong.getPath();
        String songTitle = currentSong.getTitle();
        String songArtist = currentSong.getArtist();

        songTitleTextView.setText(songTitle.substring(0, songTitle.length() - 4));
        songArtistTextView.setText(songArtist);

        Bitmap albumArt = getAlbumArt(songPath);
        if (albumArt != null) {
            albumCoverImageView.setImageBitmap(albumArt);
        } else {
            albumCoverImageView.setImageResource(R.drawable.album_placeholder);
        }
        if (mediaPlayer != null) {
            handleMediaPlayerUI();
        }
    }

    private void handleMediaPlayerUI() {
        currentTimeText.setText(formatTime(mediaPlayer.getCurrentPosition()));
        int duration = mediaPlayer.getDuration();
        seekBar.setMax(duration);
        String durationFormatted = formatTime(duration);
        songDurationText.setText(durationFormatted);

        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    currentTimeText.setText(formatTime(currentPosition));
                }
                if (mediaPlayer != null && mediaPlayer.getCurrentPosition() < mediaPlayer.getDuration() - 1000) {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(updateSeekBar, 1000);
    }

    private void loadSongData() {
        if (playlist != null && !playlist.isEmpty()) {
            Song currentSong = playlist.get(currentIndex);
            handleSongUI(currentSong);

            String songPath = currentSong.getPath();

            try {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(songPath));
                mediaPlayer.prepare();

                togglePlayPause();

                mediaPlayer.setOnCompletionListener(mp -> nextSong());

                handleMediaPlayerUI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void previousSong() {
        if (playlist != null && !playlist.isEmpty()) {
            currentIndex = (currentIndex - 1 + playlist.size()) < 0 ? 0 : (currentIndex - 1 + playlist.size()) % playlist.size();
            loadSongData();
        }
    }

    public void nextSong() {
        if (playlist != null && !playlist.isEmpty()) {
            currentIndex = (currentIndex + 1) % playlist.size();
            loadSongData();
        }
    }

    private Bitmap getAlbumArt(String songPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(songPath);
            byte[] albumArt = retriever.getEmbeddedPicture();
            if (albumArt != null) {
                return BitmapFactory.decodeByteArray(albumArt, 0, albumArt.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private String formatTime(int milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public void togglePlayPause() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlayPause.setImageResource(R.drawable.ic_play);
                showNotification(playlist.get(currentIndex), false);
            } else {
                mediaPlayer.start();
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                showNotification(playlist.get(currentIndex), true);
            }
            isPlaying = !isPlaying;
        }
    }

    private void toggleLooping() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isLooping()) {
                mediaPlayer.setLooping(false);
                btnLoop.setImageResource(R.drawable.ic_no_loop);
            }
            else {
                mediaPlayer.setLooping(true);
                btnLoop.setImageResource(R.drawable.ic_loop);
            }
        }
    }

    private void showNotification(Song song, boolean isPlaying) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Intent playPauseIntent = new Intent(this, NotificationReceiver.class).setAction(NotificationConstants.ACTION_PLAY_PAUSE);
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(this, NotificationReceiver.class).setAction(NotificationConstants.ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(this, NotificationReceiver.class).setAction(NotificationConstants.ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent activityIntent = new Intent(this, SongPlayerActivity.class);
        activityIntent.putExtra("playlist", new ArrayList<>(playlist));
        activityIntent.putExtra("currentIndex", currentIndex);
        activityIntent.putExtra("currentPosition", mediaPlayer.getCurrentPosition());
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap albumArt = getAlbumArt(song.getPath());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music_note)
                .setContentTitle(song.getTitle().substring(0, song.getTitle().length() - 4))
                .setContentText(song.getArtist())
                .setLargeIcon(albumArt != null ? albumArt : BitmapFactory.decodeResource(getResources(), R.drawable.album_placeholder))
                .setContentIntent(activityPendingIntent)
                .addAction(R.drawable.ic_previous, "Previous", previousPendingIntent)
                .addAction(isPlaying ? R.drawable.ic_pause : R.drawable.ic_play, "Play/Pause", playPausePendingIntent)
                .addAction(R.drawable.ic_next, "Next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }




    @Override
    protected void onResume() {
        super.onResume();
        NotificationManagerCompat.from(this).cancel(1);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            showNotification(playlist.get(currentIndex), true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            showNotification(playlist.get(currentIndex), false); // Keep notification when stopped
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
        unregisterReceiver(notificationReceiver);
        instance = null;

        NotificationManagerCompat.from(this).cancel(1);
    }
}
