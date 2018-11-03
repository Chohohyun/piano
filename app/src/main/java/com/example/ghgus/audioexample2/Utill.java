package com.example.ghgus.audioexample2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Utill extends Activity {

        private static Typeface typeface;

        @Override
        public void setContentView(int layoutResID) {
            super.setContentView(layoutResID);
            if(typeface == null) {
                typeface = Typeface.createFromAsset(this.getAssets(), "BMJUA_ttf.ttf");
            }
            setGlobalFont(getWindow().getDecorView());
        }

        private void setGlobalFont(View view) {
            if(view != null) {
                if(view instanceof ViewGroup) {
                    ViewGroup viewGroup = (ViewGroup)view;
                    int vgCnt = viewGroup.getChildCount();
                    for(int i = 0; i<vgCnt; i++) {
                        View v = viewGroup.getChildAt(i);
                        if(v instanceof TextView) {
                            ((TextView) v).setTypeface(typeface);
                        }
                        setGlobalFont(v);
                    }
                }
            }
        }

    }
