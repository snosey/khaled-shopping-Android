package com.example.pc.khaledwaleedshopping.products.addproduct;

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

import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.pc.khaledwaleedshopping.products.addproduct.AddProduct.category1Title;
import static com.example.pc.khaledwaleedshopping.products.addproduct.AddProduct.category2Title;
import static com.example.pc.khaledwaleedshopping.products.addproduct.AddProduct.category2_id;
import static com.example.pc.khaledwaleedshopping.products.addproduct.AddProduct.category3_id;
import static com.example.pc.khaledwaleedshopping.products.addproduct.AddProduct.categoryTitle;
import static com.example.pc.khaledwaleedshopping.products.addproduct.AddProduct.jsonObject;
import static com.example.pc.khaledwaleedshopping.products.addproduct.AddProduct.textSize;

/**
 * Created by pc on 10/7/2017.
 */

public class Category extends Dialog {
    private final int sizeCountInDB = 9;
    private RecyclerView categoryRV;
    FragmentActivity activity;
    CustomeTextView title;
    ImageView back;

    public Category(@NonNull final FragmentActivity activity) {
        super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.activity = activity;
        this.show();
        // this.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        this.setContentView(R.layout.category_list);
        title = (CustomeTextView) this.findViewById(R.id.title);
        back = (ImageView) this.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AddProduct.categoryNumber >= 1) {
                    categoryTitle.setText("You didn't choose category yet.");
                    AddProduct.categoryNumber--;
                    new Category(activity);
                }
                Category.this.cancel();
            }
        });
        categoryRV = (RecyclerView) this.findViewById(R.id.category_list);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
        categoryRV.setLayoutManager(layoutManager);
        if (AddProduct.categoryNumber == 0) {
            title.setText("Choose category");
            try {

                category2_id = "11";
                List<JSONObject> jsonObjects = new ArrayList<>();
                for (int i = 0; i < AddProduct.jsonObject.getJSONArray("catregory1").length(); i++)
                    jsonObjects.add(AddProduct.jsonObject.getJSONArray("catregory1").getJSONObject(i));
                categoryRV.setAdapter(new CategoryAdapter((jsonObjects)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (AddProduct.categoryNumber == 1) {
            try {

                if (AddProduct.category1_id.equals("12") || AddProduct.category1_id.equals("11") || AddProduct.category1_id.equals("10")) {
                    AddProduct.size.setVisibility(View.GONE);
                    textSize.setVisibility(View.GONE);
                    AddProduct.size.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("size"), "name"));
                } else if (AddProduct.category1_id.equals("9")) {
                    AddProduct.size.setVisibility(View.VISIBLE);
                    textSize.setVisibility(View.VISIBLE);
                    JSONArray jsonArray = new JSONArray();
                    for (int i = sizeCountInDB; i < jsonObject.getJSONArray("size").length(); i++)
                        jsonArray.put(jsonObject.getJSONArray("size").get(i));
                    AddProduct.size.setAdapter(new CustomeAdapter(getContext(), jsonArray, "name"));
                } else {
                    AddProduct.size.setVisibility(View.VISIBLE);
                    textSize.setVisibility(View.VISIBLE);
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < sizeCountInDB; i++)
                        jsonArray.put(jsonObject.getJSONArray("size").get(i));
                    AddProduct.size.setAdapter(new CustomeAdapter(getContext(), jsonArray, "name"));
                }
                category3_id = "2";
                title.setText(category1Title);
                boolean find = false;
                List<JSONObject> jsonObjects = new ArrayList<>();
                for (int i = 0; i < jsonObject.getJSONArray("catregory2").length(); i++)
                    if (jsonObject.getJSONArray("catregory2").getJSONObject(i).getString("id_category1").equals(AddProduct.category1_id)) {
                        jsonObjects.add(jsonObject.getJSONArray("catregory2").getJSONObject(i));
                        find = true;
                    }
                if (find)
                    categoryRV.setAdapter(new CategoryAdapter(jsonObjects));
                else {

                    category3_id = "2";
                    category2_id = "11";
                    this.cancel();
                    AddProduct.categoryNumber--;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (AddProduct.categoryNumber == 2) {
            try {

                if (AddProduct.category2_id.equals("95")) {
                    AddProduct.size.setVisibility(View.VISIBLE);
                    textSize.setVisibility(View.VISIBLE);
                    JSONArray jsonArray = new JSONArray();
                    for (int i = sizeCountInDB; i < jsonObject.getJSONArray("size").length(); i++)
                        jsonArray.put(jsonObject.getJSONArray("size").get(i));
                    AddProduct.size.setAdapter(new CustomeAdapter(getContext(), jsonArray, "name"));
                }
                if (AddProduct.category2_id.equals("105") || AddProduct.category2_id.equals("106") || AddProduct.category2_id.equals("100")) {
                    AddProduct.size.setVisibility(View.GONE);
                    textSize.setVisibility(View.GONE);
                    AddProduct.size.setAdapter(new CustomeAdapter(getContext(), jsonObject.getJSONArray("size"), "name"));
                }
                title.setText(category2Title);
                Log.e("id", AddProduct.category2_id);
                boolean find = false;
                List<JSONObject> jsonObjects = new ArrayList<>();
                for (int i = 0; i < jsonObject.getJSONArray("catregory3").length(); i++)
                    if (jsonObject.getJSONArray("catregory3").getJSONObject(i).getString("id_category2").equals(AddProduct.category2_id)) {
                        jsonObjects.add(jsonObject.getJSONArray("catregory3").getJSONObject(i));
                        find = true;
                    }
                if (find)
                    categoryRV.setAdapter(new CategoryAdapter(jsonObjects));
                else {
                    category2_id = "11";
                    this.cancel();
                    AddProduct.categoryNumber--;
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
                        if (AddProduct.categoryNumber != 0)
                            categoryTitle.setText(holder.customeTextView.getText().toString());
                        try {
                            if (AddProduct.categoryNumber == 0) {
                                AddProduct.category1_id = jsonArray.get(position).getString("id");
                                category1Title = jsonArray.get(position).getString("name");
                            } else if (AddProduct.categoryNumber == 1) {
                                AddProduct.category2_id = jsonArray.get(position).getString("id");
                                AddProduct.category2Title = jsonArray.get(position).getString("name");
                            } else if (AddProduct.categoryNumber == 2)
                                AddProduct.category3_id = jsonArray.get(position).getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (AddProduct.categoryNumber < 2) {
                            AddProduct.categoryNumber++;
                            new Category(activity);
                        }

                        //  getActivity().onBackPressed();

                        Category.this.cancel();
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
