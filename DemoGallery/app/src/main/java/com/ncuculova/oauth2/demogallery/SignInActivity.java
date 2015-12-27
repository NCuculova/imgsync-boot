package com.ncuculova.oauth2.demogallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
 * Created by ncuculova on 7.12.15.
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        mClient = DemoGalleryHttpClient.getInstance(this);
        mPreferences = Preferences.getInstance(this);
        if(mPreferences.isSigned()){
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
        mClient.getAccessToken(username, password, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    mPbLogin.setVisibility(View.GONE);
                    mPreferences.setAccessToken(response.getString("access_token"));
                    mPreferences.setRefreshToken(response.getString("refresh_token"));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                mPbLogin.setVisibility(View.GONE);
                mTvInvalid.setVisibility(View.VISIBLE);
            }
        });
    }

    @OnClick(R.id.btn_sign_up)
    public void signUp() {
        Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
        startActivity(intent);
    }
}
