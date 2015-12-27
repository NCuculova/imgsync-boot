package com.ncuculova.oauth2.demogallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ncuculova.oauth2.demogallery.model.Album;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.Preferences;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by ncuculova on 6.12.15.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    List<Album> mAlbums;
    Context mContext;
    AlbumViewHolder.OnAlbumClickListener mOnAlbumClickListener;
    Preferences mPreferences;

    public AlbumAdapter(Context context) {
        this.mAlbums = new ArrayList<>();
        mContext = context;
        mPreferences = Preferences.getInstance(context);
    }

    public void addAlbum(Album album) {
        mAlbums.add(album);
        notifyDataSetChanged();
    }

    public void clear() {
        mAlbums.clear();
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.album_card_view, parent, false);
        AlbumViewHolder albumViewHolder = new AlbumViewHolder(mContext, view);
        albumViewHolder.mOnAlbumClickListener = mOnAlbumClickListener;
        return albumViewHolder;
    }

    @Override
    public void onBindViewHolder(AlbumViewHolder holder, int position) {
        Album album = mAlbums.get(position);
        String title = String.format("%s (%d)", album.name, album.numberImages);
        holder.mTitle.setText(title);
        holder.mDate.setText(album.dateCreated);
        holder.mAlbum = album;
        holder.mPosition = position;
        String imageUrl = String.format("%s/api/image_thumb/%d", DemoGalleryHttpClient.BASE_URI, album.coverImageId);
        Picasso picasso = DemoGalleryHttpClient.getPicassoClient(mContext);
        picasso.load(imageUrl)
                .placeholder(R.drawable.ic_action_download)
                .into(holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public void deleteAt(int position) {
        mAlbums.remove(position);
        notifyDataSetChanged();
    }

    public static class AlbumViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_title)
        TextView mTitle;

        @Bind(R.id.tv_date)
        TextView mDate;

        @Bind(R.id.image)
        ImageView mImageView;

        int mPosition;
        Album mAlbum;
        OnAlbumClickListener mOnAlbumClickListener;
        DemoGalleryHttpClient mClient;

        public AlbumViewHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(mOnClickListener);
            mClient = DemoGalleryHttpClient.getInstance(context);
        }

        @OnClick(R.id.btn_delete)
        void deleteAlbum() {
            if (mOnAlbumClickListener != null) {
                mOnAlbumClickListener.onAlbumDelete(mPosition, mAlbum);
            }
        }

        final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAlbumClickListener != null) {
                    mOnAlbumClickListener.onAlbumClick(mPosition, mAlbum);
                }
            }
        };

        public interface OnAlbumClickListener {
            void onAlbumClick(int position, Album album);

            void onAlbumDelete(int position, Album album);
        }

    }

    public void setOnAlbumClickListener(AlbumViewHolder.OnAlbumClickListener mOnAlbumClickListener) {
        this.mOnAlbumClickListener = mOnAlbumClickListener;
    }
}
