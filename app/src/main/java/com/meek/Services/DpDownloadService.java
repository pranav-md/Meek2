package com.meek.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.meek.Contact;
import com.meek.Database.PeopleDBHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by User on 17-Jun-18.
 */

public class DpDownloadService extends Service {
    String dp_dest;
    String mCurrentPhotoPath;
    String serverkey;


    @Override
    public void onCreate()
    {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serverkey = intent.getStringExtra("username");
        dp_dest = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        downloadDPs();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    void downloadDPs()
    {
        ArrayList<Contact> all_uid_guys=new PeopleDBHelper(this,"server key to be set").getAllConnections();

        for(final Contact con:all_uid_guys)
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference userRef = database.getReference("Users");
            final String f_id=con.getUID();
            userRef.child(con.getUID()).child("dpno").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!checkDpPresent(dataSnapshot.getValue().toString(),f_id))
                    {
                        File localFile = new File("");
                        final String dpno= dataSnapshot.getValue().toString();
                        try {
                            localFile = File.createTempFile("images", "jpg");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        final File fin_loc_file=localFile;
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageReference = storage.getReference();
                        storageReference.child("Users DP/"+f_id+"_"+dataSnapshot.getValue().toString()+".jpg").getFile(Uri.fromFile(localFile)).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                                new PeopleDBHelper(getBaseContext(),"server key to be set").updateDPNumber(f_id,dpno);
                                if((new File(dp_dest+"/"+f_id+".jpg")).exists())
                                {
                                    (new File(dp_dest+"/"+f_id+".jpg")).delete();

                                }
                                File new_dp=new File(dp_dest+"/"+f_id+".jpg");
                                Bitmap bitmap = BitmapFactory.decodeFile(fin_loc_file.getAbsolutePath());
                                FileOutputStream out = null;
                                try {
                                    out = new FileOutputStream(new_dp);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.flush();
                                    out.close();
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
    boolean checkDpPresent(String dpno,String uid) {
        return new PeopleDBHelper(getBaseContext(),"server key to be set").checkDPNO(dpno, uid);
    }
    private File createImageFile(String ext) throws IOException
    {
        // Create an image file name
        String imageFileName = "display pictures";
        String storageDir = getBaseContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File image = new File(storageDir,
                imageFileName+ /* prefix */
                        ext /* suffix */
                /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

}

