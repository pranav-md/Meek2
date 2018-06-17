package com.meek;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.VideoView;

//import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 07-Jun-18.
 */

public class ActivityVideo extends Fragment {
    int VID_REQ=2;
    View vid_view;
    String mCurrentPhotoPath;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
         vid_view = inflater.inflate(R.layout.video_activity, container, false);
         setFront();

        return vid_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final SharedPreferences actPrefs= getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        final int curr_stat=actPrefs.getInt("curr_stat",11);
        final SharedPreferences.Editor actPrefEdit=actPrefs.edit();
        actPrefEdit.putInt("curr_stat",4);
        actPrefEdit.commit();
     /*   try {
            String filePath = SiliCompressor.with(getContext()).compressVideo(Uri.parse(mCurrentPhotoPath),  mCurrentPhotoPath.replace("activity","com_activity"),50,50,50);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
*/
        if(resultCode==VID_REQ)
        {
          setVideo();
        }
    }
    void setVideo()
    {
        RelativeLayout tap_open=(RelativeLayout)vid_view.findViewById(R.id.tap_open);
        tap_open.setVisibility(View.INVISIBLE);
        VideoView act_player= (VideoView )vid_view.findViewById(R.id.act_player);
      //  VideoView comp_player= (VideoView )vid_view.findViewById(R.id.comp_player);
        act_player.setVideoURI(Uri.parse(mCurrentPhotoPath));
      //  comp_player.setVideoURI(Uri.parse(mCurrentPhotoPath.replace("activity","com_activity")));
        act_player.start();
      //  comp_player.start();
    }


    void setFront()
    {
        SharedPreferences actPrefs= getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        int curr_stat=actPrefs.getInt("curr_stat",11);
        RelativeLayout tap_open=(RelativeLayout)vid_view.findViewById(R.id.tap_open);
        if(curr_stat==4)
        {
            try {
                createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            tap_open.setVisibility(View.INVISIBLE);
            setVideo();
        }
        else
        {
            tap_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    askVideo();
                }
            });
        }



    }

    void askVideo()
    {
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File

        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                    "com.example.android.fileprovider",
                    photoFile);
            Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            getActivity().startActivityForResult(cameraIntent, VID_REQ);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    private File createImageFile() throws IOException
    {
        // Create an image file name
        String imageFileName = "activity";
        String storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File image = new File(storageDir,
                imageFileName+ /* prefix */
                        ".mp4" /* suffix */
                      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
