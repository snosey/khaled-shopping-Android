package com.example.pc.khaledwaleedshopping.masseges;

import android.graphics.Color;
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
import android.widget.ImageView;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.image.CircleTransform;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by pc on 10/20/2017.
 */

public class Inbox extends Fragment {

    RecyclerView recyclerViewChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inbox, container, false);

        CustomeTextView msgNumber;
        msgNumber = (CustomeTextView) view.findViewById(R.id.msgNumber);
        try {
            if (!MainActivity.jsonObjectUser.getString("countUnSeen").equals("0")) {
                msgNumber.setVisibility(View.GONE);
                msgNumber.setTextColor(Color.WHITE);
                msgNumber.setText(MainActivity.jsonObjectUser.getString("countUnSeen"));
            } else
                msgNumber.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerViewChat = (RecyclerView) view.findViewById(R.id.inboxList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewChat.setLayoutManager(layoutManager);

        try {
            new GetData(new GetData.AsyncResponse() {
                @Override
                public void processFinish(String result) {

                    if (result != null) {
                        recyclerViewChat.setAdapter(new chatAdapter(result));
                    }

                }

            }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.inbox, MainActivity.jsonObjectUser.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }


    private class chatAdapter extends RecyclerView.Adapter<Inbox.MyViewHolder> {
        private JSONArray chatList;

        private chatAdapter(String result) {
            try {
                chatList = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Inbox.MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.inbox_row, parent, false);
            return new Inbox.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final Inbox.MyViewHolder holder, final int position) {
            try {

                final JSONObject chatRow = chatList.getJSONObject(position);
                holder.productName.setText(chatRow.getString("product_name"));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        try {

                            bundle.putString("id_product", chatRow.getString("id_product"));
                            bundle.putString("id", chatRow.getString("id"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Chat chat = new Chat();
                        chat.setArguments(bundle);
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.addToBackStack("null");
                        ft.replace(R.id.activity_main_content_fragment3, chat);
                        ft.commit();

                    }
                });
                holder.name.setText(chatRow.getString("name"));

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR, -6);
                try {

                    Date currentDate = format.parse(format.format(c.getTime()));
                    Date created_at = format.parse(chatRow.getString("created_at").substring(0, 10));
                    int days = (int) ((currentDate.getTime() - created_at.getTime()) / (1000 * 60 * 60 * 24));
                    if (days == 0)
                        holder.date.setText("today");
                    else if (days == 1)
                        holder.date.setText("yesterday");
                    else holder.date.setText(days + " days");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (chatRow.getString("unReed").equals("0"))
                    holder.unSeen.setVisibility(View.GONE);
                else
                    holder.unSeen.setText(chatRow.getString("unReed"));

                holder.msg.setText(chatRow.getString("message"));

                if (!chatRow.getString("product_image").equals(""))
                    Picasso.with(getActivity()).load(WebService.imageLink + chatRow.getString("product_image")).transform(new CircleTransform()).into(holder.logo, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            holder.logo.setImageResource(R.drawable.profile);
                        }
                    });

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) chatList.length();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomeTextView date, msg, name, unSeen, productName;
        public ImageView logo;

        public MyViewHolder(View v) {
            super(v);

            date = (CustomeTextView) v.findViewById(R.id.date);
            msg = (CustomeTextView) v.findViewById(R.id.msg);
            logo = (ImageView) v.findViewById(R.id.logo);
            name = (CustomeTextView) v.findViewById(R.id.name);
            unSeen = (CustomeTextView) v.findViewById(R.id.unSeen);
            productName = (CustomeTextView) v.findViewById(R.id.productName);

        }


    }

}
