package com.ncuculova.oauth2.demogallery.util;

import android.content.Context;
import android.support.v4.content.Loader;

public class HttpLoader<T> extends Loader<T> {

    protected DemoGalleryHttpClient mHttpClient;
    private boolean mRunning;

    protected OnLoaderListener mOnLoaderListener;

    public HttpLoader(Context context) {
        super(context);
        mHttpClient = DemoGalleryHttpClient.getInstance(context);
        mRunning = false;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mOnLoaderListener != null) {
            mOnLoaderListener.onStartLoading();
        }
        forceLoad();
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
        mRunning = true;
    }

    @Override
    protected boolean onCancelLoad() {
        if (mRunning) {
            mHttpClient.cancelAllRequests();
            return true;
        }
        return false;
    }

    public interface OnLoaderListener {
        void onFailure(int statusCode, String errorMessage);

        void onStartLoading();
    }

    public void setOnLoaderListener(OnLoaderListener onLoaderListener) {
        mOnLoaderListener = onLoaderListener;
    }

}