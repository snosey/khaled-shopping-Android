package com.example.pc.khaledwaleedshopping.Support.design;

/**
 * Created by ahmed on 3/25/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomeTextView extends android.support.v7.widget.AppCompatTextView {


    public CustomeTextView(Context context) {
        super(context);
        this.setTextColor(Color.BLACK);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        // this.setTypeface(face);
    }

    public CustomeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTextColor(Color.BLACK);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        //  this.setTypeface(face);
    }

    public CustomeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTextColor(Color.BLACK);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "font.ttf");
        //   this.setTypeface(face);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


    }

}
