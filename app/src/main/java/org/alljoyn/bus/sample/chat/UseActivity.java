/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package org.alljoyn.bus.sample.chat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import android.app.Activity;
import android.app.Dialog;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.util.Log;
import android.widget.ViewFlipper;

import java.util.List;

public class UseActivity extends Activity implements Observer {
    private static final String TAG = "chat.UseActivity";
    //for flashing
    private static View myView = null;

    private boolean isHost = false;

    DataBaseHelper myDbHelper = new DataBaseHelper(this);

    public static final int DIALOG_JOIN_ID = 0;
    public static final int DIALOG_LEAVE_ID = 1;
    public static final int DIALOG_ALLJOYN_ERROR_ID = 2;
    static final int DIALOG_SET_NAME_ID = 3;
    static final int DIALOG_STOP_ID = 4;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.use);

        myView = (View) findViewById(R.id.my_view);

        mHistoryList = new ArrayAdapter<String>(this, android.R.layout.test_list_item);
        //ListView hlv = (ListView) findViewById(R.id.useHistoryList);
       // hlv.setAdapter(mHistoryList);

        //get Host
        try {
            isHost = HostActivity.getActivityInstance().getHost();
            } catch (Exception e) {
                e.printStackTrace();
            }

        EditText messageBox = (EditText) findViewById(R.id.useMessage);
        messageBox.setSingleLine();
        messageBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String message = view.getText().toString();
                    myDbHelper.addQuestion(message);
                    Log.i(TAG, message);
                    mChatApplication.newLocalUserMessage(message + "cq");
                    //myDbHelper.updatePriority(message);
                    view.setText("");
                    Intent intent = new Intent(UseActivity.this, GameActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        //showSoftKeyboard(myView);
        LinearLayout layout = (LinearLayout) findViewById(R.id.button_view);
        List<String> questions = myDbHelper.getQuestions();
        Button button;

        int id = 0;
        for (String s : questions) {
            button = new Button(this);
            button.setText(s);
            button.setId(id);
            //WindowManager.LayoutParams lParams = new WindowManager.LayoutParams(0, WindowManager.LayoutParams.WRAP_CONTENT);
            //button.setLayoutParams(lParams);
            id++;
            layout.addView(button, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            button.setOnClickListener(new MyOnClickListener(String.valueOf(button.getText()), UseActivity.this){
                @Override
                public void onClick(View arg0) {

                    myDbHelper.updatePriority(super.s);
                    mChatApplication.newLocalUserMessage(super.s);
                    Intent intent = new Intent(UseActivity.this, GameActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            /*button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    mChatApplication.newLocalUserMessage("q: " + "sflfsdlf");
                    mChatApplication.newLocalUserMessage("1");
                    Intent intent = new Intent(UseActivity.this, GameActivity.class);
                    startActivity(intent);
                }
            });*/
        }


        mJoinButton = (Button) findViewById(R.id.useJoin);


        mLeaveButton = (Button) findViewById(R.id.useLeave);

       // mChannelName = (TextView) findViewById(R.id.useChannelName);
       // mChannelStatus = (TextView) findViewById(R.id.useChannelStatus);

        /*
         * Keep a pointer to the Android Appliation class around.  We use this
         * as the Model for our MVC-based application.    Whenever we are started
         * we need to "check in" with the application so it can ensure that our
         * required services are running.
         */
        mChatApplication = (ChatApplication) getApplication();
        mChatApplication.checkin();

        /*
         * Call down into the model to get its current state.  Since the model
         * outlives its Activities, this may actually be a lot of state and not
         * just empty.
         */
        updateChannelState();
        updateHistory();

        /*
         * Now that we're all ready to go, we are ready to accept notifications
         * from other components.
         */
        mChatApplication.addObserver(this);

    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        mChatApplication = (ChatApplication) getApplication();
        mChatApplication.deleteObserver(this);
        super.onDestroy();
    }

    protected Dialog onCreateDialog(int id) {
        Log.i(TAG, "onCreateDialog()");
        Dialog result = null;
        switch (id) {
            case DIALOG_JOIN_ID: {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createUseJoinDialog(this, mChatApplication);
            }
            break;
            case DIALOG_LEAVE_ID: {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createUseLeaveDialog(this, mChatApplication);
            }
            break;
            case DIALOG_ALLJOYN_ERROR_ID: {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createAllJoynErrorDialog(this, mChatApplication);
            }
            break;
            case DIALOG_SET_NAME_ID: {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createHostNameDialog(this, mChatApplication);
                }
            break;
            case DIALOG_STOP_ID: {
                DialogBuilder builder = new DialogBuilder();
                result = builder.createHostStopDialog(this, mChatApplication);
                }
            break;

        }
        return result;
    }

    public synchronized void update(Observable o, Object arg) {
        Log.i(TAG, "update(" + arg + ")");
        String qualifier = (String) arg;

        if (qualifier.equals(ChatApplication.APPLICATION_QUIT_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_APPLICATION_QUIT_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.HISTORY_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_HISTORY_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.USE_CHANNEL_STATE_CHANGED_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_CHANNEL_STATE_CHANGED_EVENT);
            mHandler.sendMessage(message);
        }

        if (qualifier.equals(ChatApplication.ALLJOYN_ERROR_EVENT)) {
            Message message = mHandler.obtainMessage(HANDLE_ALLJOYN_ERROR_EVENT);
            mHandler.sendMessage(message);
        }
    }


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    private void updateHistory() {
        Log.i(TAG, "updateHistory()");
        mHistoryList.clear();
        List<String> messages = mChatApplication.getHistory();
        for (String message : messages) {
            mHistoryList.add(message);
        }
        mHistoryList.notifyDataSetChanged();
    }

    private void updateChannelState() {
        Log.i(TAG, "updateHistory()");
        AllJoynService.UseChannelState channelState = mChatApplication.useGetChannelState();
        String name = mChatApplication.useGetChannelName();
        if (name == null) {
            name = "Not set";
        }
        //mChannelName.setText(name);

        switch (channelState) {
            case IDLE:
                //mChannelStatus.setText("Idle");
                mJoinButton.setEnabled(true);
                mJoinButton.setText("Join Channel");
                mJoinButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        showDialog(DIALOG_JOIN_ID);
                        //mChatApplication.newLocalUserMessage("r");
                    }
                });
                mLeaveButton.setEnabled(true);
                mLeaveButton.setText("Create Channel");
                mLeaveButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        showDialog(DIALOG_SET_NAME_ID);
                    }
                });
                break;
            case JOINED:
               // mChannelStatus.setText("Joined");
                if(!isHost) {
                    mJoinButton.setEnabled(false);
                }
                else{
                    mJoinButton.setText("Stop Channel");
                    mJoinButton.setOnClickListener(
                            new View.OnClickListener(){
                                public void onClick(View view){
                                    showDialog(DIALOG_STOP_ID);
                                }
                            }
                    );
                }
                mLeaveButton.setEnabled(true);
                mLeaveButton.setText("Leave Channel");
                mLeaveButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        showDialog(DIALOG_LEAVE_ID);
                    }
                });
                break;
        }
    }

    /**
     * An AllJoyn error has happened.  Since this activity pops up first we
     * handle the general errors.  We also handle our own errors.
     */
    private void alljoynError() {
        if (mChatApplication.getErrorModule() == ChatApplication.Module.GENERAL ||
                mChatApplication.getErrorModule() == ChatApplication.Module.USE) {
            showDialog(DIALOG_ALLJOYN_ERROR_ID);
        }
    }

    private static final int HANDLE_APPLICATION_QUIT_EVENT = 0;
    private static final int HANDLE_HISTORY_CHANGED_EVENT = 1;
    private static final int HANDLE_CHANNEL_STATE_CHANGED_EVENT = 2;
    private static final int HANDLE_ALLJOYN_ERROR_EVENT = 3;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_APPLICATION_QUIT_EVENT: {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_APPLICATION_QUIT_EVENT");
                    finish();
                }
                break;
                case HANDLE_HISTORY_CHANGED_EVENT: {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_HISTORY_CHANGED_EVENT");
                    updateHistory();
                    break;
                }
                case HANDLE_CHANNEL_STATE_CHANGED_EVENT: {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_CHANNEL_STATE_CHANGED_EVENT");
                    updateChannelState();
                    break;
                }
                case HANDLE_ALLJOYN_ERROR_EVENT: {
                    Log.i(TAG, "mHandler.handleMessage(): HANDLE_ALLJOYN_ERROR_EVENT");
                    alljoynError();
                    break;
                }
                default:
                    break;
            }
        }
    };

    private ChatApplication mChatApplication = null;

    private ArrayAdapter<String> mHistoryList;

    private Button mJoinButton;
    private Button mLeaveButton;

    @Override
    public void onBackPressed()
    {
        mChatApplication.quit();
    }

    //private TextView mChannelName;

    //private TextView mChannelStatus;
}
