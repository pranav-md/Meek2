package com.meek.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by User on 08-Jan-19.
 */

public class PeopleDBHelper extends SQLiteOpenHelper {



    static String TABLE_NAME="PeopleDB";
    static String UID="UID";
    static String NME="NAME";
    static String E_KY="ENC_KEY";
    static String HSHED_PNM="HASHED_PHNM";
    static String LT="LAT";
    static String LG="LNG";
    static String LOC_AC="LOCATION_ACCESS";


    public static final String CREATE_TABLE =
            "CREATE TABLE "+TABLE_NAME+" ("+UID+"INTEGER PRIMARY KEY,"+
                     NME+ " TEXT,"+
                     E_KY+ " TEXT"+
                     HSHED_PNM+"TEXT PRIMARY KEY,"+
                     LT+ " DOUBLE,"+
                     LG+ " DOUBLE"+
                     LOC_AC+ " INTEGER"+")";

    public PeopleDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
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

    void insertPerson(int uid,String h_num)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UID, uid);
        values.put(HSHED_PNM,h_num);
        // insert row
        long id = db.insert(TABLE_NAME, null, values);
        // close db connection
        db.close();
    }

    void updateEncKeyPerson(int uid,String e_key)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        PeopleObj pOBj=getNote(uid);
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
    public PeopleObj getNote(long id) {
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
                cursor.getDouble(cursor.getColumnIndex(LG)),
                cursor.getInt(cursor.getColumnIndex(LOC_AC)));

        // close the db connection
        cursor.close();

        return pObj;
    }


}
