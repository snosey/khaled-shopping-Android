package com.example.pc.khaledwaleedshopping.Support.design;

/**
 * Created by ahmed on 3/25/2017.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomeButton extends android.support.v7.widget.AppCompatButton {


    public CustomeButton(Context context) {
        super(context);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font.ttf");
        this.setTransformationMethod(null);
        //  this.setTypeface(face);
    }

    public CustomeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font.ttf");
        // this.setTypeface(face);
    }

    public CustomeButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Typeface face= Typeface.createFromAsset(context.getAssets(), "font.ttf");
        //  this.setTypeface(face);
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);


    }

}
