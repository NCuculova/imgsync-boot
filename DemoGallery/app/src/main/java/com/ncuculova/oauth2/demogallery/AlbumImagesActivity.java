package com.ncuculova.oauth2.demogallery;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ncuculova.oauth2.demogallery.model.Image;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * Preview all created albums and containing images
 */
public class AlbumImagesActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Image>> {

    @Bind(R.id.rv_images)
    RecyclerView mRecyclerView;

    GridLayoutManager mManager;

    @Bind(R.id.et_album_name)
    EditText mEtAlbumName;

    @Bind(R.id.fab_add_image)
    FloatingActionButton mFabAddImage;

    @Bind(R.id.fab_save_album)
    FloatingActionButton mFabSaveAlbum;

    @Bind(R.id.pb_save)
    ProgressBar mPbSaveProgress;

    @Bind(R.id.progress_bar)
    ProgressBar mPbProgress;

    @Bind(R.id.pb_load_img)
    ProgressBar mPbLoadImages;

    public static final int RESULT_LOAD_IMG = 0;
    long mAlbumId;
    private long mDownloadReference;
    String mAlbumName;
    ImageAdapter mImageAdapter;
    DemoGalleryHttpClient mClient;
    DownloadManager mDownloadManager;
    Preferences mPreferences;
    static final String TAG = "AlbumImagesActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_images);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mAlbumId = intent.getLongExtra("album_id", 0);
        mAlbumName = intent.getStringExtra("album_name");

