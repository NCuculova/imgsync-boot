package com.ncuculova.oauth2.demogallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.Preferences;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;

/**
 * First step of providing resource owner credentials for an access token
 */
public class SignInActivity extends AppCompatActivity {

    @Bind(R.id.pb_login)
    ProgressBar mPbLogin;

    @Bind(R.id.et_password)
    EditText mEtPassword;

    @Bind(R.id.et_email)
    EditText mEtEmail;

    @Bind(R.id.tv_invalid_credentials)
    TextView mTvInvalid;

    DemoGalleryHttpClient mClient;
    Preferences mPreferences;
    final String TAG = "SignInActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        mClient = DemoGalleryHttpClient.getInstance(this);
        mPreferences = Preferences.getInstance(this);
        if (mPreferences.hasAccessToken()) {
            Log.d(TAG, "User is authorized");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.btn_sign_in)
    public void signIn() {
        mPbLogin.setVisibility(View.VISIBLE);
        final String username = mEtEmail.getText().toString();
        final String password = mEtPassword.getText().toString();
        mClient.getAccessToken(username, password, new DemoGalleryHttpClient.ResponseHandler() {
            @Override
            public void onSuccessJsonObject(JSONObject jsonObject) {
                super.onSuccessJsonObject(jsonObject);
                try {
                    mPbLogin.setVisibility(View.GONE);
                    mPreferences.setAccessToken(jsonObject.getString("access_token"));
                    mPreferences.setRefreshToken(jsonObject.getString("refresh_token"));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailureJsonObject(int statusCode, JSONObject jsonObject) {
                mPbLogin.setVisibility(View.GONE);
                mTvInvalid.setVisibility(View.VISIBLE);
                Log.d(TAG, "Invalid credentials");
                super.onFailureJsonObject(statusCode, jsonObject);
            }
        });
    }

    @OnClick(R.id.btn_sign_up)
    public void signUp() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}
