package com.ncuculova.oauth2.demogallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ncuculova.oauth2.demogallery.util.DemoGalleryHttpClient;
import com.ncuculova.oauth2.demogallery.util.Preferences;

import org.apache.commons.validator.routines.EmailValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

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
                mClient.signUpUser(userName, password, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            String username = response.getString("email");
                            String password = response.getString("password");
                            mClient.getAccessToken(username, password, new JsonHttpResponseHandler(){
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    mPbLogin.setVisibility(View.GONE);
                                    try {
                                        mPreferences.setAccessToken(response.getString("access_token"));
                                        mPreferences.setRefreshToken(response.getString("refresh_token"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    mPbLogin.setVisibility(View.GONE);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        if (statusCode == 409) {
                            mEtEmail.setError(getString(R.string.email_taken));
                        }
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
