package com.example.pc.khaledwaleedshopping.clientprofile;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.pc.khaledwaleedshopping.Support.design.CustomeButton;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeEditText;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.image.CircleTransform;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.UrlData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.willy.ratingbar.BaseRatingBar;
import com.willy.ratingbar.ScaleRatingBar;

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

public class ClientReviews extends Fragment {
    RecyclerView recyclerViewReviews;
    CustomeButton commentButton;
    CustomeEditText commentText;
    String clientId;
    private String userId;


    public ClientReviews() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviews_client, container, false);

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
        clientId = bundle.getString("id");
        try {
            userId = MainActivity.jsonObjectUser.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        recyclerViewReviews = (RecyclerView) view.findViewById(R.id.reviewsList);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerViewReviews.setLayoutManager(layoutManager);

        getReviews();
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (MainActivity.jsonObjectUser.getString("id").equals(clientId)) {
                        Toast.makeText(getActivity(), "You can‘t review yourself", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addReview();
            }
        });
        return view;
    }

    private void addReview() {
        {
            if (!commentText.getText().toString().equals("")) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_rate);
                dialog.show();
                ScaleRatingBar ratingBar = (ScaleRatingBar) dialog.findViewById(R.id.simpleRatingBar5);
                ratingBar.setNumStars(5);
                ratingBar.setRating(1);
                ratingBar.setStarPadding(10);
                ratingBar.setTouchable(true);
                ratingBar.setClearRatingEnabled(true);
                ratingBar.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
                    @Override
                    public void onRatingChange(BaseRatingBar baseRatingBar, float v) {
                        String rateN = (v + "").substring(0, 1);
                        UrlData urlData = new UrlData();
                        urlData.add("data", commentText.getText().toString());
                        urlData.add("id_client", clientId);
                        urlData.add("rate", rateN);
                        urlData.add("id_rate_client", userId);

                        new GetData(new GetData.AsyncResponse() {
                            @Override
                            public void processFinish(String result) {

                                if (result != null) {
                                    commentText.setText("");
                                    getReviews();
                                }

                            }
                        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.postReview, urlData.get());

                        Toast.makeText(getActivity(), "Thank you for your review.", Toast.LENGTH_SHORT).show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.cancel();
                            }
                        }, 2000);
                    }
                });
            } else
                Toast.makeText(getActivity(), "You cant write empty review!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getReviews() {
        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result != null)
                    recyclerViewReviews.setAdapter(new commentAdapter(result));

            }
        }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.getReviews, clientId);

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
                    .inflate(R.layout.review_row, parent, false);
            return new MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {

                JSONObject reviewRow = commentList.getJSONObject(position);

                if (!reviewRow.getString("logo").equals(""))
                    Picasso.with(getActivity()).load(WebService.imageLink + reviewRow.getString("logo")).transform(new CircleTransform()).into(holder.userIcon, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                            holder.userIcon.setImageResource(R.drawable.profile);
                        }
                    });

                holder.rate.setText("Rate: " + reviewRow.getString("rate") + "/5");
                holder.comment.setText(reviewRow.getString("data"));
                holder.clientName.setText(reviewRow.getString("name"));

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.add(Calendar.HOUR, -6);
                try {

                    Date currentDate = format.parse(format.format(c.getTime()));
                    Date created_at = format.parse(reviewRow.getString("created_at").substring(0, 10));
                    int days = (int) ((currentDate.getTime() - created_at.getTime()) / (1000 * 60 * 60 * 24));
                    if (days == 0)
                        holder.date.setText("Today");
                    else if (days == 1)
                        holder.date.setText("Yesterday");
                    else holder.date.setText(days + " Days");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (reviewRow.getString("id_rate_client").equals(userId))
                    holder.remove.setVisibility(View.VISIBLE);

                final JSONObject finalComment = reviewRow;
                holder.remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Light_Dialog);
                        } else {
                            builder = new AlertDialog.Builder(getContext());
                        }
                        builder.setMessage("Are you sure you want to delete this review?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            new GetData(new GetData.AsyncResponse() {
                                                @Override
                                                public void processFinish(String result) {

                                                    getReviews();


                                                }
                                            }, getActivity(), false).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.deleteReview, finalComment.getString("id"));
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

        public CustomeTextView clientName, date, comment, rate;
        public ImageView userIcon, remove;

        public MyViewHolder(View v) {
            super(v);

            clientName = (CustomeTextView) v.findViewById(R.id.clientName);
            date = (CustomeTextView) v.findViewById(R.id.date);
            rate = (CustomeTextView) v.findViewById(R.id.rate);
            comment = (CustomeTextView) v.findViewById(R.id.comment);
            userIcon = (ImageView) v.findViewById(R.id.userIcon);
            remove = (ImageView) v.findViewById(R.id.remove);

        }


    }

}
