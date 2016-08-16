package org.alljoyn.bus.sample.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Nana on 13.07.16.
 */
public class GameActivity extends Activity {

    private static final String TAG = "GameActivity";
    private ChatApplication mChatApplication = null;
    private List<String> history = new ArrayList<String>();
    RelativeLayout rl;
    ImageView hourGlass, chosen, notChosen;
    ToggleButton toggle;
    TextView text;
    private boolean isHost = false;
    private boolean foundWinner = false;
    private int localId;
    String android_id;

    DataBaseHelper myDbHelper = new DataBaseHelper(this);


    private Runnable running = new Runnable() {
        @Override
        public void run() {
            while (true){
                try{
                    Thread.sleep(100);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                //get messages
                history = mChatApplication.getHistory();

                //todo: Change message such that it uses device id
                //host gambles for winner
                if(!foundWinner) {
                    if (isHost) {
                        int winner = new Random().nextInt(100 - 10) + 10;
                        Log.d(TAG,"winner: " + winner);
                        if (winner == localId) {
                            String question = myDbHelper.getQuestionByPriority();
                            chosen(true, question);
                            mChatApplication.newLocalUserMessage("win");
                        } else {
                            mChatApplication.newLocalUserMessage(String.valueOf(winner) + "id");
                        }
                    }
                }
                for (String s : history){
                    if(s.endsWith("id")){
                        if(s.substring(s.length()-5,s.length()-2).equals(String.valueOf(localId))){
                            mChatApplication.newLocalUserMessage("win");
                            foundWinner = true;
                            String question = myDbHelper.getQuestionByPriority();
                            chosen(true, question);
                        }
                    }
                    if(s.endsWith("win")){
                        foundWinner = true;
                        chosen(false, "");
                    }
                }

            }
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Game started");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.question);


        //getDeviceId
        android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d(TAG, "android_id: " + android_id);

        boolean foundLocalId = false;
        int start = 1;
        int end = 3;
        while(!foundLocalId) {
            try {
                localId = Integer.parseInt(android_id.substring(start, end));
                Log.d(TAG, "localId: " + localId);
                foundLocalId=true;
            }
            catch(Exception e){
                start ++;
                end ++;
            }
        }

        //get Host
        try {
            isHost = HostActivity.getActivityInstance().getHost();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        rl = (RelativeLayout) findViewById(R.id.my_rl);
        text = (TextView) findViewById(R.id.textView);
        hourGlass = (ImageView) findViewById(R.id.imageView3);
        notChosen = (ImageView) findViewById(R.id.imageView);
        chosen = (ImageView) findViewById(R.id.imageView2);
        toggle = (ToggleButton)findViewById(R.id.toggleButton);

        setWaitOthers();

        mChatApplication = (ChatApplication) getApplication();
        mChatApplication.checkin();

        new Thread(running).start();


    }

    public void setWaitOthers() {
        rl.setBackgroundColor(Color.rgb(210, 250, 245));
        notChosen.setVisibility(View.INVISIBLE);
        chosen.setVisibility(View.INVISIBLE);
        hourGlass.setVisibility(View.VISIBLE);

        text.setText("Wait for other Players");
        toggle.setVisibility(View.INVISIBLE);

    }

    public void chosen(boolean isMe, String question){
        if(isMe) {
            rl.setBackgroundColor(Color.RED);
            notChosen.setVisibility(View.INVISIBLE);
            chosen.setVisibility(View.VISIBLE);
            hourGlass.setVisibility(View.INVISIBLE);
            text.setText(question);

            toggle.setVisibility(View.VISIBLE);
            toggle.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(GameActivity.this, UseActivity.class);
                    startActivity(intent);
                }
            });
        }
        else{
            rl.setBackgroundColor(Color.rgb(210, 250, 245));

            notChosen.setVisibility(View.VISIBLE);
            chosen.setVisibility(View.INVISIBLE);
            hourGlass.setVisibility(View.INVISIBLE);

            text.setText("Lucky one! \\n Another Player got chosen.");
            toggle.setVisibility(View.VISIBLE);
            toggle.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(GameActivity.this, UseActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
/*
    public void setChosen(String Question) {
        rl.setBackgroundColor(Color.rgb(244, 150, 150));

        notChosen.setVisibility(View.INVISIBLE);
        chosen.setVisibility(View.VISIBLE);
        hourGlass.setVisibility(View.INVISIBLE);
        text.setText("Answer: " + Question);
        toggle.setVisibility(View.VISIBLE);
        toggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, UseActivity.class);
                startActivity(intent);


            }
        });    }

    public void setNotChosen(String Question) {
        rl.setBackgroundColor(Color.rgb(210, 250, 245));

        notChosen.setVisibility(View.VISIBLE);
        chosen.setVisibility(View.INVISIBLE);
        hourGlass.setVisibility(View.INVISIBLE);

        text.setText(Question);
        toggle.setVisibility(View.VISIBLE);
        toggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }*/

    public void startRoulette(boolean isMe) {
        if (isMe) {

            /*runOnUiThread(new Runnable() {
                public void run() {

                    //rl.setBackgroundColor(Color.RED);
                    CountDownTimer start = new CountDownTimer(4000, 250) {
                        boolean alternate = false;

                        @Override
                        public void onTick(long arg0) {
                            if (alternate == true) {
                                rl.setBackgroundColor(getResources().getColor(R.color.brightBlue));
                                alternate = false;
                            } else {
                                rl.setBackgroundColor(getResources().getColor(R.color.brightRed));
                                alternate = true;
                            }
                        }

                        @Override
                        public void onFinish() {
                            rl.setBackgroundColor(getResources().getColor(R.color.brightRed));
                            //String question = myDbHelper.getQuestionByPriority();
                            //todo: stop flickering and show question
                            setChosen("Lucky one! \n Another Player got chosen.");


                        }
                    }.start();
                }
            });*/
        }
        else{
            /*nOnUiThread(new Runnable() {
                public void run() {

                    //rl.setBackgroundColor(Color.RED);
                    CountDownTimer start = new CountDownTimer(4000, 250) {
                        boolean alternate = false;

                        @Override
                        public void onTick(long arg0) {
                            if (alternate == true) {
                                rl.setBackgroundColor(getResources().getColor(R.color.brightBlue));
                                alternate = false;
                            } else {
                                rl.setBackgroundColor(getResources().getColor(R.color.brightRed));
                                alternate = true;
                            }
                        }

                        @Override
                        public void onFinish() {
                            rl.setBackgroundColor(getResources().getColor(R.color.brightBlue));
                            String question = myDbHelper.getQuestionByPriority();
                            setNotChosen(question);
                        }
                    }.start();
                }
            });*/
        }
    }

}
