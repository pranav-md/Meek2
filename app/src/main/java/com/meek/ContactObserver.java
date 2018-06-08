package com.meek;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;

/**
 * Created by User on 29-May-18.
 */

public class ContactObserver extends ContentObserver {
    private Context context;

    public ContactObserver(Handler handler) {
        super(handler);
    }

    public ContactObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Log.wtf("Contact_sync","Contact changed");

        Log.wtf("Contact_sync","contact observer outside selfchange");

        if (!selfChange) {
            Log.wtf("Contact_sync","contact observer inside selfchange");

            try {
                new ContactSync().syncContact("update",context);
            } catch (NumberParseException e) {
                e.printStackTrace();
            }
        }
    }
}
