package com.meek;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.Manifest.permission;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.labo.kaji.fragmentanimations.FlipAnimation;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;

import android.widget.Button;
import android.widget.VideoView;


import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;


import static android.content.ContentValues.TAG;

/**
 * Created by User on 25-May-18.
 */

@SuppressLint("ValidFragment")
public class TabFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    GoogleApiClient mGoogleApiClient=null;
    int CAM_CODE=1,VID_REQ=2,SET_DEST=3;
    String mCurrentPhotoPath;

    ActivityVideo act_vid;
    ActivityImage act_img;

    private static final int GOOGLE_API_CLIENT_ID = 0;
    Context context;
    ImagePicker imagePicker;

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    String placess;
    TextView plcs;
    View activity_layout;
    RelativeLayout tabcontainer;
    ExpandableLayout img_exp;
    int curr_tab;
    boolean tab_act;

    @SuppressLint("ValidFragment")
    public TabFragment(Context context)
    {
        super.onAttach(context);
        this.context=context;
    }
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return FlipAnimation.create(FlipAnimation.RIGHT, enter, 500);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tabsfragment, container, false);
        final BottomNavigation btm_nav=(BottomNavigation)view.findViewById(R.id.bottom_nav);
        final BottomNavigation check_sel=btm_nav;
        btm_nav.setSelectedIndex(2,true);
       plcs=(TextView)view.findViewById(R.id.placess);
        tabcontainer=view.findViewById(R.id.tabscontainer);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        btm_nav.setOnMenuItemClickListener(new BottomNavigation.OnMenuItemSelectionListener() {
            @Override
            public void onMenuItemSelect(int i, int i1, boolean b) {
                Toast.makeText(context,"Activity",Toast.LENGTH_LONG);
                if(btm_nav.getSelectedIndex()==2) {
                    setActivtiyTab();
                    Toast.makeText(context, "Activity", Toast.LENGTH_LONG);
                }
                else if(btm_nav.getSelectedIndex()==1) {
                    Toast.makeText(context, "Chat", Toast.LENGTH_LONG);
                    setMsgTab();
                }
                else if(btm_nav.getSelectedIndex()==0)
                    Toast.makeText(getContext(),"Contact",Toast.LENGTH_LONG);
            }

            @Override
            public void onMenuItemReselect(int i, int i1, boolean b) {

            }
        });
        callPlaceDetectionApi();
              return view;
    }

    private void callPlaceDetectionApi() throws SecurityException {
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i("Google places", String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName()
                            ,
                            placeLikelihood.getLikelihood()));
                    placess=placess+placeLikelihood.getPlace().getName().toString()+"\n";
                }
                likelyPlaces.release();
                setPlaceList();
            }
        });
    }

    void setPlaceList()
    {
        plcs.setText(placess);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i("Google Places", "Google Places API connected.");

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("Google Places", "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }
    void setActivtiyTab()
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflatedLayout= inflater.inflate(R.layout.activity_tab, null, false);
        activity_layout=inflatedLayout;
        tabcontainer.removeAllViews();
        tabcontainer.addView(inflatedLayout);

        setTabsSetActivity();

/*
        Button camera,video;
        camera=(Button)inflatedLayout.findViewById(R.id.camera);
        video=(Button)inflatedLayout.findViewById(R.id.video);

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context,permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{permission.CAMERA},
                            100);
                }
                else
                {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile(".mp4");
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
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(context,permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{permission.CAMERA},
                            100);
                }
                else
                {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
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
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show();
                imagePicker.setWithImageCrop(1,1).openCamera();
            } else {
                Toast.makeText(context, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    void resultOfActivity(int requestCode, int resultCode, Intent data) {

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("Camera daa", "handle activity");

        if (requestCode == VID_REQ && resultCode == getActivity().RESULT_OK)
        {
           act_vid.onActivityResult(requestCode,resultCode,data);
        }
        else if (requestCode == CAM_CODE)
        {
            act_img.onActivityResult(requestCode,resultCode,data);
        }
        else if(requestCode==SET_DEST)
        {
            Log.v("SET DEST","yoyoyo");
        }
    }


    void setMsgTab()
    {
   //     LayoutInflater inflater = LayoutInflater.from(context);
  //      View inflatedLayout= inflater.inflate(R.layout.msg_dialog_list, null, false);
  //      tabcontainer.removeAllViews();
  //      tabcontainer.addView(inflatedLayout);
//        @SuppressLint("WrongViewCast") ListView listView=inflatedLayout.findViewById(R.id.msg_list_tab);


        //// start a progress bar

        //// firebase listener to the message list

        /// Declare an array list...assign the data of the messages of list one by one
                            /// such a way that, for each uid-> check offline realm data for the name and dp
                            ///if found save to the arraylist... if not found, query to firebase download the dp and save internal and to realm
                            //// query to download the messages and include last 1 of them in the arraylist
            /// repeat until the first six and store the last list item  ///

        ///adapt and shoow... if the listner worked as listener, do the same thing and notifydatachange


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

/*    void setActivityPic()
    {
        ExpandableLayout img_exp=(ExpandableLayout)activity_layout.findViewById(R.id.image_expand);
        img_exp.toggle();
        ImageView act_img=(ImageView)activity_layout.findViewById(R.id.act_img);

        File imgFile = new  File(mCurrentPhotoPath);

        if(imgFile.exists())
        {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            act_img.setImageBitmap(myBitmap);
        }

    }
    void setActivityVid()
    {
        ExpandableLayout vid_exp=(ExpandableLayout)activity_layout.findViewById(R.id.video_expand);
        vid_exp.toggle();

        VideoView act_player=(VideoView) activity_layout.findViewById(R.id.act_player);
        act_player.setVideoPath(mCurrentPhotoPath);

        //Button play=(Button)activity_layout.findViewById(R.id.play)


    }
*/
    void setTabsSetActivity()
    {
        Log.v("Tab daa", "tab is set YOO");

        ViewPager mviewPager = (ViewPager) activity_layout.findViewById(R.id.tab_container);
        ActivityTabAdapter activityTabAdapter = new ActivityTabAdapter(getChildFragmentManager());

        act_vid=new ActivityVideo();
        act_img=new ActivityImage();
        final ActivityText act_txt=new ActivityText();

        activityTabAdapter.addFragment(act_vid, "Video");
        activityTabAdapter.addFragment(act_img, "Image");
        activityTabAdapter.addFragment(act_txt, "Text");

        mviewPager.setAdapter(activityTabAdapter);
        final TabLayout tabLayout = (TabLayout) activity_layout.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mviewPager);
        tabLayout.setTabMode(3);
        curr_tab=tabLayout.getSelectedTabPosition();
        Button set_dest=(Button)activity_layout.findViewById(R.id.set_dest);
        set_dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivityForResult(new Intent(getContext(),DestinationPlace.class),SET_DEST);
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.v("Tab daa", "tab selected:" +tab.getText());
                if(tab.getText().equals("Image"))
                {
                    act_img.askCamera();
                }
                else if(tab.getText().equals("Video"))
                {
                    act_vid.askVideo();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                    if(tab.getText().equals("Text")&&(act_txt.active))
                    {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
                        AlertDialog HAHA = alertDialog.create();
                            HAHA.setTitle("wtf?");
                        HAHA.show();
                    }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}
