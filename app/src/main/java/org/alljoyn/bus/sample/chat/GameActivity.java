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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nana on 13.07.16.
 */
public class GameActivity extends Activity{

    private static final String TAG = "GameActivity";
    private ChatApplication mChatApplication = null;
    private List<String> history = new ArrayList<String>();
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


        //do this in thread
        mChatApplication = (ChatApplication)getApplication();
        mChatApplication.checkin();

        int readyCount = 0;

        while(true) {
            history = mChatApplication.getHistory();
            for (String s : history){
                if(s.equals(String.valueOf('1'))){
                    readyCount ++;
                    if(readyCount == 3){
                        //start game
                        Log.d(TAG, "all members ready");
                        break;
                    }
                }
                else{
                    if(s.equals(String.valueOf('0'))){
                        break;
                    }
                }
            }
        }
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
        text.setText("Answer: " +Question);

    }

    public void setNotChosen(String Question){
        rl.setBackgroundColor(Color.rgb(210,250,245));

        notChosen.setVisibility(View.VISIBLE);
        chosen.setVisibility(View.INVISIBLE);
        hourGlass.setVisibility(View.INVISIBLE);
        
        text.setText(Question);

    }
}
