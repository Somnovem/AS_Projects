package com.example.musicplayer.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.musicplayer.SongPlayerActivity;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && SongPlayerActivity.instance != null) {
            switch (action) {
                case NotificationConstants.ACTION_PLAY_PAUSE:
                    SongPlayerActivity.instance.togglePlayPause();
                    break;
                case NotificationConstants.ACTION_NEXT:
                    SongPlayerActivity.instance.nextSong();
                    break;
                case NotificationConstants.ACTION_PREVIOUS:
                    SongPlayerActivity.instance.previousSong();
                    break;
            }
        }
    }
}
