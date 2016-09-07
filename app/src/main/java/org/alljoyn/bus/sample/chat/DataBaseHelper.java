package org.alljoyn.bus.sample.chat;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static String LOG_TAG = "DataBaseHelper";

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/org.alljoyn.bus.sample.chat/databases/";

    private static String DB_NAME = "Questions.sqlite";

    private static String TABLE_NAME = "Questions";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }



    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void getQuestionsCount() {
        String countQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        Log.d(LOG_TAG, "Anzahl Fragen: " + cursor.getCount());
        cursor.close();
        // return count
        //return cursor.getCount();
    }

    public List<String> getQuestions(){
        List<String> questions = new ArrayList<String>();
        List<String> total = new ArrayList<String>();

        String priorityQuery = "SELECT Questions FROM " + TABLE_NAME + " ORDER BY Priority DESC LIMIT 3";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(priorityQuery, null);

        if (cursor.moveToFirst()) {
            do {
                questions.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        String totalQuery = "SELECT Questions FROM " + TABLE_NAME;
        Cursor cursor1 = db.rawQuery(totalQuery, null);

        if (cursor1.moveToFirst()) {
            do {
                total.add(cursor1.getString(0));
            } while (cursor1.moveToNext());
        }

        Random rand = new Random();
        int x = rand.nextInt(total.size());
        int y = rand.nextInt(total.size());

        questions.add(total.get(x));
        questions.add(total.get(y));

        return questions;
    }

    public String escapeChar(String s){
        for(int i = 0; i<s.length(); i++) {
            //hack hack :D
            char c = s.charAt(i);
            String temp = String.valueOf(c);
            if (temp.equals("'")) {
                s = s.substring(0, i) + "'" + s.substring(i, s.length());
            }
        }
        return s;
    }

    public void addQuestion(String s){
        s = escapeChar(s);
        String countQuery = "INSERT INTO " + TABLE_NAME + " VALUES ( null,  \"" + s +  "\", 1, 2, 0)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        Log.d(LOG_TAG, "Added question " + s);
        cursor.close();
    }

    public String getQuestionByPriority(){
        String priorityQuery = "SELECT Questions FROM " + TABLE_NAME + " ORDER BY Priority DESC LIMIT 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(priorityQuery, null);
        cursor.moveToFirst();
        String question = cursor.getString(0);
        cursor.close();
        return question;
    }

    public int getPriority(String s){
        s = escapeChar(s);
        String priorityQuery = "Select Priority FROM " + TABLE_NAME + " WHERE Questions = " +  "\'" + s + "\'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(priorityQuery, null);
        cursor.moveToFirst();
        int priority = cursor.getInt(0);
        cursor.close();
        return priority;
    }

    public void updatePriority(String s){
        int newPriority = getPriority(s) + 50;
        s = escapeChar(s);
        String priorityQuery = "UPDATE Questions SET priority=" + newPriority +" WHERE Questions = " + "\'" + s + "\'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(priorityQuery, null);
        cursor.close();

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

}