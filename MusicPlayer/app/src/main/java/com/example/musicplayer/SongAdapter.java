package com.example.musicplayer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Song song);
    }

    private List<Song> songList;
    private OnItemClickListener listener;

    public SongAdapter(List<Song> songList, OnItemClickListener listener) {
        this.songList = songList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Song song = songList.get(position);
        try {
            holder.bind(song, listener);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    static class SongViewHolder extends RecyclerView.ViewHolder {

        private ImageView songImage;
        private TextView songName;
        private TextView songArtist;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImage = itemView.findViewById(R.id.song_image);
            songName = itemView.findViewById(R.id.song_name);
            songArtist = itemView.findViewById(R.id.song_artist);
        }

        public void bind(final Song song, final OnItemClickListener listener) throws IOException {
            Bitmap albumArt = getAlbumArt(song.getPath());
            if (albumArt != null) {
                songImage.setImageBitmap(albumArt);
            } else {
                songImage.setImageResource(R.drawable.ic_music_note);
            }
            String songTitle = song.getTitle();
            songName.setText(songTitle.substring(0,songTitle.length() - 4));
            songArtist.setText(song.getArtist());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(song);
                }
            });
        }

        private Bitmap getAlbumArt(String songPath) throws IOException {
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
                retriever.release();
            }
            return null;
        }
    }
}
