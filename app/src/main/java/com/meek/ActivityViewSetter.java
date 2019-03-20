package com.meek;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.meek.Encryption.AES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class ActivityViewSetter {
    Context context;
    public ActivityViewSetter(Context context)
    {
        this.context=context;
    }
    public void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    void setVideoView(View set_view)
    {
        set_view.findViewById(R.id.img_view).setVisibility(View.INVISIBLE);
        set_view.findViewById(R.id.text_view).setVisibility(View.INVISIBLE);

    }
    void setImageView(View set_view)
    {
        set_view.findViewById(R.id.vid_view).setVisibility(View.INVISIBLE);
        set_view.findViewById(R.id.text_view).setVisibility(View.INVISIBLE);
    }
    public void setTextView(View set_view)
    {
        set_view.findViewById(R.id.vid_view).setVisibility(View.INVISIBLE);
        set_view.findViewById(R.id.img_view).setVisibility(View.INVISIBLE);
    }

    void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void decryptAndSet(String storageDir,String filename,String extension,View set_view)
    {
        AES decrypt=new AES();
        File actFile = new  File(storageDir+"/"+filename+extension);

        if(extension.equals(".png"))
        {
            decrypt.decryptActivityImage("pmdrox",storageDir,filename);

            if(actFile.exists())
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(actFile.getAbsolutePath());
                ImageView act_img = (ImageView) set_view.findViewById(R.id.img_view);
                act_img.setImageBitmap(myBitmap);
            }
        }
        else
        {
            decrypt.decryptActivityVideo("pmdrox",storageDir,filename);
            VideoView act_vid = (VideoView)set_view.findViewById(R.id.vid_view);
            act_vid.setVideoPath(actFile.getPath());
        }
    }

    public void fileDownload(String u_id, String act_id, final String act_type, final View set_view, final ShimmerLayout shimmerLayout)
    {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final String file_name=u_id+"_"+act_id+".crypt";
        storageRef=storageRef.child("Activity/"+file_name);
        try {
            final File localFile = File.createTempFile(file_name, "crypt");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                {
                    shimmerLayout.stopShimmerAnimation();
                    String storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();
                    copyFileOrDirectory(localFile.getAbsolutePath(),storageDir);
                    if(act_type.equals("1"))
                    {
                        setVideoView(set_view);
                        decryptAndSet(storageDir,file_name,".mp4",set_view);
                    }
                    else
                    {
                        setImageView(set_view);
                        decryptAndSet(storageDir,file_name,".png",set_view);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
