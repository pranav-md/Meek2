package com.meek.Database;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.Contact;
import com.meek.Encryption.AES;
import com.meek.MainActivity;
import com.meek.MapPeople;

import java.util.ArrayList;

/**
 * Created by User on 08-Jan-19.
 */

public class PeopleDBHelper extends SQLiteOpenHelper {


    static String DATABASE_NAME="MeekDB.db";
    static String TABLE_NAME="PeopleDB";
    static String UID="UID";
    static String NME="NAME";//
    static String E_KY="ENC_KEY";//
    static String HSHED_PNM="HASHED_PHNM";
    static String CON_LEVEL="CON_LEVEL";
    static String LT="LAT";//
    static String DP_NO="DP_NO";
    static String LG="LNG";//
    static String LOC_AC="LOCATION_ACCESS";
    static String name;
    static boolean lock;
    String serverkey;

    public static final String CREATE_TABLE =
            "CREATE TABLE "+TABLE_NAME+" ("+UID+" TEXT PRIMARY KEY,"+
                    NME+ " TEXT,"+
                    E_KY+ " TEXT,"+
                    HSHED_PNM+" TEXT ,"+
                    DP_NO+" TEXT ,"+
                    CON_LEVEL+ " INTEGER,"+
                    LT+ " DOUBLE,"+
                    LG+ " DOUBLE,"+
                    LOC_AC+ " INTEGER"+")";

    public PeopleDBHelper(Context context,String serverkey)
    {
        super(context, DATABASE_NAME, null, 4);
        this.serverkey=serverkey;
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
    public int getConStatus(String uid )
    {
        String query = "SELECT " + CON_LEVEL + " FROM "+ TABLE_NAME +" WHERE "+ UID+ " =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{uid});
        int con_status=cursor.getInt(0);
        return con_status;
    }

    public void insertPerson(String uid,String name,String h_num,int con_level)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UID, uid);
        values.put(NME, new AES().encrypt(name,serverkey));
        values.put(HSHED_PNM,h_num);
        values.put(CON_LEVEL,con_level);
        // insert row
        long id = db.insert(TABLE_NAME, null, values);
        Log.e("INSERT PERSON",uid+"_value is written with id="+id);
        // close db connection
        db.close();
    }

    public ArrayList<MapPeople> getLocationPplData()
    {
        String query = "SELECT " + UID+" , " + NME+" , " + LT+ " , " + LG+ " FROM "+ TABLE_NAME  +" WHERE "+ CON_LEVEL+ " =?" ;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{"2"});
        ArrayList<MapPeople> mapPPL=new ArrayList<MapPeople>();
        cursor.moveToFirst();
        if(cursor.getCount()!=0)
        {
            mapPPL.add(new MapPeople(cursor.getString(0)
                    ,new AES().decrypt(cursor.getString(1),serverkey)
                    ,new LatLng(Double.parseDouble(new AES().decrypt(cursor.getString(2),serverkey)),
                                Double.parseDouble(new AES().decrypt(cursor.getString(3),serverkey)))));
            while (cursor.moveToNext())
            {
                Log.e("GET ALL CONNS", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
                mapPPL.add(new MapPeople(cursor.getString(0)
                        ,new AES().decrypt(cursor.getString(1),serverkey)
                        ,new LatLng(Double.parseDouble(new AES().decrypt(cursor.getString(2),serverkey)),
                        Double.parseDouble(new AES().decrypt(cursor.getString(3),serverkey)))));
            }
        }
        return mapPPL;
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

    public boolean checkDPNO(String dpno,String uid)
    {

        String query = "SELECT " + DP_NO + " FROM "+ TABLE_NAME +" WHERE "+ UID+ " =?  AND " +DP_NO+" =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{uid,dpno});
        Log.e("checkDP","UID="+uid+"   the cursor count="+cursor.getCount());
        if (cursor.getCount() > 0)
        {
            return true;
        }
        else
            return false;
    }

    public void updateDPNumber(String uid,String dp_no)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DP_NO, dp_no);
        String whereClause = UID+"=?";
        String whereArgs[] = {uid};
        int update= db.update(TABLE_NAME, contentValues, whereClause, whereArgs);
        Log.e("changePersonStatus","UID="+uid+"  DPNO="+DP_NO+"  update stat="+update);
        db.close();
    }

    public String getName(String id)
    {
        String query = "SELECT " + NME + " FROM "+ TABLE_NAME +" WHERE "+ UID+ " =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{id});
        Log.e("checkUID","UID="+id+"   the cursor count="+cursor.getCount());
        String name="";
        try {
            cursor.moveToFirst();
            name=cursor.getString(0);
        }
        catch (CursorIndexOutOfBoundsException ex){
            name="";
        }
        return new AES().decrypt(name,serverkey);
    }
    public String getEncKey(String id)
    {
        String query = "SELECT " + E_KY + " FROM "+ TABLE_NAME +" WHERE "+ UID+ " =?";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{id});
        Log.e("checkUID","UID="+id+"   the cursor count="+cursor.getCount());
        String enckey="";
        try {
            cursor.moveToFirst();
            name=cursor.getString(0);
        }
        catch (CursorIndexOutOfBoundsException ex){
            name="";
        }
        return new AES().decrypt(name,serverkey);
    }


    public void updateName(String name, String uid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(getNote(uid+"")==null)
            return;
        PeopleObj pOBj=getNote(uid);
        //values.put(UID, uid);
        values.put(NME,new AES().encrypt(name,serverkey));

        db.update(TABLE_NAME, values, UID + " = ?",
                new String[]{String.valueOf(uid)});

    }



    public void updateLatLng(LatLng latLng, String uid)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(getNote(uid+"")==null)
            return;
        PeopleObj pOBj=getNote(uid);
        //values.put(UID, uid);
        values.put(LT,new AES().encrypt(latLng.latitude+"",serverkey));
        values.put(LG,new AES().encrypt(latLng.longitude+"",serverkey));

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
            conPPL.add(new Contact(cursor.getString(0),new AES().decrypt(cursor.getString(1),serverkey),Integer.parseInt(cursor.getString(2)+"")));
            while (cursor.moveToNext()) {
                Log.e("GET ALL CONNS", cursor.getString(0) + ", " + cursor.getString(1) + ", " + cursor.getString(2));
                conPPL.add(new Contact(cursor.getString(0), new AES().decrypt(cursor.getString(1),serverkey) , Integer.parseInt(cursor.getString(2) + "")));

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
    public String userName(final String id)
    {
        lock=false;
        name="";
        final DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
        ppl_ref.child("Users").child(id).child("Details2").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name =dataSnapshot.getValue().toString();
                lock =true;
                updateName(name,id);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        while(lock==false);
        return name;

    }


    public  void updateEncKeyPerson(String uid,String e_key)
    {
        // get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(getNote(uid+"")==null)
            return;
       // PeopleObj pOBj=getNote(uid+"");
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
        if(cursor.getCount()!=0) {
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
        else {
            return null;
        }
    }


}