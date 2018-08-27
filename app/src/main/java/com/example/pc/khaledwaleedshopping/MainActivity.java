package com.example.pc.khaledwaleedshopping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.example.pc.khaledwaleedshopping.Support.notification.Config;
import com.example.pc.khaledwaleedshopping.Support.notification.NotificationUtils;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.clientprofile.ClientProfile;
import com.example.pc.khaledwaleedshopping.clientprofile.ClientReviews;
import com.example.pc.khaledwaleedshopping.masseges.Chat;
import com.example.pc.khaledwaleedshopping.masseges.Inbox;
import com.example.pc.khaledwaleedshopping.myprofile.Favourites;
import com.example.pc.khaledwaleedshopping.myprofile.MyProfile;
import com.example.pc.khaledwaleedshopping.products.addproduct.AddProduct;
import com.example.pc.khaledwaleedshopping.products.home.Home;
import com.example.pc.khaledwaleedshopping.products.productprofile.ProductComments;
import com.example.pc.khaledwaleedshopping.products.productprofile.ProductProfile;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.pc.khaledwaleedshopping.products.home.Home.searchView;

public class MainActivity extends FragmentActivity {
    public static JSONObject jsonObjectUser = null;
    public static Home fragmentHome;
    AddProduct fragmentAddProduct;
    Inbox fragmentMsg;
    public static boolean chatOn = false;
    com.example.pc.khaledwaleedshopping.myprofile.Home fragmentProfile;
    Favourites favorites;

    private static final String TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static boolean closeApp = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        try {

            {
                SharedPreferences sharedPreferences = getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                try {
                    JSONObject jsonObject = new JSONObject(intent.getStringExtra("userData"));
                    jsonObjectUser = jsonObject;
                    editor.putString("userData", intent.getStringExtra("userData"));
                    editor.commit();
                } catch (Exception e) {
                    JSONObject jsonObject = new JSONObject(sharedPreferences.getString("userData", ""));
                    jsonObjectUser = jsonObject;
                }

                if (intent.hasExtra("data")) {
                    notificationAction(intent.getStringExtra("data"));
                } else {
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    fragmentHome = new Home();
                    ft.replace(R.id.activity_main_content_fragment3, fragmentHome);
                    ft.commit();
                }
            }
            if (jsonObjectUser.getString("name").equals("")) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MyProfile myProfile = new MyProfile();
                ft.replace(R.id.activity_main_content_fragment3, myProfile);
                ft.commit();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        updateRegId();
    }

    private void notificationAction(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            if (jsonObject.getString("kind").equals("comment")) {
                Bundle bundle = new Bundle();
                bundle.putString("productId", jsonObject.getString("data"));
                ProductComments comment = new ProductComments();
                comment.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.activity_main_content_fragment3, comment);
                ft.commit();
            } else if (jsonObject.getString("kind").equals("Love")) {
                Bundle bundle = new Bundle();
                bundle.putString("id", jsonObject.getString("data"));
                ProductProfile productprofile = new ProductProfile();
                productprofile.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.activity_main_content_fragment3, productprofile);
                ft.commit();
            } else if (jsonObject.getString("kind").equals("message")) {
                Bundle bundle = new Bundle();
                String dataString = jsonObject.getString("data");
                bundle.putString("id", dataString.substring(0, dataString.indexOf("|")));
                bundle.putString("id_product", dataString.substring(dataString.indexOf("|") + 1));
                Chat chat = new Chat();
                chat.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.activity_main_content_fragment3, chat);
                ft.commit();
            } else if (jsonObject.getString("kind").equals("Review")) {
                Bundle bundle = new Bundle();
                bundle.putString("id", jsonObject.getString("data"));
                ClientReviews clientReviews = new ClientReviews();
                clientReviews.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.activity_main_content_fragment3, clientReviews);
                ft.commit();
            } else if (jsonObject.getString("kind").equals("follow")) {
                Bundle bundle = new Bundle();
                bundle.putString("id", jsonObject.getString("data"));
                ClientProfile clientProfile = new ClientProfile();
                clientProfile.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.activity_main_content_fragment3, clientProfile);
                ft.commit();
            }
            // take action for notification
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateRegId() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    sentRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    final String data = intent.getStringExtra("data");

                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(MainActivity.this);
                    } else {
                        builder = new AlertDialog.Builder(MainActivity.this);
                    }

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(data);
                        if (jsonObject.getString("kind").equals("message") && chatOn)
                            notificationAction(data);
                      /*  else
                            builder.setTitle(intent.getStringExtra("title")).setMessage(intent.getStringExtra("message"))
                                    .setPositiveButton("Check it", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            notificationAction(data);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    })
                                    .show();
                        */
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        };

        sentRegId();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void sentRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId)) {
            UrlData urlData = new UrlData();
            try {
                urlData.add("id_client", jsonObjectUser.getString("id"));
                urlData.add("reg_id", regId);
                new GetData(new GetData.AsyncResponse() {
                    @Override
                    public void processFinish(String output) {
                    }
                }, this, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.updateRegId, urlData.get());

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else
            Log.e("Error", "Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    public void home(View view) {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.activity_main_content_fragment3, fragmentHome);
        ft.commit();
    }

    public void addProduct(View view) throws JSONException {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragmentAddProduct = new AddProduct();
        ft.replace(R.id.activity_main_content_fragment3, fragmentAddProduct);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void favorites(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        favorites = new Favourites();
        ft.replace(R.id.activity_main_content_fragment3, favorites);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void profile(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragmentProfile = new com.example.pc.khaledwaleedshopping.myprofile.Home();
        ft.replace(R.id.activity_main_content_fragment3, fragmentProfile);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void search(View view) {
        searchView.showSearch();
    }

    public void msg(View view) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        fragmentMsg = new Inbox();
        ft.replace(R.id.activity_main_content_fragment3, fragmentMsg);
        ft.addToBackStack(null);
        ft.commit();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void back(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (Home.searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            try {
                super.onBackPressed();
            } catch (Exception e) {
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }
    }

    @Override
    public void onDestroy() {
        if (closeApp)
            Process.killProcess(Process.myPid());
        super.onDestroy();
    }
}
