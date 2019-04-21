package com.meek.Fragments;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.ActivityViewSetter;
import com.meek.Encryption.AES;
import com.meek.R;
import com.meek.Time.TimeManage;

import java.io.File;

import io.supercharge.shimmerlayout.ShimmerLayout;
import me.grantland.widget.AutofitTextView;

/**
 * Created by User on 11-Jun-18.
 */

@SuppressLint("ValidFragment")
public class BSActivityFragment extends Fragment
{
    String uid,act_id;
    View view;
   // ShimmerLayout shim_content;

    BSActivityFragment()
    {}
    @SuppressLint("ValidFragment")
    public BSActivityFragment(String uid, String act_id)
    {
        this.act_id=act_id;
        this.uid=uid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.activity_card, container, false);
    //   shimmer();
       getDataActivity();
      //  if(act_id.equals("0"))
      //  view.findViewById(R.id.progressView).setVisibility(View.VISIBLE);
        return view;
    }


    void getDataActivity()
    {
        fileCheck();

        view.findViewById(R.id.progressView).setVisibility(View.VISIBLE);
        DatabaseReference ppl_ref = FirebaseDatabase.getInstance().getReference();
        ppl_ref.child("Activities").child(uid).child("All_Activities").child(act_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    view.findViewById(R.id.progressView).setVisibility(View.INVISIBLE);
                    String act_type = dataSnapshot.child("act_type").getValue().toString();
                    String act_date = dataSnapshot.child("act_date").getValue().toString();
                    String act_visibility = dataSnapshot.child("act_visibility").getValue().toString();
                    String act_current_place = dataSnapshot.child("act_current_place").getValue().toString();
                    String act_text = dataSnapshot.child("act_text").getValue().toString();
                    act_text = new AES().decrypt(act_text, "pmdrox");
                    act_date = new AES().decrypt(act_date, "pmdrox");

                    String extension;
                    if (act_type.equals("1"))
                        extension = ".mp4";
                    else
                        extension = ".png";
                    TextView date_view=(TextView)view.findViewById(R.id.date_view);
                    date_view.setText(new TimeManage().setTimeString(Long.parseLong(act_date)));

                    String filename = getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/" + uid + "_" + act_id + "." + extension;
                    if(!new File(filename).exists()) {
                        if (Integer.parseInt(act_type) < 3) {
                            new ActivityViewSetter(getContext()).fileDownload(uid, act_id, act_type, view);
                            TextView caption = (TextView) view.findViewById(R.id.caption);
                            caption.setText(act_text);
                        } else {
                            new ActivityViewSetter(getContext()).setTextView(view);
                            AutofitTextView caption = (AutofitTextView) view.findViewById(R.id.text_view);
                            caption.setText(act_text);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }


    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    boolean fileCheck()
    {
        String filename=getContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()+"/"+uid+"_"+act_id;
        Log.e(" FILE CHECK ","filename= "+ filename);
        if((new File(filename+".mp4")).isFile())
        {
            new ActivityViewSetter(getContext()).setVideoView(view);
            VideoView act_vid = (VideoView)view.findViewById(R.id.vid_view);
            act_vid.setVideoPath(filename+".mp4");
            Log.e(" FILE CHECK ","mp4 is found");
            return true;
        }
        else if((new File(filename+".png")).isFile())
        {
            new ActivityViewSetter(getContext()).setImageView(view);
            ImageView act_img = (ImageView) view.findViewById(R.id.img_view);
            act_img.setImageBitmap(BitmapFactory.decodeFile(filename+".png"));
            Log.e(" FILE CHECK ","png is found");
            return true;
        }
        Log.e(" FILE CHECK ","file is not found");
        return false;
    }

}
