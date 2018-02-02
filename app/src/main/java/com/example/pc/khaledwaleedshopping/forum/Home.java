package com.example.pc.khaledwaleedshopping.forum;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by pc on 10/20/2017.
 */

public class Home extends Fragment {
    Spinner spinner;
    RecyclerView recyclerViewForum;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forum, container, false);

        CustomeTextView msgNumber;
        msgNumber = (CustomeTextView) view.findViewById(R.id.msgNumber);
        try {
            if (!MainActivity.jsonObjectUser.getString("countUnSeen").equals("0")) {
                msgNumber.setVisibility(View.VISIBLE);
                msgNumber.setText(MainActivity.jsonObjectUser.getString("countUnSeen"));
            } else
                msgNumber.setVisibility(View.INVISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinner = (Spinner) view.findViewById(R.id.spinnerKind);

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result != null) {

                    try {
                        spinner.setAdapter(new CustomeAdapter(getContext(), new JSONArray(result), "name"));
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                                CustomeTextView selectedView = (CustomeTextView) spinner.getSelectedView();
                                setForumList(selectedView.getTag().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }


        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.getForumsKinds, "");


        recyclerViewForum = (RecyclerView) view.findViewById(R.id.forumList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewForum.setLayoutManager(layoutManager);
        setForumList("-1");


        return view;
    }

    private void setForumList(String id) {

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result != null) {
                    recyclerViewForum.setAdapter(new forumAdapter(result));
                }

            }


        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.getForums, id);

    }


    private class forumAdapter extends RecyclerView.Adapter<Home.MyViewHolder> {
        private JSONArray forumList;

        private forumAdapter(String result) {
            try {
                forumList = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Home.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.forum_row, parent, false);
            return new Home.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final Home.MyViewHolder holder, final int position) {
            try {

                final JSONObject forumRow = forumList.getJSONObject(position);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("id", forumRow.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Profile profile = new Profile();
                        profile.setArguments(bundle);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.addToBackStack("null");
                        ft.replace(R.id.activity_main_content_fragment3, profile);
                        ft.commit();

                    }
                });
                holder.name.setText(forumRow.getString("title"));
                holder.content.setText(forumRow.getString("content"));


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) forumList.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomeTextView content, name;

        public MyViewHolder(View v) {
            super(v);

            content = (CustomeTextView) v.findViewById(R.id.content);
            name = (CustomeTextView) v.findViewById(R.id.name);


        }


    }


}
