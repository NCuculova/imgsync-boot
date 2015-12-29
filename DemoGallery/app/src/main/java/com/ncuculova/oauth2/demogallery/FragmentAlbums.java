package com.ncuculova.oauth2.demogallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ncuculova.oauth2.demogallery.model.Album;
import com.ncuculova.oauth2.demogallery.util.ApproveDialog;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

/**
 * Created by ncuculova on 6.12.15.
 */
public class FragmentAlbums extends Fragment implements LoaderManager.LoaderCallbacks<List<Album>>, DialogInterface.OnClickListener {

    @Bind(R.id.rv_albums)
    RecyclerView mRecyclerView;

    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;

    ApproveDialog mApproveDialog;
    GridLayoutManager mManager;
    AlbumAdapter mAlbumAdapter;
    DemoGalleryHttpClient mClient;
    Preferences mPreferences;
    long mAlbumId;
    int mAlbumPosition;

    public FragmentAlbums() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums, container, false);
        ButterKnife.bind(this, view);

        mClient = DemoGalleryHttpClient.getInstance(getContext());
        mPreferences = Preferences.getInstance(getContext());
        mAlbumAdapter = new AlbumAdapter(getContext());
        mManager = new GridLayoutManager(getContext(), 2);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mAlbumAdapter);
        mAlbumAdapter.setOnAlbumClickListener(mOnAlbumClickListener);
        mApproveDialog = new ApproveDialog();
        mApproveDialog.setOnDialogClickListener(this);
        loadAlbum(false);
    }

    AlbumAdapter.AlbumViewHolder.OnAlbumClickListener mOnAlbumClickListener = new AlbumAdapter.AlbumViewHolder.OnAlbumClickListener() {
        public void onAlbumClick(int position, Album album) {
            Intent intent = new Intent(getContext(), AlbumImagesActivity.class);
            intent.putExtra("album_id", album.id);
            intent.putExtra("album_name", album.name);
            startActivity(intent);
        }

        @Override
        public void onAlbumDelete(final int position, Album album) {
            mAlbumId = album.id;
            mAlbumPosition = position;
            mApproveDialog.show(getFragmentManager(), "delete_album");
        }
    };

    @OnClick(R.id.fab_add_album)
    public void createAlbum() {
        Intent intent = new Intent(getContext(), AlbumImagesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    void loadAlbum(boolean isReload) {
        Bundle bundle = new Bundle();
        bundle.putLong("userId", 1);
        if (isReload) {
            getLoaderManager().restartLoader(0, bundle, this);
        } else {
            getLoaderManager().initLoader(0, bundle, this);
        }
    }

    @Override
    public Loader<List<Album>> onCreateLoader(int id, Bundle args) {
        long userId = args.getLong("userId");
        mProgressBar.setVisibility(View.VISIBLE);
        return new AlbumsLoader(getContext(), userId);
    }

    @Override
    public void onLoadFinished(Loader<List<Album>> loader, List<Album> data) {
        mAlbumAdapter.clear();
        mProgressBar.setVisibility(View.GONE);
        for (Album a : data) {
            mAlbumAdapter.addAlbum(a);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Album>> loader) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEGATIVE) {
            dialog.dismiss();
        } else {
            mClient.deleteAlbum(mAlbumId, new DemoGalleryHttpClient.ResponseHandler() {
                @Override
                public void onSuccessJsonObject(JSONObject jsonObject) {
                    super.onSuccessJsonObject(jsonObject);
                    mAlbumAdapter.deleteAt(mAlbumPosition);
                    try {
                        System.out.println(jsonObject.getString("response"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