        mClient = DemoGalleryHttpClient.getInstance(this);
        mPreferences = Preferences.getInstance(this);
        mImageAdapter = new ImageAdapter(this, mAlbumId);
        mImageAdapter.setOnImageClickListener(mOnImageClickListener);
        mManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mManager);
        mRecyclerView.setAdapter(mImageAdapter);

        if (mAlbumId != 0) {
            mEtAlbumName.setText(mAlbumName);
            mFabAddImage.setVisibility(View.VISIBLE);
            loadImages(false);
        } else {
            mEtAlbumName.setFocusable(true);
        }
        mEtAlbumName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    createOrRenameAlbum();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //set filter to only when download is complete and register broadcast receiver
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(mDownloadReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mDownloadReceiver);
    }

    @OnClick(R.id.fab_add_image)
    void addImage() {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        // Start the Intent
        startActivityForResult(intent, RESULT_LOAD_IMG);
    }

    void createOrRenameAlbum() {
        final String textEntered = mEtAlbumName.getText().toString();
        mEtAlbumName.clearFocus();
        mPbSaveProgress.setVisibility(View.VISIBLE);
        mFabSaveAlbum.setVisibility(View.INVISIBLE);
        if (textEntered.equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter album name", Toast.LENGTH_SHORT).show();
            showButtons();
        } else if (mAlbumName == null) {
            mClient.createAlbum(textEntered, new DemoGalleryHttpClient.ResponseHandler() {
                @Override
                public void onSuccessJsonObject(JSONObject jsonObject) {
                    Toast.makeText(getApplicationContext(), "New album is created", Toast.LENGTH_SHORT).show();
                    try {
                        mAlbumId = jsonObject.getLong("id");
                        mAlbumName = jsonObject.getString("name");
                        mFabAddImage.setVisibility(View.VISIBLE);
                        showButtons();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (!mAlbumName.equals(textEntered)) {
            mAlbumName = textEntered;
            mClient.updateAlbum(mAlbumName, mAlbumId, mOnUpdateAlbumHandler);
        } else {
            showButtons();
        }
    }

    JsonHttpResponseHandler mOnUpdateAlbumHandler = new JsonHttpResponseHandler() {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            super.onSuccess(statusCode, headers, response);
            Toast.makeText(getApplicationContext(), "This album has been updated",
                    Toast.LENGTH_SHORT).show();
            showButtons();
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            super.onFailure(statusCode, headers, throwable, errorResponse);
            if (statusCode == 401) {
                mClient.getAccessTokenFromRefreshToken(new DemoGalleryHttpClient.ResponseHandler() {
                    @Override
                    public void onSuccessJsonObject(JSONObject jsonObject) {
                        super.onSuccessJsonObject(jsonObject);
                        mClient.updateAlbum(mAlbumName, mAlbumId, mOnUpdateAlbumHandler);
                    }
                });
            }
        }
    };

    void showButtons() {
        mPbSaveProgress.setVisibility(View.GONE);
        mFabSaveAlbum.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.fab_save_album)
    public void saveAlbum() {
        createOrRenameAlbum();
    }

    ImageAdapter.ImageViewHolder.OnImageClickListener mOnImageClickListener = new ImageAdapter.ImageViewHolder.OnImageClickListener() {

        @Override
        public void onImageDelete(final int position, long albumId, final long imgId) {
            mPbProgress.setVisibility(View.VISIBLE);
            mClient.deleteImage(albumId, imgId, new DemoGalleryHttpClient.ResponseHandler() {
                @Override
                public void onSuccessJsonObject(JSONObject jsonObject) {
                    super.onSuccessJsonObject(jsonObject);
                    mPbProgress.setVisibility(View.GONE);
                    mImageAdapter.deleteImageAt(position);
                    System.out.println(jsonObject.toString());
                }
            });
        }

        @Override
        public void onImageDownload(long imgId) {
            mDownloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            Uri resourceUrl = Uri.parse(String.format("%s/api/image/%d", DemoGalleryHttpClient.BASE_URI, imgId));
            final DownloadManager.Request request = new DownloadManager.Request(resourceUrl);
            request.addRequestHeader("Authorization", String.format("Bearer %s", mPreferences.getAccessToken()));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setTitle("Downloading...");
            request.setDescription("ImgSync is downloading the image");
            //Set the local destination for the downloaded file to a path within the application's
            // external files directory
            request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS,
                    getOutputMediaFileName());
            mClient.dummyRequest(new DemoGalleryHttpClient.ResponseHandler() {
                @Override
                public void onSuccessJsonObject(JSONObject jsonObject) {
                    super.onSuccessJsonObject(jsonObject);
                    //Enqueue a new download and save the referenceId
                    mDownloadReference = mDownloadManager.enqueue(request);
                    Log.d(TAG, "Download after access token is confirmed");
                }
            });
        }

        @Override
        public void onImageCardClick(long imgId) {
            Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
            intent.putExtra("img_id", imgId);
            startActivity(intent);
        }
    };

    /**
     * Create a File for saving an image or video
     */
    private static String getOutputMediaFileName() {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
        String fileName = "IMG_" + timeStamp + "";
        return fileName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && data != null) {
            mPbProgress.setVisibility(View.VISIBLE);
            Uri selectedImageURI = data.getData();
            String mimeType = getContentResolver().getType(selectedImageURI);
            Image image = new Image();
            image.mimeType = mimeType;
            Cursor returnCursor =
                    getContentResolver().query(selectedImageURI, null, null, null, null);
            if (returnCursor != null) {
                returnCursor.moveToFirst();
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                //int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                image.name = returnCursor.getString(nameIndex);
            }
            try {
                InputStream is = getContentResolver().openInputStream(selectedImageURI);
                mClient.uploadImage(mAlbumId, image, is, new DemoGalleryHttpClient.ResponseHandler() {
                    @Override
                    public void onFailureJsonObject(int statusCode, JSONObject jsonObject) {
                        mPbProgress.setVisibility(View.GONE);
                        System.out.println(jsonObject);
                    }

                    @Override
                    public void onSuccessJsonObject(JSONObject jsonObject) {
                        mPbProgress.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Image added to album",
                                Toast.LENGTH_SHORT).show();
                        loadImages(true);
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    void loadImages(boolean isReload) {
        Bundle bundle = new Bundle();
        bundle.putLong("album_id", mAlbumId);
        if (isReload) {
            getSupportLoaderManager().restartLoader(0, bundle, this);
        } else {
            getSupportLoaderManager().initLoader(0, bundle, this);
        }
    }

    @Override
    public Loader<List<Image>> onCreateLoader(int id, Bundle args) {
        long albumId = args.getLong("album_id");
        mPbLoadImages.setVisibility(View.VISIBLE);
        return new ImagesLoader(this, albumId);
    }

    @Override
    public void onLoadFinished(Loader<List<Image>> loader, List<Image> data) {
        mImageAdapter.clear();
        mPbLoadImages.setVisibility(View.GONE);
        for (Image img : data) {
            mImageAdapter.addImage(img);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Image>> loader) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver mDownloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //check if the broadcast message is for our Enqueued download
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action) &&
                    mDownloadReference == referenceId) {
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(mDownloadReference);
                Cursor c = mDownloadManager.query(query);
                if (c.moveToFirst()) {
                    int columnIndex = c
                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                    if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                        Toast toast = Toast.makeText(AlbumImagesActivity.this,
                                "Image downloaded successful", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();
                        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    } else {
                        Toast toast = Toast.makeText(AlbumImagesActivity.this,
                                "Download unsuccessful", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP, 25, 400);
                        toast.show();
                        System.out.println("Status from download when token invalid: " + c.getInt(columnIndex));
                    }
                }
            }
        }
    };


    //in case the activity is put in background and onCreate is called again
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("album_id", mAlbumId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAlbumId = savedInstanceState.getLong("album_id");
    }
}
