package com.example.pc.khaledwaleedshopping.clientprofile;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.masseges.Chat;
import com.example.pc.khaledwaleedshopping.myprofile.MyProfile;
import com.example.pc.khaledwaleedshopping.products.home.ProductListAdapter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 10/30/2017.
 */

public class ClientProfile extends Fragment {

    CustomeTextView title, followers, followersText, following, followingText, clientReviews, description;
    RecyclerView productRV;
    CustomeButton followButton;
    ImageView call, logo, msg;
    //CustomeButton msg;
    JSONArray clientProducts;
    String clientId;
    int counter = 0;
    ProductListAdapter productListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.client_profile, container, false);


        title = (CustomeTextView) view.findViewById(R.id.title);
        followers = (CustomeTextView) view.findViewById(R.id.followers);
        following = (CustomeTextView) view.findViewById(R.id.following);
        followersText = (CustomeTextView) view.findViewById(R.id.followersText);
        followingText = (CustomeTextView) view.findViewById(R.id.followingText);
        clientReviews = (CustomeTextView) view.findViewById(R.id.clientReviews);
        description = (CustomeTextView) view.findViewById(R.id.description);
        productRV = (RecyclerView) view.findViewById(R.id.productList);
        followButton = (CustomeButton) view.findViewById(R.id.followButton);
        call = (ImageView) view.findViewById(R.id.call);
        msg = (ImageView) view.findViewById(R.id.msg);
        logo = (ImageView) view.findViewById(R.id.logo);


        productRV.setNestedScrollingEnabled(false);


        Bundle bundle = getArguments();
        clientId = bundle.getString("id");

        final GridLayoutManager mLayoutManager;
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 180);
        mLayoutManager = new GridLayoutManager(getActivity(), noOfColumns);
        productRV.setLayoutManager(mLayoutManager);
        clientProducts = new JSONArray();
        productListAdapter = new ProductListAdapter(clientProducts, getActivity(), "profile_main");
        productRV.setAdapter(productListAdapter);

        loadingProducts();
        setData();

        return view;
    }

    private void setData() {

        try {
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        final JSONObject clientDetails = new JSONObject(result);

                        description.setText(clientDetails.getString("about"));

                        msg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id", clientId);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                Chat chat = new Chat();
                                chat.setArguments(bundle);
                                ft.addToBackStack(null);
                                ft.replace(R.id.activity_main_content_fragment3, chat);
                                ft.commit();
                            }
                        });

                        followers.setText((Integer.parseInt(clientDetails.getString("followers")) + counter) + "");
                        following.setText(clientDetails.getString("follow"));
                        following.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id", clientId);
                                bundle.putString("title", "following");
                                bundle.putString("link", WebService.getFollowing);
                                try {
                                    bundle.putString("user_id", MainActivity.jsonObjectUser.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                bundle.putString("id", clientId);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                FollowList followList = new FollowList();
                                followList.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, followList);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
                        followingText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id", clientId);
                                bundle.putString("title", "following");
                                bundle.putString("link", WebService.getFollowing);
                                try {
                                    bundle.putString("user_id", MainActivity.jsonObjectUser.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                bundle.putString("id", clientId);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                FollowList followList = new FollowList();
                                followList.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, followList);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });

                        followers.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id", clientId);
                                bundle.putString("title", "followers");
                                bundle.putString("link", WebService.getFollowers);
                                try {
                                    bundle.putString("user_id", MainActivity.jsonObjectUser.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                bundle.putString("id", clientId);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                FollowList followList = new FollowList();
                                followList.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, followList);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
                        followersText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id", clientId);
                                bundle.putString("title", "followers");
                                bundle.putString("link", WebService.getFollowers);
                                try {
                                    bundle.putString("user_id", MainActivity.jsonObjectUser.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                bundle.putString("id", clientId);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                FollowList followList = new FollowList();
                                followList.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, followList);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });

                        if (clientDetails.getString("isFollow").equals("false")) {
                            followButton.setTag("not follow");
                            followButton.setText("Follow");
                            //      followButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.grey), android.graphics.PorterDuff.Mode.MULTIPLY);
                        } else {
                            followButton.setTag("Unfollow");
                            //   followButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.MULTIPLY);
                        }
                        followButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UrlData urlDataFollow = new UrlData();
                                try {

                                    urlDataFollow.add("id_follower", MainActivity.jsonObjectUser.getString("id"));
                                    urlDataFollow.add("id_client", clientId);
                                    if (followButton.getTag().toString().equals("follow")) {
                                        followButton.setTag("not follow");
                                        urlDataFollow.add("state", "remove");
                                        counter--;
                                        followers.setText((Integer.parseInt(clientDetails.getString("followers")) + counter) + "");
                                        followButton.setText("Follow");
                                    } else {
                                        followButton.setTag("follow");
                                        urlDataFollow.add("state", "add");
                                        counter++;
                                        followers.setText((Integer.parseInt(clientDetails.getString("followers")) + counter) + "");
                                        Toast.makeText(getActivity(), "You are now follow " + title.getText().toString(), Toast.LENGTH_SHORT).show();
                                        followButton.setText("Unfollow");
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


                        if (clientId.equals(MainActivity.jsonObjectUser.getString("id"))) {
                            followButton.setText("Edit Profile");
                            followButton.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                            followButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    MyProfile favourites = new MyProfile();
                                    ft.addToBackStack(null);
                                    ft.replace(R.id.activity_main_content_fragment3, favourites);
                                    ft.commit();
                                }
                            });
                        }
                        title.setText(clientDetails.getString("name"));

                        if (clientDetails.getString("review").equals("0"))
                            clientReviews.setText("No reviews yet");
                        else {
                            int stars = (int) Math.round(Double.parseDouble(clientDetails.getString("stars")));

                            clientReviews.setText(clientDetails.getString("review"));
                            if (stars == 1)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star1, 0, R.drawable.edit_user, 0);
                            else if (stars == 2)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star2, 0, R.drawable.edit_user, 0);
                            else if (stars == 3)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star3, 0, R.drawable.edit_user, 0);
                            else if (stars == 4)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star4, 0, R.drawable.edit_user, 0);
                            else if (stars == 5)
                                clientReviews.setCompoundDrawablesWithIntrinsicBounds(R.drawable.star5, 0, R.drawable.edit_user, 0);

                        }

                        clientReviews.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                bundle.putString("id", clientId);
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ClientReviews clientReviews1 = new ClientReviews();
                                clientReviews1.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, clientReviews1);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
/*                        call.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                try {
                                    intent.setData(Uri.parse("tel:" + clientDetails.getString("phone")));
                                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                        String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};
                                        if (!MainActivity.hasPermissions(getActivity(), PERMISSIONS))
                                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
                                        return;
                                    }
                                    startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });

*/
                        //logo
                        if (!clientDetails.getString("logo").equals("") &&
                                !clientDetails.getString("logo").contains("https:"))
                            Picasso.with(getActivity()).load(WebService.imageLink + clientDetails.getString("logo")).fit().centerCrop().into(logo, new Callback() {
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

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.clientData + "id=" + clientId, "&id_follower=" + MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadingProducts() {
        try {
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int postion = 0; postion < jsonArray.length(); postion++)
                            clientProducts.put(jsonArray.getJSONObject(postion));
                        productListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.clientProducts + clientId + "&limit=0", "&user_id=" + MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
