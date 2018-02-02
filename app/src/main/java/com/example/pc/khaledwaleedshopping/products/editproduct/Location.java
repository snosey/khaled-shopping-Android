package com.example.pc.khaledwaleedshopping.products.editproduct;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 10/7/2017.
 */

public class Location extends Dialog {
    private RecyclerView categoryRV;
    FragmentActivity activity;
    CustomeTextView title;
    ImageView back;
    String kind;
    private String govId;

    public Location(@NonNull final FragmentActivity activity, final String kind, String govId) {
        super(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.activity = activity;
        this.govId = govId;
        this.show();
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.kind = kind;
        this.setContentView(R.layout.category_list);
        title = (CustomeTextView) this.findViewById(R.id.title);
        back = (ImageView) this.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (kind.equals("city")) {
                    new Location(activity, "gov", "-1");
                }
                Location.this.cancel();
            }
        });
        categoryRV = (RecyclerView) this.findViewById(R.id.category_list);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        categoryRV.setLayoutManager(layoutManager);
        if (kind.equals("gov")) {
            title.setText("Choose government");
            try {
                List<JSONObject> jsonObjects = new ArrayList<>();
                for (int i = 0; i < EditProduct.jsonObject.getJSONArray("government").length(); i++)
                    jsonObjects.add(EditProduct.jsonObject.getJSONArray("government").getJSONObject(i));
                categoryRV.setAdapter(new CategoryAdapter((jsonObjects)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (kind.equals("city")) {
            try {
                title.setText(EditProduct.govTitle);
                boolean find = false;
                List<JSONObject> jsonObjects = new ArrayList<>();
                for (int i = 0; i < EditProduct.jsonObject.getJSONObject("city").getJSONArray(govId).length(); i++) {
                    jsonObjects.add(EditProduct.jsonObject.getJSONObject("city").getJSONArray(govId).getJSONObject(i));
                    find = true;
                }

                if (find)
                    categoryRV.setAdapter(new CategoryAdapter(jsonObjects));
                else {
                    this.cancel();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class CategoryAdapter extends RecyclerView.Adapter<MyViewHolder> {
        List<JSONObject> jsonArray;

        public CategoryAdapter(List<JSONObject> jsonArray) {
            this.jsonArray = jsonArray;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spinner_text, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                holder.customeTextView.setText(jsonArray.get(position).getString("name"));
                holder.customeTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (kind.equals("gov")) {
                                EditProduct.govTitle = jsonArray.get(position).getString("name");
                                new Location(activity, "city", jsonArray.get(position).getString("id"));
                            } else if (kind.equals("city")) {
                                EditProduct.government_id = govId;
                                EditProduct.city_id = jsonArray.get(position).getString("id");
                                EditProduct.location.setText(jsonArray.get(position).getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Location.this.cancel();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonArray.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomeTextView customeTextView;

        public MyViewHolder(View v) {
            super(v);
            customeTextView = (CustomeTextView) v.findViewById(R.id.text);
        }


    }

}
