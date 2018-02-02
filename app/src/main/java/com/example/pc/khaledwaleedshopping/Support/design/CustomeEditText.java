package com.example.pc.khaledwaleedshopping.Support.design;

/**
 * Created by ahmed on 3/25/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;

import com.example.pc.khaledwaleedshopping.R;

public class CustomeEditText extends android.support.v7.widget.AppCompatEditText {


    public CustomeEditText(Context context) {
        super(context);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        //  this.setTypeface(face);
        this.setTextColor(ResourcesCompat.getColor(getResources(), R.color.black_8am2, null));
    }

    public CustomeEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        //  this.setTypeface(face);
    }

    public CustomeEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        //   this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

}
