package com.meek;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.meek.Database.PeopleDBHelper;
import com.meek.Encryption.AES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import io.supercharge.shimmerlayout.ShimmerLayout;


public class ActivityViewSetter {
    Context context;
    String id;
    String serverkey;
    public ActivityViewSetter(Context context,String id,String serverkey)

    {
        this.id=id;
        this.context=context;
        this.serverkey=serverkey;
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

    public void setVideoView(View set_view)
    {
        set_view.findViewById(R.id.img_view).setVisibility(View.INVISIBLE);
        set_view.findViewById(R.id.text_view).setVisibility(View.INVISIBLE);

    }
    public void setImageView(View set_view)
    {
        set_view.findViewById(R.id.vid_view).setVisibility(View.INVISIBLE);
        set_view.findViewById(R.id.text_view).setVisibility(View.INVISIBLE);
    }
    public void setTextView(View set_view)
    {
        set_view.findViewById(R.id.vid_view).setVisibility(View.INVISIBLE);
        set_view.findViewById(R.id.img_view).setVisibility(View.INVISIBLE);
    }
////////////////////
public static void copy(File src, File dst) throws IOException {
    InputStream in = new FileInputStream(src);
    try {
        OutputStream out = new FileOutputStream(dst);
        try {
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            out.close();
        }
    } finally {
        in.close();
    }
}

//////////////////////
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
        File actFile = new  File(storageDir+"/"+filename.replace(".crypt",extension));
        String enckey=new PeopleDBHelper(context,serverkey).getEncKey(id);
        if(extension.equals(".png"))
        {
            decrypt.decryptActivityImage(enckey,storageDir,filename.replace(".crypt",""));

            if(actFile.exists())
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(actFile.getAbsolutePath());

                ImageView act_img = (ImageView) set_view.findViewById(R.id.img_view);
                act_img.setImageBitmap(myBitmap);
            }
        }
        else
        {
            decrypt.decryptActivityVideo("pmdrox",storageDir,filename.replace(".crypt",""));
            VideoView act_vid = (VideoView)set_view.findViewById(R.id.vid_view);
            act_vid.setVideoPath(actFile.getPath());
        }
        Log.e("ABSOLUTE PATH","ABS PATH IS"+actFile.getAbsolutePath());
    }
    void copyTheFile(File src, File dst) throws IOException {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }
    public void fileDownload(String u_id, String act_id, final String act_type, final View set_view)
    {
        set_view.findViewById(R.id.progressView).setVisibility(View.VISIBLE);
        final ProgressBar download_stat=(ProgressBar)set_view.findViewById(R.id.downloadBar);
        download_stat.setMax(100);
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final String file_name=u_id+"_"+act_id+".crypt";
        if(act_type.equals("1"))
            setVideoView(set_view);
        else
            setImageView(set_view);

        storageRef=storageRef.child("Activity/"+file_name);
        try {
            final File localFile = File.createTempFile(file_name, "crypt");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot)
                {
                 //   shimmerLayout.stopShimmerAnimation();
                    download_stat.setProgress((int) ((taskSnapshot.getBytesTransferred()*100)/taskSnapshot.getTotalByteCount()));
                    String storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString();

                    try {
                        set_view.findViewById(R.id.progressView).setVisibility(View.INVISIBLE);
                        copy(new File(localFile.getAbsolutePath()),new File(storageDir+"/"+file_name));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(act_type.equals("1"))
                    {
                        decryptAndSet(storageDir,file_name,".mp4",set_view);
                    }
                    else
                    {
                        decryptAndSet(storageDir,file_name,".png",set_view);
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
