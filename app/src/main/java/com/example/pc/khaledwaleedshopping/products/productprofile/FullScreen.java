package com.example.pc.khaledwaleedshopping.products.productprofile;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.example.pc.khaledwaleedshopping.R;
import com.example.pc.khaledwaleedshopping.Support.design.CustomSliderFullScreenView;
import com.example.pc.khaledwaleedshopping.Support.design.CustomSliderFullScreenView;
import com.example.pc.khaledwaleedshopping.Support.webservice.WebService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Snosey on 12/17/2017.
 */

public class FullScreen extends Dialog {
    public FullScreen(@NonNull final FragmentActivity activity, String json) throws JSONException {
        super(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.show();
        JSONObject productDetails = new JSONObject(json);
        this.setContentView(R.layout.full_screen);
        SliderLayout productImages;
        productImages = (SliderLayout) this.findViewById(R.id.productImages);
        productImages.stopAutoCycle();
        //images 1
        if (!productDetails.getString("img1").equals(" ")) {
            CustomSliderFullScreenView textSliderView = new CustomSliderFullScreenView(activity);
            textSliderView
                    .image(WebService.imageLink + productDetails.getString("img1")).error(R.drawable.icon_no_logo)
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
            productImages.addSlider(textSliderView);
        }
        //images 2
        if (!productDetails.getString("img2").equals(" ")) {
            CustomSliderFullScreenView textSliderView = new CustomSliderFullScreenView(activity);
            textSliderView
                    .image(WebService.imageLink + productDetails.getString("img2")).error(R.drawable.icon_no_logo)
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
            productImages.addSlider(textSliderView);
        }
        //images 3
        if (!productDetails.getString("img3").equals(" ")) {
            CustomSliderFullScreenView textSliderView = new CustomSliderFullScreenView(activity);
            textSliderView
                    .image(WebService.imageLink + productDetails.getString("img3")).error(R.drawable.icon_no_logo)
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
            productImages.addSlider(textSliderView);
        }
        //images 4
        if (!productDetails.getString("img4").equals(" ")) {
            CustomSliderFullScreenView textSliderView = new CustomSliderFullScreenView(activity);
            textSliderView
                    .image(WebService.imageLink + productDetails.getString("img4")).error(R.drawable.icon_no_logo)
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
            productImages.addSlider(textSliderView);
        }
        //images 5
        if (!productDetails.getString("img5").equals(" ")) {
            CustomSliderFullScreenView textSliderView = new CustomSliderFullScreenView(activity);
            textSliderView
                    .image(WebService.imageLink + productDetails.getString("img5")).error(R.drawable.icon_no_logo)
                    .setScaleType(BaseSliderView.ScaleType.CenterInside);
            productImages.addSlider(textSliderView);
        }


    }
}
