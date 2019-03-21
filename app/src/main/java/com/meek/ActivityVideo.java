package com.meek;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

//import com.iceteck.silicompressorr.SiliCompressor;

import com.dshantanu.androidsquareslib.AndroidSquares;
import com.googlecode.mp4parser.authoring.Edit;
import com.iceteck.silicompressorr.SiliCompressor;
import com.meek.Encryption.AES;
//import com.iceteck.silicompressorr.SiliCompressor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;


import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Pranav on 07-Jun-18.
 */
///Actvity setting tab in which the video displaying mode is set properly
public class ActivityVideo extends Fragment {
    int VID_REQ = 2, VID_EDT = 3;
    boolean play = true;
    View vid_view;
    int left, right;
    boolean kb_on;
    String mCurrentPhotoPath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vid_view = inflater.inflate(R.layout.video_activity, container, false);
        setFront();
        setKeyboardListener();
        return vid_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final SharedPreferences actPrefs = getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        final int curr_stat = actPrefs.getInt("curr_stat", 11);
        final SharedPreferences.Editor actPrefEdit = actPrefs.edit();
        actPrefEdit.putInt("curr_stat", 4);
        actPrefEdit.commit();
        Log.v("vid cam path", "activityresult activityvideo");

        if (requestCode == VID_EDT && resultCode == getActivity().RESULT_OK)        //Recieved activity result for edit the video..go to the cutvideo
        {
            editVideo(2);
        }

