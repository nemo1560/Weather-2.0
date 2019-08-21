package com.example.nemo1.weather21.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {
    private static Typeface typeface;
    public CustomTextView(Context context) {
        super(context);
        setFont();
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    public void setFont(){
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "digital-7.ttf");
        setTypeface(typeface,typeface.NORMAL);
        setTextColor(getContext().getColor(android.R.color.black));
        setGravity(Gravity.CENTER);
        setPadding(5,20,5,5);
    }

}
