package com.example.pc.khaledwaleedshopping.products.filter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.products.home.ProductListAdapter;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by pc on 10/8/2017.
 */


public class Search extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static int pastVisiblesItems, visibleItemCount, totalItemCount, current;
    private boolean loading = true;
    RecyclerView productRV;
    private int downScroll = -1;
    static JSONArray jsonObjects;
    ProductListAdapter productListAdapter;
    private static MaterialSearchView searchView;
    private String titleQuery;
    ImageView search;
    CustomeTextView title;
    Set<String> searchSet;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current", current);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_filter, container, false);

        title = (CustomeTextView) view.findViewById(R.id.title);

        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        searchView = (MaterialSearchView) view.findViewById(R.id.searchbar);

        searchSet = sharedPreferences.getStringSet("search", new HashSet<String>());
        searchView.setSuggestions(searchSet.toArray(new String[0]));
        searchView.showSuggestions();
        search = (ImageView) view.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.showSearch();
            }
        });
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                titleQuery = query;
                filterProducts(titleQuery);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        Bundle bundle = getArguments();
        titleQuery = bundle.getString("name");
        title.setText(titleQuery);
        productRV = (RecyclerView) view.findViewById(R.id.productList);
        final GridLayoutManager mLayoutManager;
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        productRV.setLayoutManager(mLayoutManager);
        if (downScroll == -1) {
            jsonObjects = new JSONArray();
            loadingProducts(0);
            current = 0;
            downScroll = 0;
        }
        productListAdapter = new ProductListAdapter(jsonObjects, getActivity(), "Inbox");
        productRV.setAdapter(productListAdapter);
        productRV.scrollTo(downScroll, 0);

        productRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                downScroll = dy;
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            Log.v("...", "Last Item Wow !");
                            if (totalItemCount != current) {
                                loadingProducts(totalItemCount);
                                current = totalItemCount;
                            }
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });

        return view;
    }

    private void filterProducts(String titleQuery) {

        searchSet = sharedPreferences.getStringSet("search", new HashSet<String>());
        searchSet.add(titleQuery);
        searchView.setSuggestions(searchSet.toArray(new String[0]));
        searchView.showSuggestions();
        editor.putStringSet("search", searchSet);
        editor.commit();
        title.setText(titleQuery);
        productRV.invalidate();
        jsonObjects = new JSONArray();
        productListAdapter = new ProductListAdapter(jsonObjects, getActivity(), "Inbox");
        productRV.setAdapter(productListAdapter);
        current = 0;
        downScroll = 0;
        loadingProducts(0);
    }

    private void loadingProducts(final int i) {

        try {
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() == 0)
                            loading = false;
                        else {

                            for (int postion = 0; postion < jsonArray.length(); postion++) {
                                jsonObjects.put(jsonArray.getJSONObject(postion));
                            }
                            productListAdapter.notifyDataSetChanged();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.search + i, "&user_id=" + MainActivity.jsonObjectUser.getString("id") + "&text=" + titleQuery);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
