package com.meek.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.meek.MyActivities;
import com.meek.MyDetails;
import com.meek.R;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;

import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by User on 30-May-18.
 */

public class MyProfileFrag extends AppCompatActivity {

    CircleImageView dp;
    String uid;
    ImagePicker imagePicker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile);
        CardView my_activities=(CardView)findViewById(R.id.activities);
        SharedPreferences mypref = getSharedPreferences("UserDetails", MODE_PRIVATE);
        uid=mypref.getString("uid","");
        setNamePlace();
        dp=(CircleImageView)findViewById(R.id.my_prof_dp);
        String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/DisplayPic";
        String localFilename = sFolder + "/dp.jpg";
        Bitmap dp_bm= BitmapFactory.decodeFile(localFilename);
        dp.setImageBitmap(dp_bm);


        my_activities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileFrag.this,MyActivities.class));
            }
        });

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker=new ImagePicker(MyProfileFrag.this, null, new OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri imageUri) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(MyProfileFrag.this.getContentResolver(), imageUri);
                            final Bitmap dp_bp=bitmap;
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 30, out);
                            final byte[] byteArray = out.toByteArray();
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference userRef = database.getReference("Users");
                            /////
                            userRef.child(uid).child("dpno").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot)
                                {
                                    final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                                    storageReference.child("Users DP/"+uid+"_"+(Integer.parseInt(dataSnapshot.getValue().toString())+1)+".jpg").putBytes(byteArray).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            saveImage(byteArray);
                                            dp.setImageBitmap(dp_bp);
                                            userRef.child(uid).child("dpno").setValue(Integer.parseInt(dataSnapshot.getValue().toString())+1);

                                            StorageReference  desertRef = storageReference.child("Users DP/"+uid+"_"+(Integer.parseInt(dataSnapshot.getValue().toString())));

                                            desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    // File deleted successfully
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Uh-oh, an error occurred!
                                                }
                                            });


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                        }
                                    });


                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            /////
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).setWithImageCrop(1,1);
                imagePicker.choosePicture(false /*show camera intents*/);

            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePicker.handleActivityResult(resultCode, requestCode, data);
        if(requestCode==200&&resultCode==RESULT_OK&&data!=null)
        {
            beginCrop(data.getData());
        }
        else if(requestCode==203)
        {
            try
            {
                 croppedImage();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        imagePicker.handlePermission(requestCode, grantResults);
    }
    private void beginCrop(Uri source)
    {
        Uri dest=Uri.fromFile(new File(getCacheDir(),"Cropped"));

        //imagePicker.(source,dest).asSquare().start(this);
    }
    void croppedImage() throws IOException {
        Toast.makeText(MyProfileFrag.this,"DPP",Toast.LENGTH_LONG).show();
        File myFile = imagePicker.getImageFile();
        final Uri selectedImage=getImageContentUri(getApplicationContext(),myFile);
        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }}
        void saveImage(byte[] imageData)
        {
            String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/DisplayPic";
            String localFilename = sFolder + "/dp.jpg";
            new File(sFolder).mkdirs();
            ///deleting if already another exists
            File fdelete = new File(localFilename);
            if (fdelete.exists()) {
                if (fdelete.delete()) {
                    System.out.println("file Deleted :");
                } else {
                    System.out.println("file not Deleted :");
                }
            }
            try {
                File img = new File(localFilename);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(img));
                //InputStream in = getContentResolver().openInputStream(uri);
                int bytesread;
                //byte[] imageData = new byte[1024];
                    out.write(imageData);
                //in.close();
                out.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void setNamePlace()
        {

            SharedPreferences pref=getApplicationContext().getSharedPreferences("UserDetails",MODE_PRIVATE);
            TextView tvname=(TextView)findViewById(R.id.name);
            TextView location=(TextView)findViewById(R.id.location);

            tvname.setText(pref.getString("Name",""));
            location.setText(pref.getString("place",""));

        }

}
