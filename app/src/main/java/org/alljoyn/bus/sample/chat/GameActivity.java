package org.alljoyn.bus.sample.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
    TextView text, textP;
    private boolean isHost = false;
    private boolean foundWinner = false;
    private int localId;
    String android_id;
    public boolean stop = false;
    private List<String> askedIds = new ArrayList<String>();
    private int counter = 0;

    DataBaseHelper myDbHelper = new DataBaseHelper(this);


    private Runnable running = new Runnable() {
        @Override
        public void run() {
            while (!stop){
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
                Log.d(TAG, "isHost: " + isHost);
                if(!foundWinner) {
                    String s = history.get(history.size()-1);
                    if(s.endsWith("cq")){
                        for(int i = 0; i<s.length(); i++){
                            boolean getCq = s.charAt(i)==')';
                            if(getCq){
                                String temp = s.substring(i+1, s.length()-2);
                                myDbHelper.addQuestion(temp);
                            }
                        }
                    }
                    if(s.endsWith("dq")){
                        String remoteId = s.substring(s.length()-4, s.length()-2);
                        if(remoteId.equals(String.valueOf(localId))==false) {
                                if (askedIds.contains(remoteId) == false) {
                                    askedIds.add(remoteId);
                                    for(int i = 0; i<s.length(); i++){
                                        boolean getDq = s.charAt(i)==')';
                                        if(getDq){
                                            String temp = s.substring(i+1, s.length()-4);
                                            myDbHelper.updatePriority(temp.trim());
                                        }
                                    }
                                }
                        }

                    }
                    if (isHost) {
                        int winner = new Random().nextInt(100 - 10) + 10;
                        Log.d(TAG,"winner: " + winner);
                        /*if (winner == localId) {
                            String question = myDbHelper.getQuestionByPriority();
                            chosen(true, question);
                            mChatApplication.newLocalUserMessage("win");
                        } else {*/
                            mChatApplication.newLocalUserMessage(String.valueOf(winner) + "id");
                        //}
                    }

                    Log.d(TAG, "s: " + s);
                    if(s.endsWith("id")){
                        String randomId = s.substring(s.length()-5,s.length()-2);
                        Log.d(TAG, "randomId: " + randomId);
                        //String localToString = String.valueOf(localId);
                        //Log.d(TAG, "localToString: " + localToString);

                        int randId = Integer.parseInt(randomId.substring(1));

                        boolean checkWin = (randId == localId);
                        Log.d(TAG, "checkWin: " + checkWin);
                        if(checkWin){
                            mChatApplication.newLocalUserMessage("win");
                            foundWinner = true;
                            String question = myDbHelper.getQuestionByPriority();
                            chosen(true);
                            checkWin = false;
                        }

                    }
                    if(s.endsWith("win")){
                        foundWinner = true;
                        chosen(false);
                    }
                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        String value = extras.getString("newMessage");
                        Log.d(TAG, "value: " +  value);
                        if(counter == 5) {
                            mChatApplication.newLocalUserMessage(value);
                            counter = 0;
                        }
                        else{
                            counter++;
                        }
                        Log.d(TAG, value);
                        //The key argument here must match that used in the other activity
                    }
                }
                else{
                    mChatApplication.newLocalUserMessage("win");
                    stop = true;
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

        //get host
        try {
            isHost = UseActivity.getActivityInstance().getHost();
        }
        catch(Exception e){
            e.printStackTrace();
        }


        rl = (RelativeLayout) findViewById(R.id.my_rl);
        text = (TextView) findViewById(R.id.questionT);
        textP = (TextView) findViewById(R.id.playerT);
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

        RotateAnimation r;
        r = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        r.setDuration((long) 2*1000);
        r.setRepeatCount(25);
        hourGlass.startAnimation(r);

        rl.setBackgroundColor(getResources().getColor(R.color.brightBlue));
        notChosen.setVisibility(View.INVISIBLE);
        chosen.setVisibility(View.INVISIBLE);
        hourGlass.setVisibility(View.VISIBLE);

        text.setText("Picking Player!");
        toggle.setVisibility(View.INVISIBLE);

    }

    public void chosen(boolean isMe){
        if(isMe) {
            runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    String question = myDbHelper.getQuestionByPriority();
                    rl.setBackgroundColor(getResources().getColor(R.color.brightRed));
                    notChosen.setVisibility(View.INVISIBLE);
                    chosen.setVisibility(View.VISIBLE);
                    hourGlass.clearAnimation();
                    hourGlass.setVisibility(View.INVISIBLE);
                    textP.setText("Answer This:");
                    text.setText(question);

                    toggle.setVisibility(View.VISIBLE);
                    toggle.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            foundWinner = false;
                            myDbHelper.clearDatabase();
                            Intent intent = new Intent(GameActivity.this, UseActivity.class);
                            intent.putExtra("isHost", isHost);
                            //GameActivity.this.finish();
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            });
        }
        else{
            runOnUiThread(new Runnable(){
                              @Override
                              public void run() {
                                  rl.setBackgroundColor(getResources().getColor(R.color.brightBlue));

                                  notChosen.setVisibility(View.VISIBLE);
                                  chosen.setVisibility(View.INVISIBLE);
                                  hourGlass.clearAnimation();
                                  hourGlass.setVisibility(View.INVISIBLE);

                                  text.setText("Somebody else has to answer!");
                                  textP.setText("");
                                  toggle.setVisibility(View.VISIBLE);
                                  toggle.setOnClickListener(new View.OnClickListener() {
                                      public void onClick(View v) {
                                          foundWinner = false;
                                          myDbHelper.clearDatabase();
                                          Intent intent = new Intent(GameActivity.this, UseActivity.class);
                                          intent.putExtra("isHost", isHost);
                                          //GameActivity.this.finish();
                                          startActivity(intent);
                                          finish();
                                      }
                                  });
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
