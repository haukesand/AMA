package org.alljoyn.bus.sample.chat;

import android.view.View;

/**
 * Created by hauke_000 on 14-Jul-16.
 */
public class MyOnClickListener implements View.OnClickListener
{

    int myLovelyVariable;
    public MyOnClickListener(int myLovelyVariable) {
        this.myLovelyVariable = myLovelyVariable;
    }

    @Override
    public void onClick(View v)
    {
        //read your lovely variable
    }

};
