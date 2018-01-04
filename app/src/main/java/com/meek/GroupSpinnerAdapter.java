package com.meek;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by User on 26-Dec-17.
 */

public class GroupSpinnerAdapter extends BaseAdapter {
    Context context;
    AdaptHelper[] gp_ids=new AdaptHelper[50];
    LayoutInflater inflter;
    public GroupSpinnerAdapter(Context applicationContext,AdaptHelper[] grps)
    {
        this.context = applicationContext;
        this.gp_ids=grps;
    }
    @Override
    public int getCount() {
        return gp_ids.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view=inflter.inflate(R.layout.group_spinner,null);
        CircleImageView icon = (CircleImageView) view.findViewById(R.id.grp_dp);
        TextView names = (TextView) view.findViewById(R.id.grp_name);
        view.setTag(gp_ids[position].uid);
        String sFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Meek/Groups/" +gp_ids[position].dpno+ ".jpg";
        File f = new File(sFolder);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f));
            icon.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            icon.setImageResource(R.drawable.defaultdp);
            e.printStackTrace();
        }
        names.setText(gp_ids[position].name);
        return view;
    }
}
