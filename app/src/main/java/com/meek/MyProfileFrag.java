package com.meek;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.ActionBar;
import android.widget.Button;
import android.widget.Toast;

import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;


/**
 * Created by User on 30-May-18.
 */

public class MyProfileFrag extends AppCompatActivity {

    CircleImageView dp;
    ImagePicker imagePicker;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile);
        Button dpchange=(Button)findViewById(R.id.dpchange);
        CardView my_places=(CardView)findViewById(R.id.places);
        CardView my_activities=(CardView)findViewById(R.id.activities);
        my_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MyProfileFrag.this,MyPlaces.class));
            }
        });
        dp=(CircleImageView)findViewById(R.id.my_prof_dp);
        dpchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker=new ImagePicker(MyProfileFrag.this, null, new OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri imageUri) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(MyProfileFrag.this.getContentResolver(), imageUri);
                            dp.setImageBitmap(bitmap);
                            saveImage(imageUri);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).setWithImageCrop(1,1);
                imagePicker.choosePicture(true /*show camera intents*/);

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
        Realm.init(getApplicationContext());
        final Realm myRealm= Realm.getDefaultInstance();
        myRealm.executeTransaction(new Realm.Transaction()
        {
            @Override
            public void execute(Realm realm) {
                MyDetails myDetails;
                if(myRealm.where(MyDetails.class).findAll().size()==0)
                {
                    myDetails=myRealm.createObject(MyDetails.class);
                }
                else
                {
                    myDetails=myRealm.where(MyDetails.class).findFirst();
                }
                myDetails.my_dp_uri=selectedImage.toString();
            }
        });
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
        void saveImage(Uri uri)
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
                InputStream in = getContentResolver().openInputStream(uri);
                int bytesread;
                byte[] imageData = new byte[1024];
                while ((bytesread = in.read(imageData)) > 0) {
                    out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesread)));
                }
                in.close();
                out.close();


            } catch (IOException e) {
                e.printStackTrace();
            }


        }
}
