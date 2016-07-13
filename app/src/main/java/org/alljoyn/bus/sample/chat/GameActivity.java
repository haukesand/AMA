package org.alljoyn.bus.sample.chat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Nana on 13.07.16.
 */
public class GameActivity extends Activity{

    private static final String TAG = "GameActivity";

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Game started");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.question);

        RelativeLayout rl = (RelativeLayout)findViewById(R.id.my_rl);
        rl.setBackgroundColor(Color.BLUE);

        ImageView hourGlass = (ImageView)findViewById(R.id.imageView3);
        hourGlass.setVisibility(View.VISIBLE);

        TextView text = (TextView)findViewById(R.id.textView);
        text.setText("Wait for other Players");



    }
}
