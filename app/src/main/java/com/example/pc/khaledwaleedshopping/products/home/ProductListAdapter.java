package com.example.pc.khaledwaleedshopping.products.home;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.image.CircleTransform;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.clientprofile.ClientProfile;
import com.example.pc.khaledwaleedshopping.products.productprofile.ProductProfile;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by pc on 10/8/2017.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.MyViewHolder> {
    JSONArray jsonObjectsList;
    FragmentActivity activity;
    String kind;

    public ProductListAdapter(JSONArray jsonObjectsList, FragmentActivity activity, String kind) {
        this.activity = activity;
        this.kind = kind;
        this.jsonObjectsList = jsonObjectsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_list_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        try {

            holder.top.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    try {
                        bundle.putString("id", jsonObjectsList.getJSONObject(position).getString("id_client"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    FragmentManager fm = activity.getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ClientProfile fragment = new ClientProfile();
                    fragment.setArguments(bundle);
                    ft.replace(R.id.activity_main_content_fragment3, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
            holder.productImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    try {
                        bundle.putString("id", jsonObjectsList.getJSONObject(position).getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    FragmentManager fm = activity.getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ProductProfile fragment = new ProductProfile();
                    fragment.setArguments(bundle);
                    ft.replace(R.id.activity_main_content_fragment3, fragment);
                    ft.addToBackStack(null);
                    ft.commit();

                }
            });

            holder.price.setText(jsonObjectsList.getJSONObject(position).getString("price") + " L.E");
            holder.brand.setText(jsonObjectsList.getJSONObject(position).getString("brand"));
            holder.username.setText(jsonObjectsList.getJSONObject(position).getString("username"));
            if (jsonObjectsList.getJSONObject(position).getString("swap").equals("1")) {
                holder.swap.setVisibility(View.VISIBLE);
            } else holder.swap.setVisibility(View.GONE);
            holder.size.setText(jsonObjectsList.getJSONObject(position).getString("size"));

            if (holder.enter && jsonObjectsList.getJSONObject(position).getString("isLove").equals("false")) {
                holder.love.setTag("not love");
                holder.love.setImageResource(R.drawable.not_love);
                //      holder.enter = false;
            } else if (holder.enter && jsonObjectsList.getJSONObject(position).getString("isLove").equals("true")) {
                holder.love.setTag("love");
                //       holder.enter = false;
                holder.love.setImageResource(R.drawable.full_love);
            }
            holder.love.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        if (MainActivity.jsonObjectUser.getString("id").equals(jsonObjectsList.getJSONObject(position).getString("id_client"))) {
                            Toast.makeText(activity, "You can‘t mark your items as favorite", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UrlData urlData = new UrlData();
                    try {

                        urlData.add("id_client", MainActivity.jsonObjectUser.getString("id"));
                        urlData.add("id_product", jsonObjectsList.getJSONObject(position).getString("id"));
                        if (holder.love.getTag().toString().equals("love")) {
                            holder.love.setTag("not love");
                            urlData.add("state", "remove");
                            if (holder.count != 0)
                                holder.count--;
                            holder.love.setImageResource(R.drawable.not_love);
                        } else {
                            holder.love.setTag("love");
                            urlData.add("state", "add");
                            holder.count++;
                            Toast.makeText(activity, "added to your favorites", Toast.LENGTH_SHORT).show();
                            holder.love.setImageResource(R.drawable.full_love);
                        }
                        //       holder.love.setText((Integer.parseInt(jsonObjectsList.getJSONObject(position).getString("love")) + holder.count) + "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String result) {

                        }
                    }, activity, false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.updateLove, urlData.get());

                }
            });


            //  holder.love.setText((Integer.parseInt(jsonObjectsList.getJSONObject(position).getString("love")) + holder.count) + "");
            if (!jsonObjectsList.getJSONObject(position).getString("img1").equals("") &&
                    !jsonObjectsList.getJSONObject(position).getString("img1").contains("https:")) {


                Picasso.with(activity).load(WebService.imageLink + jsonObjectsList.getJSONObject(position).getString("img1")).fit().centerCrop().into(holder.productImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        try {
                            Log.e("Error:", "downloading Image:" + WebService.imageLink + jsonObjectsList.getJSONObject(position).getString("img1"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        holder.productImage.setImageResource(R.drawable.icon_no_logo);
                    }
                });
            } else {

                Log.e("Error:", "Image content");
                holder.productImage.setImageResource(R.drawable.icon_no_logo);
            }
            if (!jsonObjectsList.getJSONObject(position).getString("logo").equals("") &&
                    !jsonObjectsList.getJSONObject(position).getString("logo").contains("https:"))
                Picasso.with(activity).load(WebService.imageLink + jsonObjectsList.getJSONObject(position).getString("logo")).transform(new CircleTransform()).into(holder.userIcon, new Callback() {
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
        return jsonObjectsList.length();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        boolean enter = true;
        int count = 0;
        public ImageView userIcon, productImage, love, swap;
        CustomeTextView username, size, brand, price;
        LinearLayout top;

        public MyViewHolder(View v) {
            super(v);
            username = (CustomeTextView) v.findViewById(R.id.username);
            swap = (ImageView) v.findViewById(R.id.swap);
            size = (CustomeTextView) v.findViewById(R.id.size);
            brand = (CustomeTextView) v.findViewById(R.id.brand);
            top = (LinearLayout) v.findViewById(R.id.userInfo);
            if (kind.equals("profile_main") || kind.equals("Edit"))
                top.setVisibility(View.GONE);
            price = (CustomeTextView) v.findViewById(R.id.price);
            love = (ImageView) v.findViewById(R.id.love);
            userIcon = (ImageView) v.findViewById(R.id.usericon);
            productImage = (ImageView) v.findViewById(R.id.imgProduct);
        }


    }

}
