package com.example.pc.khaledwaleedshopping.products.productprofile;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pc.khaledwaleedshopping.MainActivity;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.image.CircleTransform;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeEditText;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
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
 * Created by ahmed on 6/26/2017.
 */

public class ProductComments extends Fragment {
    RecyclerView recyclerViewComments;
    CustomeButton commentButton;
    CustomeEditText commentText;
    String productId;
    String clientId;


    public ProductComments() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comments, container, false);

        ImageView back = (ImageView) view.findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        commentButton = (CustomeButton) view.findViewById(R.id.commentButton);
        commentText = (CustomeEditText) view.findViewById(R.id.commentText);
        Bundle bundle = getArguments();
        productId = bundle.getString("productId");
        try {
            clientId = MainActivity.jsonObjectUser.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerViewComments = (RecyclerView) view.findViewById(R.id.commentsList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewComments.setLayoutManager(layoutManager);

        getComments();
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!commentText.getText().toString().equals("")) {
                    UrlData urlData = new UrlData();
                    urlData.add("comment", commentText.getText().toString());
                    urlData.add("id_client", clientId);
                    urlData.add("id_product", productId);
                    new GetData(new GetData.AsyncResponse() {
                        @Override
                        public void processFinish(String result) {

                            if (result != null) {
                                commentText.setText("");
                                getComments();
                            }

                        }
                    }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.postComments, urlData.get());

                } else
                    Toast.makeText(getActivity(), "You cant write empty comment!", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    private void getComments() {
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result != null)
                    recyclerViewComments.setAdapter(new commentAdapter(result));

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.getComments, productId);

    }


    private class commentAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private JSONArray commentList;

        private commentAdapter(String result) {
            try {
                 commentList = new JSONArray(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, final int position) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comments_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            JSONObject comment = null;
            try {
                comment = commentList.getJSONObject(position);
                holder.comment.setText(comment.getString("comment"));
                holder.clientName.setText(comment.getString("name"));

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR, -6);
                try {

                    Date currentDate = format.parse(format.format(c.getTime()));
                    Date created_at = format.parse(comment.getString("created_at").substring(0, 10));
                    int days = (int) ((currentDate.getTime() - created_at.getTime()) / (1000 * 60 * 60 * 24));
                    if (days == 0)
                        holder.date.setText("today");
                    else if (days == 1)
                        holder.date.setText("yesterday");
                    else holder.date.setText(days + " days");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (comment.getString("id_client").equals(clientId))
                    holder.remove.setVisibility(View.VISIBLE);

                final JSONObject finalComment = comment;
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Light_Dialog);
                        } else {
                            builder = new AlertDialog.Builder(getContext());
                        }
                        builder.setMessage("Are you sure you want to delete this comment?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            new GetData(new GetData.AsyncResponse() {
                                                @Override
                                                public void processFinish(String result) {

                                                    getComments();


                                                }
                                            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.deleteComment, finalComment.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();


                    }
                });
                if (!comment.getString("logo").equals(""))
                    Picasso.with(getActivity()).load(WebService.imageLink + comment.getString("logo")).transform(new CircleTransform()).into(holder.userIcon, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            holder.userIcon.setImageResource(R.drawable.profile);
                        }
                    });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return (int) commentList.length();
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public CustomeTextView clientName, date, comment;
        public ImageView userIcon, remove;

        public MyViewHolder(View v) {
            super(v);

            clientName = (CustomeTextView) v.findViewById(R.id.clientName);
            date = (CustomeTextView) v.findViewById(R.id.date);
            comment = (CustomeTextView) v.findViewById(R.id.comment);
            userIcon = (ImageView) v.findViewById(R.id.userIcon);
            remove = (ImageView) v.findViewById(R.id.remove);

        }


    }

}
