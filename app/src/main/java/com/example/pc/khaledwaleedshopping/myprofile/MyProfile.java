package com.example.pc.khaledwaleedshopping.myprofile;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pc.khaledwaleedshopping.LoginActivity;
import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeEditText;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.image.CircleTransform;
import com.example.pc.khaledwaleedshopping.Support.image.CompressImage;
import com.example.pc.khaledwaleedshopping.Support.image.UploadImage;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import in.myinnos.awesomeimagepicker.activities.AlbumSelectActivity;
import in.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery;

import static android.app.Activity.RESULT_OK;
import static com.example.pc.khaledwaleedshopping.MainActivity.fragmentHome;

/**
 * Created by pc on 10/28/2017.
 */


public class MyProfile extends Fragment {
    CustomeTextView username, change, emailError, phoneError, passwordError, nameError;
    CustomeButton update;
    CustomeEditText email, phone, password, about, firstName, lastName;
    ImageView logo;
    List<Uri> logoUri;
    private String logoLast = "";
    private String oldPassword = "";
    private String code = "$";
    private String verificationId = "";
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_profile, container, false);

        logoUri = new ArrayList<>();
        emailError = (CustomeTextView) view.findViewById(R.id.emailError);
        phoneError = (CustomeTextView) view.findViewById(R.id.phoneError);
        nameError = (CustomeTextView) view.findViewById(R.id.nameError);
        passwordError = (CustomeTextView) view.findViewById(R.id.passwordError);
        logo = (ImageView) view.findViewById(R.id.clientLogo);
        username = (CustomeTextView) view.findViewById(R.id.username);
        change = (CustomeTextView) view.findViewById(R.id.change);
        firstName = (CustomeEditText) view.findViewById(R.id.firstName);
        lastName = (CustomeEditText) view.findViewById(R.id.lastName);
        update = (CustomeButton) view.findViewById(R.id.update);
        email = (CustomeEditText) view.findViewById(R.id.email);
        phone = (CustomeEditText) view.findViewById(R.id.phone);
        password = (CustomeEditText) view.findViewById(R.id.password);
        about = (CustomeEditText) view.findViewById(R.id.about);

        if (!LoginActivity.loginKind.equals("normal")) {
            password.setVisibility(View.GONE);
            passwordError.setVisibility(View.GONE);
        }
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                    try {
                        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1);
                        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    Intent intent = new Intent(getActivity(), AlbumSelectActivity.class);
                    try {
                        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1);
                        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        setData();


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantsCustomGallery.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            //The array list has the image paths of the selected images
            ArrayList<in.myinnos.awesomeimagepicker.models.Image> images = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES);
            CompressImage compressImage = new CompressImage();
            for (int i = 0; i < images.size(); i++) {
                Uri uri = Uri.fromFile(compressImage.compress(new File(images.get(i).path)));
                logoUri.add(0, uri);
            }
            Picasso.with(getContext()).load(logoUri.get(0)).transform(new CircleTransform()).into(logo);

        }
    }

    private void setData() {
        try {
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        logoLast = jsonObject.getString("logo");
                        username.setText(jsonObject.getString("username"));
                        if (jsonObject.getString("name").length() > 0) {
                            if (!jsonObject.getString("name").contains(" "))
                                firstName.setText(jsonObject.getString("name"));
                            else {
                                firstName.setText(jsonObject.getString("name").substring(0, jsonObject.getString("name").indexOf(" ") - 1));
                                lastName.setText(jsonObject.getString("name").substring(jsonObject.getString("name").indexOf(" ") + 1));
                            }
                        }
                        password.setText(jsonObject.getString("password"));
                        email.setText(jsonObject.getString("email"));
                        phone.setText(jsonObject.getString("phone"));
                        about.setText(jsonObject.getString("about"));
                        if (phone.getText().length() > 0) {
                            oldPassword = password.getText().toString();
                            phone.setEnabled(false);
                            phone.setBackgroundColor(Color.TRANSPARENT);
                            email.setBackgroundColor(Color.TRANSPARENT);
                            email.setEnabled(false);
                        }
                        if (!jsonObject.getString("logo").equals(" "))
                            Picasso.with(getContext()).load(WebService.imageLink + jsonObject.getString("logo")).transform(new CircleTransform()).into(logo, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
                                    logo.setImageResource(R.drawable.profile);
                                }
                            });
                        else
                            logo.setImageResource(R.drawable.profile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkUpdateInfo();
                        }
                    });
                }
            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.userInfo, MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void checkUpdateInfo() {

        if (dataComplete())
            if (oldPassword.equals(password.getText().toString())) {
                updateNow();
            } else if (oldPassword.equals("")) {

                sendCode();
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("We have sent you an SMS!");

// Set up the input
                final EditText input = new EditText(getActivity());
                input.setHint("Please enter your verification code");
                input.setInputType(InputType.TYPE_CLASS_PHONE);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, input.getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("please enter old password");

// Set up the input
                final EditText input = new EditText(getActivity());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (input.getText().toString().equals(oldPassword)) {
                            oldPassword = password.getText().toString();
                            checkUpdateInfo();
                        } else {
                            checkUpdateInfo();
                            Toast.makeText(getActivity(), "Wrong password, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }

    }

    private void updateNow() {
        new UploadImage(new UploadImage.AsyncResponse() {
            @Override
            public void processFinish(boolean output) {
                if (output) {

                    UrlData urlData = new UrlData();
                    try {
                        urlData.add("id", MainActivity.jsonObjectUser.getString("id"));
                        urlData.add("username", username.getText().toString());
                        urlData.add("name", firstName.getText().toString() + " " + lastName);
                        urlData.add("password", password.getText().toString());
                        urlData.add("email", email.getText().toString());
                        if (logoUri.size() == 1)
                            urlData.add("logo", getFileName(logoUri.get(0)));
                        else
                            urlData.add("logo", logoLast);


                        urlData.add("phone", phone.getText().toString());
                        urlData.add("about", about.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String result) {

                            try {

                                try {
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    fragmentHome = new com.example.pc.khaledwaleedshopping.products.home.Home();
                                    fragmentHome.downScroll = -1;
                                    ft.replace(R.id.activity_main_content_fragment3, fragmentHome);
                                    ft.commit();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.updateClient, urlData.get());

                }
            }
        }, getActivity(), logoUri);
    }

    private boolean dataComplete() {
        boolean b = true;
        cleanError();
        if (LoginActivity.loginKind.equals("normal") && password.getText().length() < 6) {
            b = false;
            passwordError.setText("Please enter password more than 6 characters");
            passwordError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }
        if (email.length() != 0 && !(email.getText().toString().contains("@") && email.getText().toString().contains("."))) {
            b = false;
            emailError.setText("Please enter valid email address");
            emailError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        if (phone.getText().length() != 11) {
            b = false;
            phoneError.setText("Please enter valid phone");
            phoneError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        if (firstName.getText().length() == 0) {
            b = false;
            nameError.setText("Please enter your firstName");
            nameError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        } else if (lastName.getText().length() == 0) {
            b = false;
            nameError.setText("Please enter your lastName");
            nameError.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
        }

        return b;
    }

    private void cleanError() {
        passwordError.setText("password");
        passwordError.setTextColor(ContextCompat.getColor(getContext(), R.color.mainColor));


        nameError.setText("Your firstName");
        nameError.setTextColor(ContextCompat.getColor(getContext(), R.color.mainColor));

        emailError.setText("Email address");
        emailError.setTextColor(ContextCompat.getColor(getContext(), R.color.mainColor));

        phoneError.setText("phone");
        phoneError.setTextColor(ContextCompat.getColor(getContext(), R.color.mainColor));

    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDateandTime = sdf.format(new Date());

        if (!(result.endsWith("jpg") || result.endsWith("png") || result.endsWith("jpeg"))) {
            result += ".png";
        }

        return currentDateandTime + result;
    }

    private void sendCode() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + phone.getText().toString(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                getActivity(),               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Log.e("Code", "!" + phoneAuthCredential.getSmsCode());
                        code = (phoneAuthCredential.getSmsCode());
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        MyProfile.this.verificationId = verificationId;
                        Log.e("Token", token.toString());
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.e("Error SMS:", e.getMessage());
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SMS State", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            updateNow();

                            // ...
                        } else {
                            Toast.makeText(getActivity(), "Wrong code, please try again!", Toast.LENGTH_SHORT).show();
                            checkUpdateInfo();
                            // Sign in failed, display a message and update the UI
                            Log.w("SMS State", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }
}
