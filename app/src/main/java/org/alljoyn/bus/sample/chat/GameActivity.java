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
    RelativeLayout rl;
    ImageView hourGlass, chosen, notChosen;
    TextView text;
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Game started");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.question);

        rl = (RelativeLayout)findViewById(R.id.my_rl);
        text = (TextView)findViewById(R.id.textView);
        hourGlass = (ImageView)findViewById(R.id.imageView3);
        notChosen = (ImageView)findViewById(R.id.imageView);
        chosen = (ImageView)findViewById(R.id.imageView2);

    setChosen("THis is your Question?!!");
    }

    public void setWaitOthers(){
        rl.setBackgroundColor(Color.rgb(210,250,245));
        notChosen.setVisibility(View.INVISIBLE);
        chosen.setVisibility(View.INVISIBLE);
        hourGlass.setVisibility(View.VISIBLE);

        text.setText("Wait for other Players");

    }

    public void setChosen(String Question){
        rl.setBackgroundColor(Color.rgb(244,150,150));

        notChosen.setVisibility(View.INVISIBLE);
        chosen.setVisibility(View.VISIBLE);
        hourGlass.setVisibility(View.INVISIBLE);

        TextView text = (TextView)findViewById(R.id.textView);
        text.setText("Answer: " +Question);

    }

    public void setNotChosen(String Question){
        rl.setBackgroundColor(Color.rgb(210,250,245));

        notChosen.setVisibility(View.VISIBLE);
        chosen.setVisibility(View.INVISIBLE);
        hourGlass.setVisibility(View.INVISIBLE);

        TextView text = (TextView)findViewById(R.id.textView);
        text.setText(Question);

    }
}
