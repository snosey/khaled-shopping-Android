package com.example.pc.khaledwaleedshopping.products.editproduct;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by pc on 10/7/2017.
 */
class CustomeAdapter extends BaseAdapter {
    String colName;
    Context context;
    LayoutInflater inflter;
    JSONArray jsonArray;
    String spinnerName;
    public static HashMap<String, Integer> color1map, color2map, brandsMap, sizeMap, condtionMap;

    public CustomeAdapter(Context applicationContext, JSONArray jsonArray, String colName, String spinnerName) {

        this.spinnerName = spinnerName;

        if (spinnerName.equals("brands"))
            brandsMap = new HashMap<>();
        else if (spinnerName.equals("color1"))
            color1map = new HashMap<>();
        else if (spinnerName.equals("color2"))
            color2map = new HashMap<>();
        else if (spinnerName.equals("condition"))
            condtionMap = new HashMap<>();
        else if (spinnerName.equals("size"))
            sizeMap = new HashMap<>();


        this.jsonArray = jsonArray;
        this.context = applicationContext;
        this.colName = colName;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_text, null);
        CustomeTextView textView = (CustomeTextView) view.findViewById(R.id.text);
        try {

            if (spinnerName.equals("brands"))
                brandsMap.put(jsonArray.getJSONObject(i).getString("id"), i);
            else if (spinnerName.equals("color1"))
                color1map.put(jsonArray.getJSONObject(i).getString("id"), i);
            else if (spinnerName.equals("color2"))
                color2map.put(jsonArray.getJSONObject(i).getString("id"), i);
            else if (spinnerName.equals("condition"))
                condtionMap.put(jsonArray.getJSONObject(i).getString("id"), i);
            else if (spinnerName.equals("size"))
                sizeMap.put(jsonArray.getJSONObject(i).getString("id"), i);

            if (colName.equals("color")) {
                Drawable myImage = context.getResources().getDrawable(R.drawable.circel_color);
                try {
                    myImage.setTint(Color.parseColor(jsonArray.getJSONObject(i).getString("colorCode")));
                } catch (Exception e) {
                }
                textView.setCompoundDrawablePadding(15);
                textView.setCompoundDrawablesWithIntrinsicBounds(myImage, null, null, null);
            }
            textView.setText(jsonArray.getJSONObject(i).getString(colName));
            textView.setTag(jsonArray.getJSONObject(i).getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
