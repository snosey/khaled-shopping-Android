package com.example.pc.khaledwaleedshopping.myprofile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pc.khaledwaleedshopping.LoginActivity;
import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.clientprofile.ClientProfile;

import org.json.JSONException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by pc on 10/20/2017.
 */

public class Home extends Fragment {


    CustomeTextView favor, myItems, myProfile, logout, inviteFriends, contactUs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_main, container, false);

        CustomeTextView msgNumber;
        msgNumber = (CustomeTextView) view.findViewById(R.id.msgNumber);
        try {
            if (!MainActivity.jsonObjectUser.getString("countUnSeen").equals("0")) {
                msgNumber.setVisibility(View.VISIBLE);
                msgNumber.setTextColor(Color.WHITE);
                msgNumber.setText(MainActivity.jsonObjectUser.getString("countUnSeen"));
            } else
                msgNumber.setVisibility(View.INVISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        favor = (CustomeTextView) view.findViewById(R.id.favor);
        contactUs = (CustomeTextView) view.findViewById(R.id.contactUs);
        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@virclo.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                email.putExtra(Intent.EXTRA_TEXT, "message");
                email.setType("message/rfc822");
                startActivity(Intent.createChooser(email, "Get in touch"));
            }
        });
        inviteFriends = (CustomeTextView) view.findViewById(R.id.inviteFriends);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "Download Virclo to sell, buy and swap ... http://www.example.com";
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Virclo");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Invite Friends"));
            }
        });
        favor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Favourites favourites = new Favourites();
                ft.addToBackStack(null);
                ft.replace(R.id.activity_main_content_fragment3, favourites);
                ft.commit();
            }
        });


        logout = (CustomeTextView) view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.closeApp = false;
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("login", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                getActivity().finish();
                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        myItems = (CustomeTextView) view.findViewById(R.id.myItems);
        myItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MyItems favourites = new MyItems();
                ft.addToBackStack(null);
                ft.replace(R.id.activity_main_content_fragment3, favourites);
                ft.commit();
            }
        });

        myProfile = (CustomeTextView) view.findViewById(R.id.myProfile);
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/*
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MyProfile favourites = new MyProfile();
                ft.addToBackStack(null);
                ft.replace(R.id.activity_main_content_fragment3, favourites);
                ft.commit();*/
                Bundle bundle = new Bundle();
                try {
                    bundle.putString("id", MainActivity.jsonObjectUser.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ClientProfile fragment = new ClientProfile();
                fragment.setArguments(bundle);
                ft.replace(R.id.activity_main_content_fragment3, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        return view;
    }
}
