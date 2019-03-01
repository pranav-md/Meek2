package com.meek.Service;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by User on 17-Jun-18.
 */

public class DpDownloadService extends Service {
    String dp_dest;

    @Override
    public void onCreate()
    {
        super.onCreate();
        dp_dest = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();


        //downloadDPs();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
/*
    void downloadDPs()
    {
        Realm.init(this);
        final Realm myRealm= Realm.getDefaultInstance();

        RealmResults<Contact> all_uid_guys=myRealm.where(Contact.class).notEqualTo("uid","0").findAll();
        for(final Contact con:all_uid_guys)
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final DatabaseReference userRef = database.getReference("Users");
            final String f_id=con.getUid();
            userRef.child(con.getUid()).child("dpno").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!checkDpPresent(f_id+"_"+dataSnapshot.getValue().toString()))
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
                                myRealm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        com.meek.Contact contact=realm.where(com.meek.Contact.class).equalTo("uid",dpno).findFirst();
                                        contact.setDpno(dpno);
                                    }
                                });
                                File new_dp=new File(dp_dest+"/"+f_id+"_"+dpno+".jpg");
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
                                for(int i=Integer.parseInt(dpno);i>0;i--)
                                {
                                    if((new File(dp_dest+"/"+f_id+"_"+i+".jpg")).exists())
                                    {
                                        (new File(dp_dest+"/"+f_id+"_"+i+".jpg")).delete();
                                        break;
                                    }
                                    else
                                        continue;
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
    boolean checkDpPresent(String name)
    {
        String storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File image = new File(storageDir, "/UsersDp/"+name+".jpg");
        if(image.exists())
            return true;
        else
            return false;
    }*/
}
