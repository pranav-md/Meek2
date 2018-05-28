package com.meek;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by User on 20-May-18.
 */

public class ActivityService extends IntentService
{
    public ActivityService()
    {
        super("ActivityRecognitionService");
    }
    public ActivityService(String res)
    {
        super(res);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        if(ActivityRecognitionResult.hasResult(intent))
        {
            ActivityRecognitionResult result=ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivity(result.getProbableActivities());
        }
    }

    public  void handleDetectedActivity(List<DetectedActivity> probActs)
    {
        int maxconf;
        int act;
        maxconf=probActs.get(0).getConfidence();
        act=probActs.get(0).getType();
        for(DetectedActivity activity:probActs)
        {
            if(maxconf<activity.getConfidence())
            {
                maxconf=activity.getConfidence();
                act=activity.getType();
            }
        }


        Realm realm = null;
        Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.
                Builder().name("activity").
                deleteRealmIfMigrationNeeded().
                build();
        Realm.setDefaultConfiguration(config);
        realm=Realm.getInstance(config);

     // create your Realm configuration

        final int finalAct = act;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                com.meek.Activity old_act=realm.where(com.meek.Activity.class).findFirst();
                        //realm.where(Activity.class).findFirst();

                if(old_act==null)
                {
                    com.meek.Activity new_act=realm.createObject(com.meek.Activity.class);
                    new_act.activity= finalAct;
                }
                else
                {
                    old_act.activity= finalAct;
                }
            }
        });




        switch(act)
        {
            case DetectedActivity.IN_VEHICLE:   Log.d("HAH","In Vehicle");
                                                break;
            case DetectedActivity.ON_BICYCLE:   Log.d("HAH","ON_BICYCLE");
                break;

            case DetectedActivity.ON_FOOT:   Log.d("HAH","ON_FOOT");
                break;

            case DetectedActivity.RUNNING:   Log.d("HAH","RUNNING");
                break;

            case DetectedActivity.STILL:   Log.d("HAH","STILL");
                break;

            case DetectedActivity.TILTING:   Log.d("HAH","TILTING");
                break;

            case DetectedActivity.WALKING:   Log.d("HAH","WALKING");
                break;

        }
    }



}