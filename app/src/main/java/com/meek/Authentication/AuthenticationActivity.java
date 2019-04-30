package com.meek.Authentication;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.meek.AccountManage.AccountSetup;
import com.meek.ContactSync;
import com.meek.Encryption.FingerPrintActivity;
import com.meek.Encryption.FingerprintHandler;
import com.meek.Encryption.RSAKeyExchange;
import com.meek.MainActivity;
import com.meek.R;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

//import android.database.Cursor;

/**
 * Created by User on 19-Dec-17.
 */
@SuppressWarnings("unchecked")
public class
AuthenticationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ProgressDialog progressBar;
    String uid;
    String[] countryNames={"Afghanistan 	+93",
            "Albania 	+355",
            "Algeria 	+213",
            "American Samoa	  +1-684",
            "Angola	+244",
            "Anguilla 	+1-264",
            "Antarctica	 +672",
            "Antigua and Barbuda	+1-268",
            "Argentina 	+54",
            "Armenia	+374",
            "Aruba	+297",
            "Australia	+61",
            "Austria	+43",
            "Azerbaijan 	+994",

            "Bahamas	+1-242",
            "Bahrain	+973",
            "Bangladesh  +880",
            "Barbados 	+1-246",
            "Belarus 	+375",
            "Belgium 	+32",
            "Belize 	+501",
            "Benin 	+229",
            "Bermuda 	+1-441",
            "Bhutan 	+975",
            "Bolivia 	+591",
            "Bosnia & Herzegovina 	+387",
            "Botswana 	+267",
            "Brazil 	+55",
            "Brunei  +673",
            "Bulgaria 	+359",

            "Cambodia	+855",
            "Cameroon 	+237",
            "Canada 	+1",
            "Cape Verde 	+238",
            "Cayman Islands 	+1-345",
            "Central African Republic 	+236",
            "Chad 	+235",
            "Chile 	+56",
            "China 	+86",
            "Christmas Island 	+53",
            "Colombia 	+57",
            "Comoros 	+269",
            "Congo(DRC) 	+243",
            "Congo(Republic)	+242",
            "Cook Islands  +682",
            "Costa Rica  +506",
            "Cote D'Ivoire  +225",
            "Croatia  +385",
            "Cuba  +53",
            "Cyprus  +357",
            "Czech Republic	+420",

            "Denmark 	+45",
            "Dominica 	+1-767",
            "Dominican Republic 	+1-809 and +1-829 ",

            "East Timor (Former Portuguese Timor)	+670",
            "Ecuador 	+593",
            "Egypt (Former United Arab Republic - with Syria)	+20",
            "El Salvador 	+503",
            "Equatorial Guinea (Former Spanish Guinea)	+240",
            "Eritrea (Former Eritrea Autonomous Region in Ethiopia)	+291",
            "Estonia (Former Estonian Soviet Socialist Republic)	+372",
            "Ethiopia (Former Abyssinia, Italian East Africa)	+251",

            "Falkland Islands (Islas Malvinas) 	+500",
            "Faroe Islands 	+298",
            "Fiji 	+679",
            "Finland 	+358",
            "France 	+33",
            "French Guiana or French Guyana 	+594",
            "French Polynesia (Former French Colony of Oceania)	+689",


            "Gabon (Gabonese Republic)	+241",
            "Gambia, The 	+220",
            "Georgia (Former Georgian Soviet Socialist Republic)	+995",
            "Germany 	+49",
            "Ghana (Former Gold Coast)	+233",
            "Gibraltar 	+350",
            "Greece 	+30",
            "Greenland 	+299",
            "Grenada 	+1-473",
            "Guadeloupe	+590",
            "Guam	+1-671",
            "Guatemala 	+502",
            "Guinea (Former French Guinea)	+224",
            "Guinea-Bissau (Former Portuguese Guinea)	+245",
            "Guyana (Former British Guiana)	+592",

            "Haiti 	+509",
            "Honduras 	+504",
            "Hong Kong 	+852",
            "Hungary 	+36",

            "Iceland 	+354",
            "India 	+91",
            "Indonesia 	+62",
            "Iran 	+98",
            "Iraq 	+964",
            "Ireland 	+353",
            "Israel 	+972",
            "Italy 	+39",

            "Jamaica 	+1-876",
            "Japan 	+81",
            "Jordan 	+962",

            "Kazakhstan 	+7",
            "Kenya 	+254",
            "Kiribati (Pronounced keer-ree-bahss) (Former Gilbert Islands)	+686",
            "Korea, Democratic People's Republic of (North Korea)	+850",
            "Korea, Republic of (South Korea) 	+82",
            "Kuwait 	+965",
            "Kyrgyzstan (Kyrgyz Republic) (Former Kirghiz Soviet Socialist Republic)	+996",

            "Lao People's Democratic Republic (Laos)	+856",
            "Latvia (Former Latvian Soviet Socialist Republic)	+371",
            "Lebanon 	+961",
            "Lesotho (Former Basutoland)	+266",
            "Liberia 	+231",
            "Libya (Libyan Arab Jamahiriya)	+218",
            "Liechtenstein 	+423",
            "Lithuania (Former Lithuanian Soviet Socialist Republic)	+370",
            "Luxembourg 	+352",
            "Macedonia, The Former Yugoslav Republic of	+389",
            "Madagascar (Former Malagasy Republic)	+261",

            "Malaysia 	+60",
            "Maldives 	+960",
            "Mali (Former French Sudan and Sudanese Republic) 	+223",
            "Mauritius 	+230",
            "Mexico 	+52",
            "Mongolia (Former Outer Mongolia)	+976",
            "Morocco 	+212",
            "Myanmar, Union of (Former Burma)	+95",

            "Nepal 	+977",
            "Netherlands 	+31",
            "New Zealand (Aotearoa) 	+64",
            "Niger 	+227",
            "Nigeria 	+234",
            "Norway 	+47",

            "Oman, Sultanate of (Former Muscat and Oman)	+968",

            "Pakistan (Former West Pakistan)	+92",
            "Papua New Guinea (Former Territory of Papua and New Guinea)	+675",
            "Paraguay 	+595",
            "Peru 	+51",
            "Philippines 	+63",
            "Poland 	+48",
            "Portugal 	+351",
            "Puerto Rico 	+1-787 or +1-939",

            "Qatar, State of 	+974",

            "Romania 	+40",

            "Russian Federation 	+7",

            "Saudi Arabia 	+966",

            "Singapore 	+65",
            "Slovakia	+421",
            "Slovenia 	+386",
            "Somalia (Former Somali Republic, Somali Democratic Republic) 	+252",
            "South Africa (Former Union of South Africa)	+27",
            "Spain 	+34",
            "Sri Lanka (Former Serendib, Ceylon) 	+94",
            "Sudan (Former Anglo-Egyptian Sudan) 	+249",
            "Sweden 	+46",
            "Switzerland 	+41",
            "Syria (Syrian Arab Republic) (Former United Arab Republic - with Egypt)	+963",

            "Taiwan (Former Formosa)	+886",
            "Tajikistan (Former Tajik Soviet Socialist Republic)	+992",
            "Thailand (Former Siam)	+66",
            "Tokelau 	+690",
            "Tonga, Kingdom of (Former Friendly Islands)	+676",
            "Trinidad and Tobago 	+1-868",
            "Tromelin Island",
            "Tunisia 	+216",
            "Turkey 	+90",
            "Turkmenistan (Former Turkmen Soviet Socialist Republic)	+993",
            "Turks and Caicos Islands 	+1-649",
            "Tuvalu (Former Ellice Islands)	+688",

            "Uganda, Republic of	+256",
            "Ukraine (Former Ukrainian National Republic, Ukrainian State, Ukrainian Soviet Socialist Republic)	+380",
            "United Arab Emirates (UAE) (Former Trucial Oman, Trucial States)	+971",
            "United Kingdom (Great Britain / UK)	+44",
            "United States 	+1",
            "Uruguay, Oriental Republic of (Former Banda Oriental, Cisplatine Province)	+598",
            "Uzbekistan (Former UZbek Soviet Socialist Republic)	+998",

            "Vatican City State (Holy See)	+418",
            "Venezuela 	+58",
            "Vietnam 	+84",
            "Yemen	+967",

            "Zimbabwe, Republic of (Former Southern Rhodesia, Rhodesia) 	+263"};
    EditText phnum;
    TextView authview;
    private FirebaseAuth mAuth;
    String mVerificationCode;
    Editable pnum;
    ProgressDialog pd;
    boolean status=true;
    private Handler progressBarbHandler = new Handler();
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    boolean mVerificationInProgress;
    ProgressDialog authwait;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_auth);
        /////
        authwait=new ProgressDialog(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
        String uid=pref.getString("uid", "");
        if (!pref.getString("uid", "").equals("")) {

            Runnable runnable = new Runnable() {
                public void run() {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                //    new ContactSync().syncContact(AuthenticationActivity.this,pref.getString("uid",""));
                }
            };
            Thread mythread = new Thread(runnable);
            mythread.start();

            startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
            finish();
        }
        /////

        Button btn=(Button)findViewById(R.id.auth_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyNumber();
                final Dialog dialog = new Dialog(AuthenticationActivity.this);
                dialog.setContentView(R.layout.enter_otp_dialog);
                dialog.show();
                Button enter_otp=(Button)dialog.findViewById(R.id.enter);
                enter_otp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView otpView;
                        otpView = dialog.findViewById(R.id.otpview);
                        String get_otp= otpView.getText().toString();
                        verifyPhoneNumberWithCode(mVerificationCode,get_otp);
                    }
                });
            }
        });

        phnum=(EditText)findViewById(R.id.editText2);
        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,countryNames);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);

        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        authview=(TextView) findViewById(R.id.textView);
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
               authview.setText("The authentication success!!");
                signInWithPhoneAuthCredential(credential);

            }


            @Override
            public void onVerificationFailed(FirebaseException e) {

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                   authview.setText("Invalid phone number...");
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    authview.setText("Too many requests");
                }
            }
            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mResendToken = token;
                mVerificationCode=verificationId;
            }
        };
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(), countryNames[position], Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    boolean checkStatus()
    {
        return status;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        final boolean[] userfound = {false};

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            //////////
                            final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                            final DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();

                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Log.e("AUTH","inside taskcomplete");

                            if(isNew!=true)
                            {
                                Log.e("AUTH","inside not isnew");

                                usersRef.child("NUM_ID").child(new ContactSync().getSHA(String.valueOf(pnum)) ).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        Log.e("AUTH","inside ondatachange");
                                        if(dataSnapshot.getValue()!=null)
                                        {
                                            Log.e("AUTH","inside ds!=null");
                                            uid=dataSnapshot.child("uid").getValue().toString();
                                          //  String enc_key=dataSnapshot.child("enc_key").getValue().toString();
                                            SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                                            SharedPreferences.Editor uidpref=pref.edit();
                                            uidpref.putString("uid", uid);
                                           // uidpref.putString("enc_key", enc_key);
                                            uidpref.commit();
                                            createExchangeKey();
                                            nextActivity();
                                        }
                                        else
                                            {
                                            Log.e("AUTH","inside setnewnode");
                                            setNewNode();

                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e("AUTH","inside oncancelled "+databaseError);

                                    }
                                });
                            }
                            else
                            {
                                Log.e("AUTH","inside outer else setnewnode");
                                setNewNode();
                            }
                            Log.e("AUTH","going inside intent");


                        } else {
                            // Sign in failed, display a message and update the UI
                            authview.setText("Failed to authenciate");
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                            }
                        }
                    }
                });

    }

    void setNewNode()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //////////
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference();

        Log.e("AUTH","inside setnewnode");

        rootRef.child("num_users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                Log.e("AUTH","inside setnewnode firebase");
                uid= (Integer.parseInt(dataSnapshot.getValue().toString())+1)+"";
                String enc_key=random();
                userRef.child("NUM_ID").child(new ContactSync().getSHA(String.valueOf(pnum))).child("uid").setValue(uid);
                userRef.child("NUM_ID").child(new ContactSync().getSHA(String.valueOf(pnum))).child("enc_key").setValue(enc_key);
                rootRef.child("num_users").setValue(uid);
                SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                SharedPreferences.Editor uidpref=pref.edit();

                uidpref.putString("uid", uid);
                uidpref.putString("enc_key", enc_key);
                uidpref.commit();
                createExchangeKey();
                nextActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    void createExchangeKey()
    {
        RSAKeyExchange keyExchange=new RSAKeyExchange(this,uid);
        try
        {
            keyExchange.generateKeyPair();
            keyExchange.writeMyKeys();
            keyExchange.uploadPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PANI PAALI","Key generation is "+e.toString());
        }
    }

    void nextActivity()
    {
        authwait.dismiss();
        startActivity(new Intent(AuthenticationActivity.this,AccountSetup.class));
        finish();
    }

    private void startPhoneNumberVerification(String phoneNumber)
    {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = phnum.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this,"Invalid phone number.", Toast.LENGTH_LONG).show();
            //mPhoneNumberField.setTextColor(Color.parseColor("#ff1744"));
            return false;
        }

        return true;
    }

    public static String random()
    {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(6);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }


    void verifyNumber()
    {
        authwait.show();
        authwait.setCancelable(false);

        if (!validatePhoneNumber()) {
            return;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            //we are connected to a network

        }
        else
        {
            authwait.dismiss();
            Toast.makeText(this,"No Internet Connection", Toast.LENGTH_LONG).show();
            return;
        }


        ///////hide keyboard start
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        /////////hide keyboard end


        //mStatusText.setText("Authenticating....!");
        authview.setText("Code waiting...");
        startPhoneNumberVerification(phnum.getText().toString());
        pnum=phnum.getText();
    }


}
