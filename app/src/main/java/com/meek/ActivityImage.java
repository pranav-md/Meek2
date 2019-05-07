package com.meek;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
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
import android.support.v4.content.FileProvider;
import android.support.v4.widget.ViewDragHelper;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dshantanu.androidsquareslib.AndroidSquares;
import com.google.android.gms.vision.Frame;
import com.iceteck.silicompressorr.SiliCompressor;
import com.jsibbold.zoomage.ZoomageView;
import com.meek.Encryption.AES;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


/**
 * Created by User on 07-Jun-18.
 */

@SuppressLint("ValidFragment")
public class ActivityImage extends Fragment {

    String mCurrentPhotoPath;
    int CAM_CODE = 1;
    private static final float BLUR_RADIUS = 25f;
    View img_view;
    boolean active,kb_on;
    String serverkey;

    @SuppressLint("ValidFragment")
    public ActivityImage(String serverkey)
    {
        this.serverkey=serverkey;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        img_view = inflater.inflate(R.layout.image_activity, container, false);
        setFront();
        kb_on=true;
        setKeyboardListener();
        return img_view;
    }
    void noKeyBoard()
    {
        Log.v("KYBRD", "keyboard is off");
        View layout = img_view.findViewById(R.id.bg_layout);
        View layout_bg=(View) img_view.findViewById(R.id.layout_bg_img);

        ViewGroup.LayoutParams params = layout.getLayoutParams();
// Changes the height and width to the specified *pixels*
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layout.setLayoutParams(params);
        EditText img_caption=img_view.findViewById(R.id.img_caption);

        ViewGroup.LayoutParams bg_params = (ViewGroup.LayoutParams)((ViewGroup)img_view.findViewById(R.id.layout_bg_img)).getLayoutParams();
        bg_params.height= ViewGroup.LayoutParams.WRAP_CONTENT;
        layout_bg.setLayoutParams(bg_params);
    /*    ViewGroup.MarginLayoutParams edt_parms=new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,50);
        edt_parms.setMargins(0,100,0,0);
        img_caption.setLayoutParams(edt_parms);
    */

    }

    void keyBoardON()
    {
        kb_on=true;
        ViewGroup layout=img_view.findViewById(R.id.bg_layout);
        ViewGroup layout_bg=(ViewGroup) img_view.findViewById(R.id.layout_bg_img);

        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) layout.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.width = 300;
        layout.setLayoutParams(params);

