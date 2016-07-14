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
}
