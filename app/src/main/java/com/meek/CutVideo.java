package com.meek;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import java.io.File;
import java.net.URISyntaxException;

import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.github.tcking.giraffecompressor.GiraffeCompressor;
import com.google.android.gms.nearby.connection.Payload;
import com.iceteck.silicompressorr.SiliCompressor;
//import com.iceteck.silicompressorr.SiliCompressor;
//import com.iceteck.silicompressorr.videocompression.MediaController;

import life.knowledge4.videotrimmer.K4LVideoTrimmer;
import life.knowledge4.videotrimmer.interfaces.OnTrimVideoListener;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by User on 11-Jul-18.
 */

public class CutVideo extends AppCompatActivity {

    int left=-1,right=-1,cse;
    String filepath;
    ProgressDialog progress;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cut_video);
        Bundle extras=getIntent().getExtras();
        setVideo(extras.getString("PATH"));
        cse=extras.getInt("CASE");
        if(cse==2)
        {
            left = extras.getInt("LEFT", -1);
            right = extras.getInt("right", -1);
        }
    }
    void setVideo(final String path) {
        final K4LVideoTrimmer videoTrimmer = ((K4LVideoTrimmer) findViewById(R.id.timeLine));
        if (videoTrimmer != null) {
            videoTrimmer.setVideoURI(Uri.parse(path));
        }
        videoTrimmer.setMaxDuration(20);
        if (left != -1)
        {
            videoTrimmer.setLeft(left);
            videoTrimmer.setRight(right);
        }
        videoTrimmer.setOnTrimVideoListener(new OnTrimVideoListener() {
            @Override
            public void getResult(Uri uri) {
                int left=videoTrimmer.getLeft();
                int right=videoTrimmer.getRight();

                File from=new File(uri.toString());
                File to =new File(path.replace("activity","trim_uncom_activity"));
                File compress =new File(path.replace("activity","trim_activity"));
                from.renameTo(to);
                filepath=to.getPath();
                Intent intent=new Intent();
                intent.putExtra("RESULT",1);
                intent.putExtra("LEFT",left);
                intent.putExtra("RIGHT",right);
                intent.putExtra("CASE",cse);
                setResult(2,intent);

                new VideoCompressAsyncTask(CutVideo.this).execute(to.getPath(), to.getPath().replace("/trim_uncom_activity.mp4",""));


            }

            @Override
            public void cancelAction() {
                Intent intent=new Intent();
                intent.putExtra("RESULT",0);
                intent.putExtra("LEFT",left);
                intent.putExtra("RIGHT",right);
                intent.putExtra("CASE",cse);
            }
        });
    }
    void stopThisThing()
    {
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class VideoCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;
        ProgressDialog p_dialog;


        public VideoCompressAsyncTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            p_dialog = new ProgressDialog(mContext);
            p_dialog.setMessage("Its loading....");
            p_dialog.show();
            p_dialog.setCancelable(false);

        }

        @Override
        protected String doInBackground(String... paths) {
            String filePath = null;
            try {

                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return filePath;

        }


        @Override
        protected void onPostExecute(String compressedFilePath) {
            super.onPostExecute(compressedFilePath);
            File videoFile = new File(compressedFilePath);
            float length = videoFile.length() / 1024f; // Size in KB
            String value;
            if (length >= 1024)
                value = length / 1024f + " MB";
            else
                value = length + " KB";
            videoFile.renameTo(new File(compressedFilePath.replace(compressedFilePath.substring(compressedFilePath.indexOf("VIDEO"),compressedFilePath.indexOf(".mp4")),"ACTIVITY")));
            p_dialog.dismiss();
            Log.i("Silicompressor", "Path: " + compressedFilePath);
        }
    }
}