        if (requestCode == VID_REQ)                    ///received the actvity result for display the video
        {
            new VideoCompressAsyncTask(getContext()).execute(mCurrentPhotoPath, mCurrentPhotoPath.replace("/activity.mp4",""));

            if (data.getIntExtra("CASE", 3) == 2)       //whether this is a case of update the left and right
            {
                left = data.getIntExtra("LEFT", 3);
                right = data.getIntExtra("RIGHT", 3);
                setVideo();
                Log.v("vid cam path", "if of activityvideo");

            } else {
                if (data.getIntExtra("RESULT", 3) == 1) {
                    left = data.getIntExtra("LEFT", 3);
                    right = data.getIntExtra("RIGHT", 3);
                    setVideo();
                }
            }

        }
    }

    void editVideo(int cse)         ///intent for edit the video
    {
     /*   Intent edt_intent = new Intent(getContext(), CutVideo.class);
        edt_intent.putExtra("PATH", mCurrentPhotoPath);
        edt_intent.putExtra("CASE", cse);
        if (cse == 2) {
            edt_intent.putExtra("LEFT", left);
            edt_intent.putExtra("RIGHT", right);
        }
        startActivityForResult(edt_intent, VID_REQ);*/
    }

    void keyBoardON()               ///if keyboard is on..change the size of the layouts
    {
        kb_on = true;
        ViewGroup layout = vid_view.findViewById(R.id.bg_layout);
        ViewGroup layout_bg = (ViewGroup) vid_view.findViewById(R.id.layout_bg_vid);

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) layout.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.width = 300;
        layout.setLayoutParams(params);

        ViewGroup.LayoutParams bg_params = (ViewGroup.LayoutParams) ((ViewGroup) vid_view.findViewById(R.id.layout_bg_vid)).getLayoutParams();
        bg_params.height = 300;
        layout_bg.setLayoutParams(bg_params);
    }

    void noKeyBoard()               ///if the keyboard is on...change the size of layout t
    {
        Log.v("KYBRD", "keyboard is off");
        View layout = vid_view.findViewById(R.id.bg_layout);
        View layout_bg = (View) vid_view.findViewById(R.id.layout_bg_vid);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layout.setLayoutParams(params);

        ViewGroup.LayoutParams bg_params = (ViewGroup.LayoutParams) ((ViewGroup) vid_view.findViewById(R.id.layout_bg_vid)).getLayoutParams();
        bg_params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        layout_bg.setLayoutParams(bg_params);
    }

    void setKeyboardListener()          ///listen the keyboard changes
    {

        final EditText enter_caption=(EditText)vid_view.findViewById(R.id.vid_caption);
        enter_caption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                CreateActivity act_funcall = (CreateActivity) getContext();
                act_funcall.setCaptionText(enter_caption.getText().toString());

            }
        });
        //////
        final View activityRootView = vid_view.findViewById(R.id.vid_act);
        final Handler handler = new Handler();

        Thread t = new Thread(new Runnable() {
            public void run() {
                boolean lis_on = true;
                while (true) {
                    Log.e("View tree observer","global layout");
                    ViewTreeObserver observer = activityRootView.getViewTreeObserver();

                    if (activityRootView.getViewTreeObserver().isAlive())
                        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                Rect r = new Rect();
                                vid_view.getWindowVisibleDisplayFrame(r);
                                if (vid_view.getRootView().getHeight() - (r.bottom - r.top) > 500) {
                                    Log.v("KYBRD", "keyboard is on");
                                    keyBoardON();
                                } else {
                                    noKeyBoard();
                                }

                                activityRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                        });

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        t.start();
        //////
    }


    void setVideo()         ///sets the video appropriately
    {
        new AES().encryptActivityVideo("pmdrox",getActivity());
        SharedPreferences getprefs = getContext().getSharedPreferences("ActsPrefs", MODE_PRIVATE);
        SharedPreferences.Editor edit_prefs = getprefs.edit();
        edit_prefs.putInt("curr_stat", 4);
        edit_prefs.commit();
        Log.v("vid cam path", "setvideo of activityvideo");
        vid_view.findViewById(R.id.bg_layout).setVisibility(View.VISIBLE);
        vid_view.findViewById(R.id.tap_open).setVisibility(View.INVISIBLE);
        final VideoView act_player = (VideoView) vid_view.findViewById(R.id.act_player);

        //  VideoView comp_player= (VideoView )vid_view.findViewById(R.id.comp_player);
        Bitmap thumb = ThumbnailUtils.createVideoThumbnail(mCurrentPhotoPath, MediaStore.Video.Thumbnails.MINI_KIND);
        AndroidSquares thump = (AndroidSquares) vid_view.findViewById(R.id.bg_layout);
        thump.setBackground(new BitmapDrawable(getResources(), thumb));
        act_player.setVideoURI(Uri.parse(mCurrentPhotoPath));
        //  comp_player.setVideoURI(Uri.parse(mCurrentPhotoPath.replace("activity","com_activity")));
        act_player.start();
        final RelativeLayout play_layer = (RelativeLayout) vid_view.findViewById(R.id.play_layer);
        play_layer.setVisibility(View.INVISIBLE);
        act_player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                act_player.start();
            }
        });
        vid_view.findViewById(R.id.bg_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act_player.pause();
                play_layer.setVisibility(View.VISIBLE);
                play = false;
            }
        });
        play_layer.findViewById(R.id.trim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editVideo(2);
            }
        });
        play_layer.findViewById(R.id.retake).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askVideo();
            }
        });
        play_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("vid cam path", "clicked on the activityvideo");
                if (!play)
                    act_player.start();
                play_layer.setVisibility(View.INVISIBLE);
                play = !play;
            }
        });
        //  comp_player.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    void setFront()             ////setting the start-up screen of the activity appropriately
    {
        SharedPreferences actPrefs = getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        int curr_stat = actPrefs.getInt("curr_stat", 11);
        RelativeLayout tap_open = (RelativeLayout) vid_view.findViewById(R.id.tap_open);
        if (curr_stat == 4) {
            try {
                createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            vid_view.findViewById(R.id.bg_layout).setVisibility(View.VISIBLE);
            vid_view.findViewById(R.id.tap_open).setVisibility(View.INVISIBLE);
            setVideo();
        } else {
            tap_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    askVideo();
                }
            });
        }


    }

    void askVideo()                 ////asks to take video
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
            cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1 );
            cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,20);

            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(cameraIntent, VID_REQ);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    private File createImageFile() throws IOException              /// the filename generator
    {
        // Create an image file name
        String imageFileName = "activity";
        String storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File image = new File(storageDir,
                imageFileName + /* prefix */
                        ".mp4" /* suffix */
                /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
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
                Log.e("Silicompressor", "Compression started");
                filePath = SiliCompressor.with(mContext).compressVideo(paths[0], paths[1]);
                Log.e("Silicompressor", "Compression finished");
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
            videoFile.renameTo(new File(compressedFilePath.replace(compressedFilePath.substring(compressedFilePath.indexOf("VIDEO"),compressedFilePath.indexOf(".mp4")),"activity")));
            p_dialog.dismiss();
            Log.i("Silicompressor", "Path: " + compressedFilePath);
            setVideo();
        }
    }






}