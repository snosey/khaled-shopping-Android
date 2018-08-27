package com.example.pc.khaledwaleedshopping;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeDialogMassege;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeEditText;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetHashKey;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by pc on 10/1/2017.
 */

public class LoginActivity extends FragmentActivity {
    CustomeButton facebook, google, signin, signup;

    public GoogleApiClient mGoogleApiClient;
    int GOOGLE_SIGN_IN = 1000, FACEBOOK_SIGN_IN = 1001;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String username, password;
    public static String loginKind;
    private CallbackManager callbackManager;

    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        new GetHashKey(this);
        facebook = (CustomeButton) findViewById(R.id.facebook);
        google = (CustomeButton) findViewById(R.id.google);
        signin = (CustomeButton) findViewById(R.id.signin);
        signup = (CustomeButton) findViewById(R.id.signup);

        background = (ImageView) findViewById(R.id.background);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                google();
            }

        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }

        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signin();
            }

        });
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebook();
            }

        });

        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        username = sharedPreferences.getString("username", "");
        password = sharedPreferences.getString("password", "");
        loginKind = sharedPreferences.getString("kind", "nothing");

        if (!username.equals("")) {
            background.setVisibility(View.VISIBLE);
            CheckLogin(username, password, "", loginKind);
        }
    }

    private void signup() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_signup);
        CustomeButton signup = (CustomeButton) dialog.findViewById(R.id.signup);
        final CustomeEditText username = (CustomeEditText) dialog.findViewById(R.id.username);
        final CustomeEditText email = (CustomeEditText) dialog.findViewById(R.id.email);
        final CustomeEditText password = (CustomeEditText) dialog.findViewById(R.id.password);
        final CustomeTextView usernameError = (CustomeTextView) dialog.findViewById(R.id.usernameError);
        final CustomeTextView emailError = (CustomeTextView) dialog.findViewById(R.id.emailError);
        final CustomeTextView passwordError = (CustomeTextView) dialog.findViewById(R.id.passwordError);
        final boolean[] checkData = {true};
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkData[0] = true;
                usernameError.setVisibility(View.GONE);
                if (username.getText().toString().equals("")) {
                    usernameError.setVisibility(View.VISIBLE);
                    usernameError.setText("Please enter the username!");
                    checkData[0] = false;
                }
                emailError.setVisibility(View.GONE);
                if (!email.getText().toString().contains(".") || !email.getText().toString().contains("@")) {
                    emailError.setVisibility(View.VISIBLE);
                    emailError.setText("Please enter valid email address!");
                    checkData[0] = false;
                }
                passwordError.setVisibility(View.GONE);
                if (password.getText().toString().length() < 6) {
                    passwordError.setVisibility(View.VISIBLE);
                    passwordError.setText("Please enter password 6 character or more!");
                    checkData[0] = false;
                }
                if (checkData[0])
                    sendDataForSignup(username.getText().toString(), email.getText().toString(), password.getText().toString(), "normal");
            }
        });

        try {
            dialog.show();
        } catch (Exception e) {

        }
    }

    private void signin() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_signin);
        CustomeButton signin = (CustomeButton) dialog.findViewById(R.id.signin);
        CustomeButton send = (CustomeButton) dialog.findViewById(R.id.send);
        final CustomeEditText username = (CustomeEditText) dialog.findViewById(R.id.username);
        final CustomeEditText email = (CustomeEditText) dialog.findViewById(R.id.email);
        final CustomeEditText password = (CustomeEditText) dialog.findViewById(R.id.password);
        final LinearLayout forgetLayout = (LinearLayout) dialog.findViewById(R.id.forget_linear);
        CustomeTextView forgetText = (CustomeTextView) dialog.findViewById(R.id.forget);
        try {
            dialog.show();
        } catch (Exception e) {

        }


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().equals("") || password.getText().toString().equals(""))
                    Toast.makeText(LoginActivity.this, "Please enter username and password!", Toast.LENGTH_SHORT).show();
                else
                    CheckLogin(username.getText().toString(), password.getText().toString(), "", "normal");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().contains("@") && email.getText().toString().contains(".")) {
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String output) {
                            try {
                                JSONObject jsonObject = new JSONObject(output);
                                if (jsonObject.getString("sucess").equals("true")) {
                                    Toast.makeText(LoginActivity.this, "username and password is sent to your email address!", Toast.LENGTH_LONG).show();
                                } else
                                    new CustomeDialogMassege(LoginActivity.this, "Email address  is not exist!");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, LoginActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.forgetPassword, email.getText().toString());

                } else {
                    Toast.makeText(LoginActivity.this, "Please enter valid email address ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        forgetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetLayout.setVisibility(View.VISIBLE);

            }
        });
    }

    private void google() {
        try {

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                            Log.e("Connection Result", connectionResult.toString());
                            Toast.makeText(LoginActivity.this, "Failed to Sign in", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Please try again..", Toast.LENGTH_SHORT).show();
        }
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.e("handleSignInResult:", "" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acc = result.getSignInAccount();
            CheckLogin(acc.getId(), "", "google", "google");
        } else {
            Toast.makeText(LoginActivity.this, "Failed to Sign in", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        try {
            if (callbackManager.onActivityResult(requestCode, resultCode, data))
                return;
        } catch (Exception e) {

        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GOOGLE_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void facebook() {

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Profile profile = Profile.getCurrentProfile();
                        CheckLogin(profile.getId(), "", "facebook", "facebook");

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "You canceled loggin with facebook!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends"));

    }

    private void sendDataForSignup(final String username, final String email, final String password, final String kind) {
        UrlData urlData = new UrlData();
        urlData.add("username", username);
        urlData.add("email", email);
        urlData.add("password", password);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (jsonObject.getString("sucess").equals("true")) {
                        CheckLogin(username, password, email, kind);
                    } else
                        new CustomeDialogMassege(LoginActivity.this, "Username or Email does already exist!");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, LoginActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.signUp, urlData.get());

    }

    private void StartMainActivity(JSONObject jsonObject) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("userData", jsonObject.toString());
        startActivity(intent);
        finish();
    }

    private void CheckLogin(final String username, final String password, final String email, final String kind) {
        UrlData urlData = new UrlData();
        urlData.add("username", username);
        urlData.add("password", password);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String output) {
                try {
                    JSONObject jsonObject = new JSONObject(output);
                    if (jsonObject.getString("sucess").equals("true")) {
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.putString("kind", kind);
                        loginKind = kind;
                        editor.commit();
                        StartMainActivity(jsonObject);
                    } else if (kind.equals("normal")) {
                        background.setVisibility(View.GONE);
                        new CustomeDialogMassege(LoginActivity.this, "Username or password is not valid!");
                    }
                    else {
                        background.setVisibility(View.GONE);
                        sendDataForSignup(username, email, password, kind);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, LoginActivity.this, true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.login, urlData.get());

    }


}
