package com.example.pc.khaledwaleedshopping.myprofile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.products.home.ProductListAdapter;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by pc on 10/28/2017.
 */

public class MyItems extends Fragment {

    RecyclerView productRV;
    JSONArray favorProducts;
    ProductListAdapter productListAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myitems, container, false);

        productRV = (RecyclerView) view.findViewById(R.id.productList);
        final GridLayoutManager mLayoutManager;
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        productRV.setLayoutManager(mLayoutManager);
        favorProducts = new JSONArray();
        productListAdapter = new ProductListAdapter(favorProducts, getActivity(), "Edit");
        productRV.setAdapter(productListAdapter);
        loadingProducts();

        return view;
    }

    private void loadingProducts() {
        try {
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        for (int postion = 0; postion < jsonArray.length(); postion++)
                            favorProducts.put(jsonArray.getJSONObject(postion));
                        if (favorProducts.length() == 0) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            alertDialogBuilder.setMessage("You have no items yet").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().onBackPressed();
                                }
                            }).show();
                        }
                        productListAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.clientProducts + MainActivity.jsonObjectUser.getString("id") + "&limit=0", "&user_id=" + MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
