package com.meek.AccountManage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.googlecode.mp4parser.authoring.Edit;
import com.meek.Database.PeopleDBHelper;
import com.meek.MainActivity;
import com.meek.R;

import java.util.Random;

public class AccountSetup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setup);
        Button enter_button=(Button)findViewById(R.id.enter);
        enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name=(EditText)findViewById(R.id.enter_name);

                SharedPreferences pref = getApplicationContext().getSharedPreferences("UserDetails", MODE_PRIVATE);
                SharedPreferences.Editor edit=pref.edit();

                edit.putString("Name",name.getText().toString());
                edit.commit();
                DatabaseReference act_ref = FirebaseDatabase.getInstance().getReference();
                act_ref.child("NAMES").child(pref.getString("uid","")).setValue(name.getText().toString());
                String server_key=random();

                act_ref.child("Server_Key").child(pref.getString("uid","")).setValue(server_key);

                act_ref.child("Users").child(pref.getString("uid","")).child("Details2").child("name").setValue(server_key);
                if(!new PeopleDBHelper(AccountSetup.this,server_key).checkTable())
                    new PeopleDBHelper(AccountSetup.this,server_key).createTable();
                new PeopleDBHelper(AccountSetup.this,server_key).insertPerson(pref.getString("uid",""),name.getText().toString()," ",0);
                Intent intent=new Intent(AccountSetup.this,EnterKey.class);
                intent.putExtra("ServerKey",server_key);
                startActivity(intent);
            }
        });

    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = 20+generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
