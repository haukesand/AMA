package org.alljoyn.bus.sample.chat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private int readyCount = 0;
    RelativeLayout rl;
    ImageView hourGlass, chosen, notChosen;
    ToggleButton toggle;
    TextView text;
    String [][] users = new String[3][2];
    private int x = 0;
    private String myName;
    private int myId;
    private int winner;
    private boolean meSet = false;

    private boolean meChosen = false;
    DataBaseHelper myDbHelper = new DataBaseHelper(this);
    //todo:
    //create twodimensional array with 3 elements
    //get remote usernames (when '1' posted -> get order for user index)
    //create variable "me"
    //send remote usernames
    //compare with other users usernames
    //username not in own list = 'me' -> add 'me' to userlist
    //user with index 0 does math.random -> posts result
    //resulting index = winner

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
                history = mChatApplication.getHistory();
                for (String s : history) {
                    Log.d(TAG, s);
                    String test =  s.substring(9,11);
                    boolean bla = s.substring(s.length()-1, s.length()).equals(String.valueOf('1'));
                    if (bla) {
                        //get remote nicknames
                        Log.d(TAG, s + " " + s.length());

                        if(s.length()>14 && s.charAt(14) == ')') {
                            String temp = s.substring(1,10);
                            boolean isInList = false;
                            for(int i = 0; i<users.length; i++){
                                if(users[i][1].equals(temp)){
                                    isInList = true;
                                }
                            }
                            if(!isInList) {
                                users[x][1] = temp;
                                users[x][2] = String.valueOf(x);
                                readyCount++;
                                x++;
                            }

                        }
                        else{
                            if(s.substring(9,11).equals("Me")){
                                if(!meSet) {
                                    x++;
                                    meSet = true;
                                }
                            }
                        }
                        if (readyCount == 2) {
                            for(int i = 0; i<users.length; i++){
                                mChatApplication.newLocalUserMessage("l: " + users[i][1]);
                            }
                        }
                    } else {
                        Log.d(TAG, "test test");
                        if (s.substring(12,13).equals("l")) {
                            boolean inList = false;
                            int abc = users.length;
                            for(int i = 0; i<users.length; i++){
                                if(s.equals(users[i][1])==false){
                                    inList = true;
                                }
                            }
                            if(!inList){
                                users[2][1] = s.substring(16, s.length());
                                myName = s.substring(16, s.length());
                                if(Integer.parseInt(users[0][2]) == 0){
                                    if(Integer.parseInt(users[1][2]) == 1){
                                        users[2][2] = "2";
                                        myId = 2;
                                    }
                                    else{
                                        if(Integer.parseInt(users[0][2]) == 2){
                                            users[2][2] = "1";
                                            myId = 1;
                                        }
                                    }
                                }
                                else{
                                    if(Integer.parseInt(users[0][2]) == 1){
                                        if(Integer.parseInt(users[1][2]) == 0){
                                            users[2][2] = "2";
                                            myId = 2;
                                        }
                                        if(Integer.parseInt(users[1][2]) == 2){
                                            users[2][2] = "0";
                                            myId = 0;
                                        }
                                    }
                                    else{
                                        if(Integer.parseInt(users[1][2]) == 0){
                                            users[2][2] = "1";
                                            myId = 1;
                                        }
                                        if(Integer.parseInt(users[1][2]) == 1){
                                            users[2][2] = "0";
                                            myId = 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(users[0][1] != null && !users[0][1].isEmpty()) {
                        for (int i = 0; i < users.length; i++) {
                            Log.d(TAG, "username: " + users[i][1]);
                            Log.d(TAG, "userId: " + users[i][2]);
                        }
                    }
                    if(myId == 0) {
                        winner = new Random().nextInt(3);
                        mChatApplication.newLocalUserMessage("w: " + winner);
                        if (winner == myId){
                            //I got chosen!!!
                            startRoulette(true);
                        }
                    }
                    boolean huehue = s.substring(12,13).equals("w") && s.substring(s.length()-1, s.length()).equals(String.valueOf(myId));
                    if(s.substring(12,13).equals("w") && s.substring(s.length()-1, s.length()).equals(String.valueOf(myId))){
                        //you got chosen!!!
                        startRoulette(false);
                    }
                }
            }
        }
    };


    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "Game started");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.question);

        rl = (RelativeLayout) findViewById(R.id.my_rl);
        text = (TextView) findViewById(R.id.textView);
        hourGlass = (ImageView) findViewById(R.id.imageView3);
        notChosen = (ImageView) findViewById(R.id.imageView);
        chosen = (ImageView) findViewById(R.id.imageView2);
        toggle = (ToggleButton)findViewById(R.id.toggleButton);
        //setChosen("THis is your Question?!!");
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

    public void setChosen(String Question) {
        rl.setBackgroundColor(Color.rgb(244, 150, 150));

        notChosen.setVisibility(View.INVISIBLE);
        chosen.setVisibility(View.VISIBLE);
        hourGlass.setVisibility(View.INVISIBLE);
        text.setText("Answer: " + Question);
        toggle.setVisibility(View.VISIBLE);
        toggle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();


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
    }

    public void startRoulette(boolean isMe) {
        if (isMe) {
            runOnUiThread(new Runnable() {
                public void run() {

                    //rl.setBackgroundColor(Color.RED);
                    CountDownTimer start = new CountDownTimer(4000, 250) {
                        boolean alternate = false;

                        @Override
                        public void onTick(long arg0) {
                            if (alternate == true) {
                                rl.setBackgroundColor(Color.rgb(244, 150, 150));
                                alternate = false;
                            } else {
                                rl.setBackgroundColor(Color.BLACK);
                                alternate = true;
                            }
                        }

                        @Override
                        public void onFinish() {
                            String question = myDbHelper.getQuestionByPriority();

                        }
                    }.start();
                }
            });
        }
    }

}
