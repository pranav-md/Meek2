package com.meek;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.github.tamir7.contacts.*;
import com.github.tamir7.contacts.Contact;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by User on 29-May-18.
 */

public class ContactSync {
    Context context;

    void syncContact(final String status, Context context)
    {
        this.context=context;
        Contacts.initialize(context);

        List<com.github.tamir7.contacts.Contact> contacts=Contacts.getQuery().find();

        Realm myRealm= Realm.getDefaultInstance();

        for(final Contact contct:contacts)
        {
            Log.d("Contact syncing","id="+contct.getId()+"  name="+contct.getDisplayName());
            for(final PhoneNumber phno:contct.getPhoneNumbers()) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                String phonenum=phno.getNumber();
                if(!phoneUtil.isValidNumber(new Phonenumber.PhoneNumber().setRawInput(phonenum)))
                {
                    try {
                        Phonenumber.PhoneNumber p_num=phoneUtil.parse(phonenum,"IN");
                        phonenum=p_num.toString();
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                phonenum=phoneUtil.format(new Phonenumber.PhoneNumber().setRawInput(phonenum), PhoneNumberUtil.PhoneNumberFormat.E164);
                if(phoneUtil.isValidNumber(new Phonenumber.PhoneNumber().setRawInput(phonenum))) {
                    final String finalPhonenum = phonenum;
                    myRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            com.meek.Contact contact = realm.createObject(com.meek.Contact.class);
                            contact.setID(String.valueOf(contct.getId()));
                            contact.setName(contct.getDisplayName());
                            contact.setPhnum(finalPhonenum);
                            contact.setStatus(status);
                        }

                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Log.d("Contact syncing","id="+contct.getId()+"  name="+contct.getDisplayName()+"  phno="+finalPhonenum);
                        }
                    });
                }
            }

        }
        if(status.equals("update"))
            crossCheck();
    }

    void crossCheck()
    {
        Realm myRealm= Realm.getDefaultInstance();
        RealmResults<com.meek.Contact> syncAct=myRealm.where(com.meek.Contact.class)
                                                .equalTo("status","update").findAll();

    }

}
