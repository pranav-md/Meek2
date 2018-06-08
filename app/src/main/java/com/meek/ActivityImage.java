package com.meek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dshantanu.androidsquareslib.AndroidSquares;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;


/**
 * Created by User on 07-Jun-18.
 */

public class ActivityImage extends Fragment {
    String mCurrentPhotoPath;
    int CAM_CODE=1;
    private static final float BLUR_RADIUS = 25f;
    View img_view;
    boolean active;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        img_view = inflater.inflate(R.layout.image_activity, container, false);

        return img_view;
    }

    void askCamera()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(".png");
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
    private File createImageFile(String ext) throws IOException {
        // Create an image file name
        String imageFileName = "activity";
        String storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        File image = new File(storageDir,
                imageFileName+ /* prefix */
                        ext /* suffix */
                      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CAM_CODE&& resultCode == getActivity().RESULT_OK)
        {

            ZoomageView act_img=(ZoomageView)img_view.findViewById(R.id.act_img);
            File imgFile = new  File(mCurrentPhotoPath);
            AndroidSquares bg_img=(AndroidSquares) img_view.findViewById(R.id.bg_layout);
            if(imgFile.exists())
            {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                act_img.setImageBitmap(myBitmap);
                bg_img.setBackground(new BitmapDrawable(getResources(),blur(myBitmap)));

            }
        }
    }

    public Bitmap blur(Bitmap image) {
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
        final RenderScript renderScript = RenderScript.create(getContext());
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

}
