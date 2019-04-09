package com.meek.Services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.List;


/**
 * Created by User on 20-May-18.
 */

public class ActivityService extends IntentService
{
    private Looper mServiceLooper;
    private ServiceConHandler mServiceHandler;

    public ActivityService()
    {
        super("ActivityRecognitionService");
    }
    public ActivityService(String res)
    {
        super(res);
    }
    final class ServiceConHandler extends Handler {
        public ServiceConHandler(Looper looper) {
            super(looper);
        }
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        if(ActivityRecognitionResult.hasResult(intent))
        {
            ActivityRecognitionResult result=ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivity(result.getProbableActivities());
        }
        Log.v("Actvityservice","onHandleIntent");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceConHandler(mServiceLooper);
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


       // RealmConfiguration


     // create your Realm configuration

        final int finalAct = act;
   /*     realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                com.meek.Activity old_act=realm.where(com.meek.Activity.class).findFirst();
                        //realm.where(Activity.class).findFirst();

                if(old_act==null)
                {
                    com.meek.Activity new_act=realm.createObject(com.meek.Activity.class);
                   // new_act.activity= finalAct;
                }
                else
                {
                  //  old_act.activity= finalAct;
                }

            }
        });

*/

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