package com.example.pc.khaledwaleedshopping.products.home;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.products.filter.FullFilter;
import com.example.pc.khaledwaleedshopping.products.filter.Search;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by pc on 10/8/2017.
 */


public class Home extends Fragment {


    private SwipeRefreshLayout refreshLayout;

    static int pastVisiblesItems, visibleItemCount, totalItemCount, current;
    private boolean loading = true;
    RecyclerView productRV;
    public static int downScroll = -1;
    static JSONArray jsonObjects;
    ProductListAdapter productListAdapter;
    public static MaterialSearchView searchView;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Set<String> searchSet;
    Button filter, sort;
    FullFilter fullFilter;
    public static String kind = "";
    private boolean fullFilterActive = false;
    PopupMenu popup;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current", current);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_home, container, false);

        kind = "";

        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.mainColor));

        popup = new PopupMenu(getActivity(), view.findViewById(R.id.sort));
        popup.getMenuInflater().inflate(R.menu.sort, popup.getMenu());

        filter = (Button) view.findViewById(R.id.filter);
        sort = (Button) view.findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        item.setChecked(true);
                        if (item.getItemId() == R.id.high) {
                            sortItems("price", true);
                        } else if (item.getItemId() == R.id.low) {
                            sortItems("price", false);
                        } else {
                            sortItems("id", true);
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fullFilterActive)
                    fullFilter = new FullFilter(getActivity());

                fullFilterActive = true;
                fullFilter.show();
                fullFilter.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        jsonObjects = new JSONArray();
                        productRV.invalidate();
                        productListAdapter = new ProductListAdapter(jsonObjects, getActivity(), "Inbox");
                        productRV.setAdapter(productListAdapter);
                        current = 0;
                        downScroll = 0;
                        loadingProducts(0, kind);

                    }
                });
                fullFilter.defaultFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fullFilter.dismiss();
                        fullFilterActive = false;
                        filter.callOnClick();
                    }
                });
            }
        });
        sharedPreferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        searchView = (MaterialSearchView) view.findViewById(R.id.searchbar);
        searchSet = sharedPreferences.getStringSet("search", new HashSet<String>());
        searchView.setSuggestions(searchSet.toArray(new String[0]));
        searchView.showSuggestions();

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchSet = sharedPreferences.getStringSet("search", new HashSet<String>());
                searchSet.add(query);
                searchView.setSuggestions(searchSet.toArray(new String[0]));
                searchView.showSuggestions();
                editor.putStringSet("search", searchSet);
                editor.commit();
                Bundle bundle = new Bundle();
                bundle.putString("name", query);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Search fragment = new Search();
                fragment.setArguments(bundle);
                ft.replace(R.id.activity_main_content_fragment3, fragment);
                ft.addToBackStack(null);
                ft.commit();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        CustomeTextView msgNumber;
        msgNumber = (CustomeTextView) view.findViewById(R.id.msgNumber);
        try {
            if (!MainActivity.jsonObjectUser.getString("countUnSeen").equals("0")) {
                msgNumber.setVisibility(View.VISIBLE);
                msgNumber.setTextColor(Color.WHITE);
                msgNumber.setText(MainActivity.jsonObjectUser.getString("countUnSeen"));
            } else
                msgNumber.setVisibility(View.INVISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        productRV = (RecyclerView) view.findViewById(R.id.productList);
        final GridLayoutManager mLayoutManager;
        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        mLayoutManager = new GridLayoutManager(getActivity(), 2);
        productRV.setLayoutManager(mLayoutManager);
        if (downScroll == -1 || downScroll == -2) {
            jsonObjects = new JSONArray();
            loadingProducts(0, kind);
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
                                loadingProducts(totalItemCount, kind);
                                current = totalItemCount;
                            }
                            //Do pagination.. i.e. fetch new data
                        }
                    }
                }
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                jsonObjects = new JSONArray();
                productListAdapter.notifyDataSetChanged();
                loadingProducts(0, kind);
            }
        });
        return view;
    }

    private void loadingProducts(final int i, final String kind) {
        try {

            if (refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(false);
            }
            String link = "";
            if (kind.equals(""))
                link = WebService.getAllProducts;
            else {
                link = WebService.filter;
            }
            link += i + "&user_id=" + MainActivity.jsonObjectUser.getString("id") + "&" + kind;
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    try {
                        JSONArray jsonArray = new JSONArray(result);
                        if (jsonArray.length() == 0)
                            loading = false;
                        else {
                            for (int postion = 0; postion < jsonArray.length(); postion++) {
                                if (i == 0 && jsonObjects.length() == postion && !jsonObjects.toString().contains(jsonArray.getJSONObject(postion).toString())) {
                                    jsonObjects.put(jsonArray.getJSONObject(postion));
                                } else {
                                    if (i == 0 && jsonObjects.length() > postion && !jsonObjects.toString().contains(jsonArray.getJSONObject(postion).toString()) &&
                                            !jsonArray.getJSONObject(postion).getString("id").equals(jsonObjects.getJSONObject(postion).getString("id"))) {
                                        Log.e("Loading:", " New product!");
                                        jsonObjects.put(postion, jsonArray.getJSONObject(postion));
                                    } else if (!jsonObjects.toString().contains(jsonArray.getJSONObject(postion).toString()))
                                        jsonObjects.put(jsonArray.getJSONObject(postion));
                                }
                            }
                            productListAdapter.notifyDataSetChanged();
                        }
                        if (jsonObjects.length() == 0) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                            alertDialogBuilder.setMessage("Oops! We didn‘t find what you‘re looking for").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    fullFilter.show();
                                }
                            }).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, link, "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    void sortItems(final String type, boolean reverse) {
        List<JSONObject> tempJsonObjects = new ArrayList<>();
        for (int i = 0; i < jsonObjects.length(); i++) {
            try {
                tempJsonObjects.add(jsonObjects.getJSONObject(i));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Collections.sort(tempJsonObjects, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                // TODO Auto-generated method stub

                try {
                    int bool = 0;
                    if (Integer.parseInt(rhs.getString(type)) > Integer.parseInt(lhs.getString(type)))
                        bool = -1;
                    else if (Integer.parseInt(rhs.getString(type)) < Integer.parseInt(lhs.getString(type)))
                        bool = 1;

                    return bool;
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 0;
                }
            }
        });
        if (reverse)
            Collections.reverse(tempJsonObjects);

        jsonObjects = new JSONArray();
        for (int i = 0; i < tempJsonObjects.size(); i++) {
            jsonObjects.put(tempJsonObjects.get(i));
        }
        productListAdapter = new ProductListAdapter(jsonObjects, getActivity(), "Inbox");
        productRV.setAdapter(productListAdapter);
    }

}
