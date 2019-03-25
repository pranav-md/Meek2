package com.meek.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.meek.Contact;

import java.util.ArrayList;

public class MessageDBHelper extends SQLiteOpenHelper {


    static String DATABASE_NAME="MeekDB.db";
    static String TABLE_NAME="MessagesDB";
    static String MSG_ID="MSG_ID";
    static String MSG_NUM="MSG_NUM";
    static String SENDER_ID="SENDER_ID";
    static String DATE="DATE";
    static String TEXT="TEXT";


    public static final String CREATE_TABLE =
            "CREATE TABLE "+TABLE_NAME+" ("
                    +MSG_ID+" TEXT,"+
                    MSG_NUM+ " INTEGER PRIMARY KEY," +
                    SENDER_ID+ " TEXT,"+
                    TEXT+ " TEXT,"+
                    DATE+ " TEXT)";

    public MessageDBHelper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.e("ONCREATEE","The table is created");
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
        values.put(TEXT,text);
        values.put(DATE,date);
        // insert row
        long id = db.insert(TABLE_NAME, null, values);
        Log.e("INSERT PERSON",msg_id+"_value is written with id="+sender_id);
        // close db connection
        db.close();
    }


    //////////RETREIVAL
    public Cursor getMessages(String msg_id)
    {
        String query = "SELECT "+SENDER_ID+" , " + TEXT+ " , " + DATE+ " FROM "+ TABLE_NAME  +" WHERE "+ MSG_ID+ " =?" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{msg_id});
        int con_status=cursor.getInt(0);
        return cursor;
    }

    long numberOfMessages()
    {
        String query = "SELECT * FROM "+ TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{});
        return cursor.getCount();
    }
    public Cursor getMessageDialogs()
    {
        String query = "SELECT "+MSG_ID+" , " +SENDER_ID+" , " + TEXT+ " , " + DATE+ " FROM "+ TABLE_NAME  +" GROUP BY "+ MSG_ID+" HAVING MAX("+MSG_NUM+")" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{});
        int con_status=cursor.getInt(0);
        return cursor;
    }



    public Cursor getLocationPplData()
    {
        String query = "SELECT " + UID+" , " + NME+" , " + LT+ " , " + LG+ " FROM "+ TABLE_NAME  +" WHERE "+ CON_LEVEL+ " =?" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{"2"});
        return cursor;
    }

    public void changePersonStatus(String uid,int con_level)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CON_LEVEL, con_level);
        String whereClause = UID+"=?";
        String whereArgs[] = {uid};
        int update= db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
        Log.e("changePersonStatus","UID="+uid+"  CON_LEVEL="+con_level+"  update stat="+update);
        db.close();
    }
    public boolean checkUID(String uid,int status)
    {
        String query = "SELECT " + UID + " FROM "+ TABLE_NAME +" WHERE "+ UID+ " =? AND "+CON_LEVEL+"=?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{uid,status+""});
        Log.e("checkUID","UID="+uid+"   the cursor count="+cursor.getCount());

        if (cursor.getCount() > 0)
        {
            return true;
        }
        else
            return false;
    }
    public boolean checkUID(String uid)
    {

        String query = "SELECT " + UID + " FROM "+ TABLE_NAME +" WHERE "+ UID+ " =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{uid});
        Log.e("checkUID","UID="+uid+"   the cursor count="+cursor.getCount());
        if (cursor.getCount() > 0)
        {
            return true;
        }
        else
            return false;
    }

    public void updateLatLng(LatLng latLng, String uid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        PeopleObj pOBj=getNote(uid);
        //values.put(UID, uid);
        values.put(LT,latLng.latitude);
        values.put(LT,latLng.longitude);

        db.update(TABLE_NAME, values, UID + " = ?",
                new String[]{String.valueOf(uid)});

    }

    public ArrayList<Contact> getAllConnections()
    {
        String query = "SELECT " + UID+" , " + NME+" , " + CON_LEVEL+ " FROM "+ TABLE_NAME ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ArrayList<Contact> conPPL=new ArrayList<Contact>();
        cursor.moveToFirst();
        if(cursor.getCount()!=0)
        {
            conPPL.add(new Contact(cursor.getString(0),cursor.getString(1),Integer.parseInt(cursor.getString(2)+"")));
            while (cursor.moveToNext()) {
                Log.e("GET ALL CONNS", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
                conPPL.add(new Contact(cursor.getString(0), cursor.getString(1), Integer.parseInt(cursor.getString(2) + "")));

            }
        }
        return conPPL;
    }
    public void checkPnumHash(String h_num)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

    /*    String query = "Select * From STUDENTS where name = '"+name+"'";
       if(db.getData(query).getCount()>0){
            Toast.makeText(getApplicationContext(), "Already Exist!", Toast.LENGTH_SHORT).show();
        }else{
            sqLiteHelper.insertData(
                    name,
                    edtPrice.getText().toString().trim(),
                    imageViewToByte(imageView)

            );

        values.put(HSHED_PNM,h_num);
        // insert row
        long id = db.insert(TABLE_NAME, null, values);
        // close db connection
        db.close();
        */
    }

    void updateEncKeyPerson(int uid,String e_key)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        PeopleObj pOBj=getNote(uid+"");
        //values.put(UID, uid);
        values.put(E_KY,e_key);
        //values.put(NME,pOBj.getName());
        //values.put(NME,pOBj.getName());

        db.update(TABLE_NAME, values, UID + " = ?",
                new String[]{String.valueOf(uid)});
    }

    void updateLatLngPerson(int uid,LatLng ltlng)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(LT,ltlng.latitude);
        values.put(LG,ltlng.longitude);

        db.update(TABLE_NAME, values, UID + " = ?",
                new String[]{String.valueOf(uid)});
    }
    public PeopleObj getNote(String id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(PeopleDBHelper.TABLE_NAME,
                new String[]{UID,NME,E_KY,HSHED_PNM,LT,LG, },
                UID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // prepare note object
        PeopleObj pObj = new PeopleObj(
                cursor.getInt(cursor.getColumnIndex(UID)),
                cursor.getString(cursor.getColumnIndex(NME)),
                cursor.getString(cursor.getColumnIndex(E_KY)),
                cursor.getString(cursor.getColumnIndex(HSHED_PNM)),
                cursor.getDouble(cursor.getColumnIndex(LT)),
                cursor.getDouble(cursor.getColumnIndex(LG)));

        // close the db connection
        cursor.close();

        return pObj;
    }


}