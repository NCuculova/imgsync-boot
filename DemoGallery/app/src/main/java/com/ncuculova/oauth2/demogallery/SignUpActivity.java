package com.ncuculova.oauth2.demogallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.Preferences;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Create new user if email is unique
 */
public class SignUpActivity extends AppCompatActivity {

    @Bind(R.id.pb_login)
    ProgressBar mPbLogin;

    @Bind(R.id.et_pass_confirm)
    EditText mEtPassConfirm;

    @Bind(R.id.et_password)
    EditText mEtPass;

    @Bind(R.id.et_email)
    EditText mEtEmail;

    DemoGalleryHttpClient mClient;
    EmailValidator mEmailValidator;
    Preferences mPreferences;
    final String TAG = "SignUpActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        mClient = DemoGalleryHttpClient.getInstance(this);
        mEmailValidator = EmailValidator.getInstance();
        mPreferences = Preferences.getInstance(this);
    }

    @OnClick(R.id.btn_sign_up)
    public void createUser() {
        mPbLogin.setVisibility(View.VISIBLE);
        String userName = mEtEmail.getText().toString();
        String password = mEtPass.getText().toString();
        String passwordConfirm = mEtPassConfirm.getText().toString();
        if (mEmailValidator.isValid(userName)) {
            if (password.equals(passwordConfirm)) {
                mClient.signUpUser(userName, password, new DemoGalleryHttpClient.ResponseHandler() {

                    @Override
                    public void onSuccessJsonObject(JSONObject jsonObject) {
                        super.onSuccessJsonObject(jsonObject);
                        String username = null;
                        String password = null;
                        try {
                            username = jsonObject.getString("email");
                            password = jsonObject.getString("password");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mPreferences.setUsername(username);
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
                    public void onFailureJsonObject(int statusCode, JSONObject jsonObject) {
                        if (statusCode == 409) {
                            mEtEmail.setError(getString(R.string.email_taken));
                            Log.e(TAG, "Conflict " + statusCode);
                        }
                        super.onFailureJsonObject(statusCode, jsonObject);
                    }
                });
            } else {
                mEtPassConfirm.setError(getString(R.string.password_error));
            }
        } else {
            mEtEmail.setError(getString(R.string.email_invalid));
        }
    }
}
