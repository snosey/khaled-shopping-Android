package com.example.pc.khaledwaleedshopping.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 10/7/2017.
 */
class CustomeAdapter extends BaseAdapter {
    String colName;
    Context context;
    LayoutInflater inflter;
    JSONArray jsonArray;
    String spinnerName;

    public CustomeAdapter(Context applicationContext, JSONArray jsonArray, String colName) {


        String any = "{\"" + colName + "\": \"All\" , \"id\": \"-1\"}";
        try {
            JSONObject jsonObject = new JSONObject(any);
            for(int i=jsonArray.length()-1;i>=0;i--){
                jsonArray.put(i+1,jsonArray.getJSONObject(i));
            }
            jsonArray.put(0,jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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


            textView.setText(jsonArray.getJSONObject(i).getString(colName));
            textView.setTag(jsonArray.getJSONObject(i).getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
