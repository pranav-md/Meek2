package com.meek;

import android.content.Context;
import android.util.Log;

import com.github.tamir7.contacts.*;
import com.github.tamir7.contacts.Contact;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * Created by User on 29-May-18.
 */

public class ContactSync {
    Context context;
    String uid;
    void syncContact( Context context,String uid){
        Log.v("Contact_sync","contactsync...synccontact");
        this.context=context;
        Contacts.initialize(context);
        this.uid=uid;
        List<com.github.tamir7.contacts.Contact> contacts=Contacts.getQuery().find();

        Log.v("Contact syncing","Contacts size="+contacts.size());
        DatabaseReference con_ref= FirebaseDatabase.getInstance().getReference();
        for(final Contact contct:contacts)
        {
            for(final PhoneNumber phno:contct.getPhoneNumbers()) {
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber phNumberProto = null;

                String real_phnum=covertToNum(phno.getNumber());

                con_ref.child("Contacts_DB").child(uid).child(getSHA(real_phnum)).setValue("0");
                Log.v("Contact syncing","sha=" + getSHA(real_phnum) +"  name="+contct.getDisplayName()+"  phno="+real_phnum);
            }
            con_ref.child("Contacts_DB").child(uid).child("contact_trigger").setValue(new Date()+"");
        }

    }


    String covertToNum(String phno)
    {
        String phone_num=" ";
        Phonenumber.PhoneNumber phoneNumber=null;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            phoneNumber = phoneUtil.parse(phno,"IN");

        if(phoneUtil.isValidNumber(phoneNumber))
        {
            phone_num=phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        }
        }
        catch (NumberParseException e) {
            e.printStackTrace();
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }

        Log.v("Contact syncing","original phone num="+phno+" E164 phone num="+phone_num);
        return phone_num;
    }


    public String getSHA(String input)
    {

        try {

            // Static getInstance method is called with hashing SHA
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // digest() method called
            // to calculate message digest of an input
            // and return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown"
                    + " for incorrect algorithm: " + e);

            return null;
        }
    }
}
