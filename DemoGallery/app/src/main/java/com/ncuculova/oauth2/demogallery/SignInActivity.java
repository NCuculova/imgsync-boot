package com.ncuculova.oauth2.demogallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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

    @Bind(R.id.login_button)
    LoginButton mLoginFbBtn;

    DemoGalleryHttpClient mClient;
    Preferences mPreferences;
    final String TAG = "SignInActivity";
    CallbackManager mCallbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
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
        mCallbackManager = CallbackManager.Factory.create();
        mLoginFbBtn.setReadPermissions("email");
        mLoginFbBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String token = loginResult.getAccessToken().getToken();
                String userId = loginResult.getAccessToken().getUserId();
                mClient.signInWithFb(token, userId, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        String username = null;
                        String password = null;
                        try {
                            username = response.getString("email");
                            password = response.getString("password");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mClient.getAccessToken(username, password, new DemoGalleryHttpClient.ResponseHandler() {

                            @Override
                            public void onSuccessJsonObject(JSONObject jsonObject) {
                                super.onSuccessJsonObject(jsonObject);
                                mPbLogin.setVisibility(View.GONE);
                                try {
                                    mPreferences.setAccessToken(jsonObject.getString("access_token"));
                                    mPreferences.setRefreshToken(jsonObject.getString("refresh_token"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailureJsonObject(int statusCode, JSONObject jsonObject) {
                                mPbLogin.setVisibility(View.GONE);
                                Log.d(TAG, "Access token not obtained!");
                                super.onFailureJsonObject(statusCode, jsonObject);
                            }
                        });
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        System.out.println(errorResponse.toString());
                    }
                });
            }

            @Override
            public void onCancel() {
                System.out.println("CANCELED");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("ERROR");
            }
        });
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

   /* @OnClick(R.id.login_button)
    public void signInWithFacebook(){

    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
