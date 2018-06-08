package com.meek;

import android.content.Context;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.Toast;

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

    void syncContact(final String status, Context context) throws NumberParseException {
        Log.v("Contact_sync","contactsync...synccontact");
        this.context=context;
        Contacts.initialize(context);

        List<com.github.tamir7.contacts.Contact> contacts=Contacts.getQuery().find();
        Realm.init(context);
        Realm myRealm= Realm.getDefaultInstance();
        Toast.makeText(context,"Contacts size="+contacts.size(),Toast.LENGTH_LONG);
        Log.v("Contact syncing","Contacts size="+contacts.size());

        for(final Contact contct:contacts)
        {
            for(final PhoneNumber phno:contct.getPhoneNumbers()) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber phNumberProto = null;
                try {
                    phNumberProto = phoneUtil.parse(phno.getNumber().replaceAll(" ","").replaceAll("-",""),"IN");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                    continue;
                }
                Log.v("Contact syncing","id="+contct.getId()+"  name="+contct.getDisplayName()+"  phno="+phno.getNumber());

                String phonenum=phno.getNumber().replaceAll(" ","").replaceAll("-","");
               // String phonenum=phno.getNumber().replace(" ","")
                //                                .replace("-","");
                if(!phoneUtil.isValidNumber(phNumberProto))
                {
                    try {
                        // Phonenumber.PhoneNumber p_num = new Phonenumber.PhoneNumber().setCountryCode(91).setNationalNumber(Long.parseLong(phonenum));

                        phonenum = "+91" + phNumberProto.getNationalNumber();
                        Log.v("Contct aftr crct sync", "id=" + contct.getId() + "  name=" + contct.getDisplayName() + "  phno=" +(phoneUtil.parse(phonenum,"IN")).getRawInput()+ " valid=" + phoneUtil.isValidNumber((phoneUtil.parse(phonenum,"IN"))));
                    }
                    catch (NumberFormatException excp)
                    {
                        continue;
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                        continue;
                    }
                }
                Log.v("Contact syncing","id="+contct.getId()+"  name="+contct.getDisplayName()+"  phno="+phonenum);
              //  phonenum=phoneUtil.format(new Phonenumber.PhoneNumber().setRawInput(phonenum), PhoneNumberUtil.PhoneNumberFormat.E164);
                if(phoneUtil.isValidNumber((phoneUtil.parse(phonenum,"IN")))) {
                    final String finalPhonenum = phonenum;
                    myRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            com.meek.Contact contact = realm.createObject(com.meek.Contact.class);
                            contact.setID(String.valueOf(contct.getId()));
                            contact.setName(contct.getDisplayName());
                            contact.setPhnum(finalPhonenum);
                            contact.setStatus(status);
                            Log.v("Contact realm syncing", "id=" + contct.getId() + "  name=" + contct.getDisplayName() + "  phno=" + finalPhonenum);

                        }  });
                }
            }

        }
        myRealm.close();
        if(status.equals("update"))
            crossCheck();
    }

    void crossCheck()
    {
        Realm myRealm= Realm.getDefaultInstance();
        RealmResults<com.meek.Contact> syncCon=myRealm.where(com.meek.Contact.class)
                                                .equalTo("status","sync").findAll();
        RealmResults<com.meek.Contact> updCon=myRealm.where(com.meek.Contact.class)
                .equalTo("status","update").findAll();

        for(final com.meek.Contact con:syncCon)
        {
            if(myRealm.where(com.meek.Contact.class).equalTo("id",con.getID()).equalTo("status","update").equalTo("phnum",con.getPhnum()).findAll().size()==0)
            {
                   myRealm.executeTransaction(new Realm.Transaction() {
                       @Override
                       public void execute(Realm realm) {
                           com.meek.Contact contact=realm.where(com.meek.Contact.class).equalTo("status","sync").equalTo("phnum",con.getPhnum()).findFirst();
                           contact.setStatus("delete");
                       }
                   });
            }
        }


        for(final com.meek.Contact con:updCon)
        {
            if(myRealm.where(com.meek.Contact.class).equalTo("id",con.getID()).equalTo("status","sync").equalTo("phnum",con.getPhnum()).findAll().size()==0)
            {
                myRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        com.meek.Contact contact=realm.where(com.meek.Contact.class).equalTo("status","update").equalTo("phnum",con.getPhnum()).findFirst();
                        contact.setStatus("add");
                    }
                });
            }
        }
        myRealm.where(com.meek.Contact.class)
                .equalTo("status","update").findAll().deleteAllFromRealm();
        for(com.meek.Contact con:myRealm.where(com.meek.Contact.class).findAll())
        {
            Log.v("All realm contact", "id=" + con.getID() + "  name=" + con.getName() + "  phno=" +con.getPhnum()+"   status="+con.getStatus());
        }

    }

}
