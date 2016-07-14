package org.alljoyn.bus.sample.chat;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by hauke_000 on 14-Jul-16.
 */
public class MyOnClickListener implements View.OnClickListener
{

    String s;
    Context context;
    public MyOnClickListener(String s,Context context)
    {
        this.s = s;
        this.context=context;
    }

    @Override
    public void onClick(View arg0) {

        // now finish Activity as\
        //context.finish();
        //  OR
        ((Activity)context).finish();
    }

};
