package com.meek.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.meek.Contact;
import com.meek.Encryption.AES;
import com.meek.MapPeople;
import com.meek.Messaging.Message;

import java.util.ArrayList;

public class MessageDBHelper extends SQLiteOpenHelper {


    static String DATABASE_NAME="MeekDB.db";
    static String TABLE_NAME="MessagesDB";
    static String MSG_ID="MSG_ID";
    static String MSG_NUM="MSG_NUM";
    static String SENDER_ID="SENDER_ID";
    static String DATE="DATE";
    static String TEXT="TEXT";
    String serverkey;


    public static final String CREATE_TABLE =
            "CREATE TABLE "+TABLE_NAME+" ("
                    +MSG_ID+" TEXT, "+
                    MSG_NUM+ " INTEGER PRIMARY KEY," +
                    SENDER_ID+ " TEXT,"+
                    TEXT+ " TEXT,"+
                    DATE+ " TEXT)";

    public MessageDBHelper(Context context,String serverkey)
    {
        super(context, DATABASE_NAME, null, 4);
        this.serverkey=serverkey;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.e("ON MESSGE CREATE","The Message table is created");
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1)
    {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Create tables again
        onCreate(db);
    }

    public boolean checkTable()
    {
        SQLiteDatabase mDatabase = this.getWritableDatabase();

        Log.d("CHECK TABLE", TABLE_NAME+" Exist or not check");

        Cursor c = null;
        boolean tableExists = false;
        /* get cursor on it */
        try
        {
            c = mDatabase.query(TABLE_NAME, null,
                    null, null, null, null, null);
            tableExists = true;
        }
        catch (Exception e) {
          /* fail */
            Log.d("TABLE NOT EXISTS", TABLE_NAME+" doesn't exist :(((");
        }

        return tableExists;
    }



    public void createTable()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE);
    }

    /////////////////////// INSERTION
    public void insertMessage(String msg_id,String sender_id,String text,String date)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        long number=numberOfMessages();

        values.put(MSG_ID, msg_id);
        values.put(MSG_NUM, number+1);
        values.put(SENDER_ID,sender_id);
        values.put(TEXT,new AES().encrypt(text,serverkey));
        values.put(DATE,new AES().encrypt(date,serverkey));
        // insert row
        long id = db.insert(TABLE_NAME, null, values);
        Log.e("INSERT PERSON",msg_id+" written with number of="+number);
        // close db connection
        db.close();
    }


    //////////RETREIVAL
    public ArrayList<Message> getMessages(String msg_id)
    {
        String query = "SELECT "+SENDER_ID+" , " + TEXT+ " , " + DATE+ " FROM "+ TABLE_NAME  +" WHERE "+ MSG_ID+ " =?" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{msg_id});
        ArrayList<Message> msgPPL=new ArrayList<Message>();
        cursor.moveToFirst();
        if(cursor.getCount()!=0)
        {
            msgPPL.add(new Message(cursor.getString(0)
                    ,cursor.getString(1)
                    ,new AES().decrypt(cursor.getString(2),serverkey)));
            while (cursor.moveToNext())
            {
                Log.e("GET ALL CONNS", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
                msgPPL.add(new Message(cursor.getString(0)
                        ,cursor.getString(1)
                        ,new AES().decrypt(cursor.getString(2),serverkey)));
            }
        }
        return msgPPL;
    }

    long numberOfMessages()
    {
        String query = "SELECT * FROM "+ TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{});
        return cursor.getCount();
    }

    public ArrayList<Message> getMessageDialogs()
    {
        String query = "SELECT "+MSG_ID+" , " +SENDER_ID+" , " +TEXT+ " FROM "+ TABLE_NAME  +" GROUP BY "+ MSG_ID+" HAVING MAX("+MSG_NUM+")" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor =  db.query(TABLE_NAME, new String[] {MSG_ID,SENDER_ID,TEXT,DATE },null,null,MSG_ID, "MAX("+MSG_NUM+")", null);
        Log.e("getMessageDialogs", "Cursor count="+cursor.getCount());

        ArrayList<Message> msgPPL=new ArrayList<Message>();
        cursor.moveToFirst();
        if(cursor.getCount()!=0)
        {
            Message newone=new Message(cursor.getString(0));
            newone.setMsgDlg(cursor.getString(1),cursor.getString(2));
            msgPPL.add(newone);
            while (cursor.moveToNext())
            {
                Log.e("GET ALL CONNS", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
                newone=new Message(cursor.getString(0));
                newone.setMsgDlg(cursor.getString(1),cursor.getString(2));
                msgPPL.add(newone);
            }
        }

        return msgPPL;
    }
///////////DELETION

    public void deletionMSGS(String msg_id)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, MSG_ID + "=" + msg_id, null);
        db.close();
    }

}