        ViewGroup.LayoutParams bg_params = (ViewGroup.LayoutParams)((ViewGroup)img_view.findViewById(R.id.layout_bg_img)).getLayoutParams();
        bg_params.height=300;
        layout_bg.setLayoutParams(bg_params);
    }

    void setKeyboardListener()
    {
        LinearLayout i_act=img_view.findViewById(R.id.img_act);
        //////
        final EditText enter_caption=(EditText)img_view.findViewById(R.id.img_caption);
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
        final View activityRootView = img_view.findViewById(R.id.img_act);
        final Handler handler = new Handler();
        Thread t = new Thread(new Runnable() {
            public void run() {
                boolean lis_on=true;
                while(true)
                {
                    if(activityRootView.getViewTreeObserver().isAlive())
                    activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                  @Override
                  public void onGlobalLayout() {
                      Rect r = new Rect();
                      img_view.getWindowVisibleDisplayFrame(r);
                      if (img_view.getRootView().getHeight() - (r.bottom - r.top) > 500) {
                          Log.v("KYBRD", "keyboard is on");
                          keyBoardON();

                      } else {
                          noKeyBoard();
                      }

                      activityRootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                  }

                });

                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                }
            }
        });

        t.start();

        //////
        EditText img_caption=img_view.findViewById(R.id.img_caption);
    }

    void askCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAM_CODE);
            }
        }
    }

    void setFront()
    {
        SharedPreferences actPrefs= getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        int curr_stat=actPrefs.getInt("curr_stat",11);
        RelativeLayout tap_open=(RelativeLayout)img_view.findViewById(R.id.tap_open);
        if(curr_stat==9)
        {
            tap_open.setVisibility(View.INVISIBLE);
            setImage();
        }
        else
        {
            tap_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    askCamera();
                }
            });
        }
    }

    void setTakePhoto()
    {
        RelativeLayout tap_open=(RelativeLayout)img_view.findViewById(R.id.tap_open);
        tap_open.setVisibility(View.VISIBLE);
        SharedPreferences actPrefs= getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        SharedPreferences.Editor actPrefEdit=actPrefs.edit();
        actPrefEdit.putInt("curr_stat",11);
        actPrefEdit.commit();
        ZoomageView act_img=(ZoomageView)img_view.findViewById(R.id.act_img);
        AndroidSquares bg_img=(AndroidSquares) img_view.findViewById(R.id.bg_layout);
        act_img.setImageDrawable(null);
        bg_img.setBackground(null);
    }

    void setImage()
    {
        new AES().encryptActivityImage("pmdrox",getActivity());

        RelativeLayout tap_open=(RelativeLayout)img_view.findViewById(R.id.tap_open);
        tap_open.setVisibility(View.INVISIBLE);
        ImageView retake=(ImageView)img_view.findViewById(R.id.retake);
        ImageView close_img=(ImageView)img_view.findViewById(R.id.close_img);
        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCamera();
            }
        });
        close_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTakePhoto();
            }
        });
        try {
            createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ZoomageView act_img=(ZoomageView)img_view.findViewById(R.id.act_img);
        File imgFile = new  File(mCurrentPhotoPath);
        AndroidSquares bg_img=(AndroidSquares) img_view.findViewById(R.id.bg_layout);
        bg_img.setVisibility(View.VISIBLE);
        if(imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            act_img.setImageBitmap(myBitmap);
            bg_img.setBackground(new BitmapDrawable(getResources(),blur(myBitmap,getContext())));
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "activity";
        String storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File image = new File(storageDir,
                imageFileName+ /* prefix */
                        ".png" /* suffix */
                      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final SharedPreferences actPrefs= getContext().getSharedPreferences("ActPrefs", MODE_PRIVATE);
        final int curr_stat=actPrefs.getInt("curr_stat",11);
        final SharedPreferences.Editor actPrefEdit=actPrefs.edit();
        actPrefEdit.putInt("curr_stat",9);
        actPrefEdit.commit();
        new ActivityImage.ImageCompressAsyncTask(getContext()).execute(mCurrentPhotoPath, mCurrentPhotoPath.replace("/activity.png",""));
        if(requestCode==CAM_CODE&& resultCode == getActivity().RESULT_OK)
        {
          //  new ActivityImage.ImageCompressAsyncTask(getContext()).execute(mCurrentPhotoPath, mCurrentPhotoPath);

        }
    }

    public static Bitmap blur(Bitmap image,Context ctxt) {
        if (null == image) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        Bitmap mut_otBitmap=outputBitmap.copy(Bitmap.Config.ARGB_8888,true);
        Canvas c= new Canvas(mut_otBitmap);
        if(image.getWidth()>image.getHeight())
        {
        //    outputBitmap= Bitmap.createScaledBitmap(image, image.getWidth()/2,image.getWidth()/2, true);
            c.drawBitmap(mut_otBitmap,image.getHeight()/2,image.getHeight()/2,null);
        }
        else
        {
       //     outputBitmap= Bitmap.createScaledBitmap(image, image.getHeight()/2,image.getHeight()/2, true);

            c.drawBitmap(mut_otBitmap,image.getHeight()/2,image.getHeight()/2,null);
        }
        final RenderScript renderScript = RenderScript.create(ctxt);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, mut_otBitmap);

        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(mut_otBitmap);

        return mut_otBitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    class ImageCompressAsyncTask extends AsyncTask<String, String, String> {

        Context mContext;
        ProgressDialog p_dialog;


        public ImageCompressAsyncTask(Context context) {
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
            Log.e("Silicompressor", "Compression started");
            filePath = SiliCompressor.with(mContext).compress(paths[0], new File(paths[1]));

            Log.e("Silicompressor", "Compression finished");
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
            videoFile.renameTo(new File(compressedFilePath.replace(compressedFilePath.substring(compressedFilePath.indexOf("IMG"),compressedFilePath.indexOf(".jpg")+4),"activity.png")));
            p_dialog.dismiss();
            Log.i("Silicompressor", "Path: " + compressedFilePath);
            setImage();
        }
    }




}
