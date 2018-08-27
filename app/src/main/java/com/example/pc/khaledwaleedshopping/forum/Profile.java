package com.example.pc.khaledwaleedshopping.forum;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomSliderView;
import com.example.pc.khaledwaleedshopping.Support.design.CustomeTextView;
import com.example.pc.khaledwaleedshopping.Support.webservice.GetData;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Snosey on 11/9/2017.
 */

public class Profile extends android.support.v4.app.Fragment {
    CustomeTextView content, title1, title2, comments;
    private String id;
    SliderLayout forumImages;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forum_profile, container, false);

        content = (CustomeTextView) view.findViewById(R.id.content);
        title1 = (CustomeTextView) view.findViewById(R.id.name1);
        title2 = (CustomeTextView) view.findViewById(R.id.name2);
        comments = (CustomeTextView) view.findViewById(R.id.comments);
        forumImages = (SliderLayout) view.findViewById(R.id.forumImages);
        forumImages.stopAutoCycle();

        Bundle bundle = getArguments();
        id = bundle.getString("id");

        new GetData(new GetData.AsyncResponse() {
            @Override
            public void processFinish(String result) {

                if (result != null) {
                    try {
                        final JSONObject object = new JSONObject(result);
                        content.setText(object.getString("content"));
                        title1.setText(object.getString("title"));
                        title2.setText(object.getString("title"));
                        comments.setText("Comments ( " + object.getString("countComments") + " )");
                        comments.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bundle bundle = new Bundle();
                                try {
                                    bundle.putString("forumId", object.getString("id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ForumComments forumComments = new ForumComments();
                                forumComments.setArguments(bundle);
                                ft.replace(R.id.activity_main_content_fragment3, forumComments);
                                ft.addToBackStack(null);
                                ft.commit();
                            }
                        });
                        //images 1
                        if (!object.getString("img1").equals("")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView
                                    .image(WebService.imageLink + object.getString("img1")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            forumImages.addSlider(textSliderView);
                        }
                        //images 2
                        if (!object.getString("img2").equals("")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView
                                    .image(WebService.imageLink + object.getString("img2")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            forumImages.addSlider(textSliderView);
                        }
                        //images 3
                        if (!object.getString("img3").equals("")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView
                                    .image(WebService.imageLink + object.getString("img3")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            forumImages.addSlider(textSliderView);
                        }
                        //images 4
                        if (!object.getString("img4").equals("")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView
                                    .image(WebService.imageLink + object.getString("img4")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            forumImages.addSlider(textSliderView);
                        }
                        //images 5
                        if (!object.getString("img5").equals("")) {
                            CustomSliderView textSliderView = new CustomSliderView(getActivity());
                            textSliderView
                                    .image(WebService.imageLink + object.getString("img5")).error(R.drawable.icon_no_logo)
                                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
                            forumImages.addSlider(textSliderView);
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }


        }, getActivity(), true).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, WebService.getForumsProfile, id);

        return view;
    }
}
