package com.example.pc.khaledwaleedshopping.Support.design;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.example.pc.khaledwaleedshopping.R;
import com.github.chrisbanes.photoview.PhotoView;

public class CustomSliderFullScreenView extends BaseSliderView {

    public CustomSliderFullScreenView(Context context) {
        super(context);
    }

    public View getView() {

        View v = LayoutInflater.from(this.getContext()).inflate(R.layout.custome_slider, null);
        PhotoView target = (PhotoView) v.findViewById(R.id.daimajia_slider_image);
        LinearLayout frame = (LinearLayout) v.findViewById(R.id.description_layout);
        frame.setBackgroundColor(Color.parseColor("#00FF9800"));


        this.bindEventAndShow(v, target);

        return v;
    }
}