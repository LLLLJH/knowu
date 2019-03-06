package cn.cjwddz.knowu.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public   class TabView extends LinearLayout {
    ImageView imageView;
    TextView tv;

    public TabView(Context c, @Nullable int drawable,@Nullable int drawableselec,@Nullable String s) {
        super(c);
       // LinearLayout ll = new LinearLayout(c);
        imageView = new ImageView(c);
        StateListDrawable listDrawable = new StateListDrawable();
        if(drawable!=0&&drawable!=-1){
            listDrawable.addState(SELECTED_STATE_SET, this.getResources()
                    .getDrawable(drawableselec));
        }
       if(drawableselec != 0&&drawable!=-1){
           listDrawable.addState(ENABLED_STATE_SET, this.getResources()
                   .getDrawable(drawable));
       }
       if(listDrawable!=null){
           imageView.setImageDrawable(listDrawable);
           LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,Gravity.CENTER);
           imageView.setLayoutParams(params);
           addView(imageView);
           imageView.setBackgroundColor(Color.TRANSPARENT);
       }
        //tv = new TextView(c);
        //tv.setTextSize(15);
       // tv.setText(s);
        //tv.setGravity(Gravity.CENTER);
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        //addView(tv);
    }
}
