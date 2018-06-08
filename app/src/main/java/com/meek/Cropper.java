package com.meek;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;

/**
 * Created by User on 05-Jun-18.
 */

public class Cropper extends AppCompatActivity {
    String mCurrentPath;
    RelativeLayout editor_container;
    ProgressDialog pb=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cropper);
        editor_container=findViewById(R.id.editor_container);
        String val=getIntent().getExtras().getString("key1");
        mCurrentPath=getIntent().getExtras().getString("key2");
        if(val.equals("pic"))
        {
            try {
                imageCrop();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else
        {
            videoTrim();
        }

    }

    void imageCrop() throws FileNotFoundException {
        final File img_file=new File(mCurrentPath.replace("activity","tmp"));
        final Bitmap img_btp= BitmapFactory.decodeStream(new FileInputStream(img_file));

        final CropImageView cropImageView=(CropImageView)findViewById(R.id.crop_image);
        cropImageView.setImageBitmap(img_btp);
        cropImageView.setAspectRatio(1, 1);

        cropImageView.setFixedAspectRatio(false);

        Button cancel=(Button)findViewById(R.id.cancel);
        Button rotate=(Button)findViewById(R.id.rotate);
        Button done=(Button)findViewById(R.id.done);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cropImageView.rotateImage(90);
            }
        });
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pb=new ProgressDialog(Cropper.this);
                    pb.show();
                    pb.setCancelable(false);
                    FileOutputStream fos1 =  new FileOutputStream(new File(mCurrentPath));
                    Bitmap b=cropImageView.getCroppedImage();
                    b.compress(Bitmap.CompressFormat.PNG, 100, fos1);
                    fos1.close();
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void videoTrim()
    {
        LayoutInflater inflater = LayoutInflater.from(Cropper.this);
        View inflatedLayout= inflater.inflate(R.layout.video_cutter, null, false);
        editor_container.removeAllViews();
        ConstraintLayout constraintLayout=(ConstraintLayout)findViewById(R.id.cons_layout);
        LinearLayout linearLayout=(LinearLayout)findViewById(R.id.lin_btn_lyt);
        constraintLayout.removeView(linearLayout);
        editor_container.addView(inflatedLayout);
        final K4LVideoTrimmer videoTrimmer=(K4LVideoTrimmer)inflatedLayout.findViewById(R.id.video_trim);

        if(videoTrimmer!=null)
        {
            final File file=new File(mCurrentPath.replace("activity","tmp"));
            videoTrimmer.setVideoURI(Uri.fromFile(file));
        }
        videoTrimmer.setMaxDuration(20);

        videoTrimmer.setDestinationPath(mCurrentPath);

        videoTrimmer.setOnTrimVideoListener(new OnTrimVideoListener() {
            @Override
            public void getResult(Uri uri) {
                videoTrimmer.setDestinationPath(mCurrentPath);
                finish();
            }

            @Override
            public void cancelAction() {
                finish();
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pb!=null)
            pb.dismiss();
    }
}
