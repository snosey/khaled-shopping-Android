package com.example.pc.khaledwaleedshopping.masseges;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomSliderFullScreenView;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;

import org.json.JSONException;

/**
 * Created by Snosey on 12/17/2017.
 */

public class FullScreen extends Dialog {
    public FullScreen(@NonNull final FragmentActivity activity, String url) throws JSONException {
        super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.show();
        this.setContentView(R.layout.full_screen);
        SliderLayout productImages;
        productImages = (SliderLayout) this.findViewById(R.id.productImages);
        productImages.stopAutoCycle();
        //images 1
        CustomSliderFullScreenView textSliderView = new CustomSliderFullScreenView(activity);
        textSliderView
                .image(WebService.imageLink + url).error(R.drawable.icon_no_logo)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        productImages.addSlider(textSliderView);
    }
}
