package com.example.pc.khaledwaleedshopping.clientprofile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.image.CircleTransform;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by ahmed on 6/26/2017.
 */

public class FollowList extends Fragment {
    RecyclerView followList;
    String userId;
    String followUrl;
    String clientId;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follow, container, false);

        Bundle bundle = getArguments();
        clientId = bundle.getString("id");
        userId = bundle.getString("user_id");
        followUrl = bundle.getString("link");

        CustomeTextView title = (CustomeTextView) view.findViewById(R.id.title);
        title.setText(bundle.getString("title"));

        followList = (RecyclerView) view.findViewById(R.id.followList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        followList.setLayoutManager(layoutManager);

        getFollow();

        return view;
    }

    private void getFollow() {
        UrlData urlData = new UrlData();
        urlData.add("id", clientId);
        urlData.add("id_follower", userId);
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result != null)
                    followList.setAdapter(new commentAdapter(result));

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, followUrl, urlData.get());

    }


    private class commentAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private JSONArray List;

        private commentAdapter(String result) {
            try {
                List = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.follow_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            try {

                final JSONObject followRow = List.getJSONObject(position);
                holder.clientName.setText(followRow.getString("name"));
                holder.followers.setText(followRow.getString("followers"));

                if (followRow.getString("isFollow").equals("false")) {

                    holder.followButton.setTag("not follow");
                    holder.followButton.setText("Follow");
                } else {
                    holder.followButton.setTag("follow");
                    holder.followButton.setText("Following");
                }
                holder.followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UrlData urlDataFollow = new UrlData();
                        try {

                            urlDataFollow.add("id_follower", MainActivity.jsonObjectUser.getString("id"));
                            urlDataFollow.add("id_client", followRow.getString("id"));
                            if (holder.followButton.getTag().toString().equals("follow")) {
                                holder.followButton.setTag("not follow");
                                urlDataFollow.add("state", "remove");

                                holder.followers.setText((Integer.parseInt(followRow.getString("followers"))) + "");
                                holder.followButton.setText("Follow");
                            } else {
                                holder.followButton.setTag("follow");
                                urlDataFollow.add("state", "add");

                                holder.followers.setText((Integer.parseInt(followRow.getString("followers"))) + "");
                                Toast.makeText(getActivity(), "You are now follow " + holder.clientName.getText().toString(), Toast.LENGTH_SHORT).show();
                                holder.followButton.setText("Following");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new GetData(new GetData.AsyncResponse() {
                            @Override
                            public void processFinish(String result) {

                            }
                        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.updateFollow, urlDataFollow.get());

                    }
                });


                holder.userIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("id", followRow.getString("id"));
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
                //logo
                if (!followRow.getString("logo").equals("") &&
                        !followRow.getString("logo").contains("https:"))
                    Picasso.with(getActivity()).load(WebService.imageLink + followRow.getString("logo")).transform(new CircleTransform()).into(holder.userIcon, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            holder.userIcon.setImageResource(R.drawable.profile);
                        }
                    });
                else
                    holder.userIcon.setImageResource(R.drawable.profile);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) List.length();
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomeTextView clientName, followers;
        public ImageView userIcon;
        public CustomeButton followButton;

        public MyViewHolder(View v) {
            super(v);

            clientName = (CustomeTextView) v.findViewById(R.id.name);
            followers = (CustomeTextView) v.findViewById(R.id.followers);
            followButton = (CustomeButton) v.findViewById(R.id.followButton);
            userIcon = (ImageView) v.findViewById(R.id.logo);

        }


    }

}
