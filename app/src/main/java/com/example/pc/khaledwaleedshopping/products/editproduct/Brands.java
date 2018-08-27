package com.example.pc.khaledwaleedshopping.products.editproduct;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pc on 10/7/2017.
 */

public class Brands extends Dialog {
    private List<JSONObject> tempJsonArray = new ArrayList<>();
    private List<JSONObject> jsonArray = new ArrayList<>();
    private BrandsAdapter brandsAdapter;
    private RecyclerView brandsRV;
    FragmentActivity activity;
    ImageView back;
    SearchView searchView;

    public Brands(@NonNull final FragmentActivity activity) {
        super(activity, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        this.activity = activity;
        this.show();
        // this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.setContentView(R.layout.brands_list);
        back = (ImageView) this.findViewById(R.id.back);
        searchView = (SearchView) this.findViewById(R.id.searchView);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Brands.this.cancel();
            }
        });
        brandsRV = (RecyclerView) this.findViewById(R.id.category_list);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        brandsRV.setLayoutManager(layoutManager);

        try {

            for (int i = 0; i < EditProduct.jsonObject.getJSONArray("brands").length(); i++) {
                jsonArray.add(EditProduct.jsonObject.getJSONArray("brands").getJSONObject(i));
                tempJsonArray.add(EditProduct.jsonObject.getJSONArray("brands").getJSONObject(i));
            }
            brandsAdapter = new BrandsAdapter();
            brandsRV.setAdapter(brandsAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()

        {
            @Override
            public boolean onQueryTextSubmit(String text) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                try {
                    checkString(text);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }

            private void checkString(String text) throws JSONException {
                if (text.length() == 0) {
                    jsonArray = tempJsonArray;
                    Log.e("brands", " " + jsonArray.toString());
                } else {
                    jsonArray = new ArrayList<>();
                }
                brandsAdapter.notifyDataSetChanged();
                brandsRV.scrollToPosition(0);
                for (JSONObject brandObject : tempJsonArray) {
                    if (text.length() != 0 && brandObject.getString("name").toLowerCase().contains(text.toLowerCase())) {
                        jsonArray.add(brandObject);
                        brandsAdapter.notifyDataSetChanged();
                        brandsRV.scrollToPosition(0);
                    }
                }
            }
        });

    }


    private class BrandsAdapter extends RecyclerView.Adapter<MyViewHolder> {


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
                            EditProduct.brandId = jsonArray.get(position).getString("id");
                            EditProduct.brands.setText(jsonArray.get(position).getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        Brands.this.cancel();
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
