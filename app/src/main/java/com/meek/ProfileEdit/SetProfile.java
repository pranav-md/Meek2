package com.meek.ProfileEdit;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.meek.R;

public class SetProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        checkNameListner();
        Button btn=(Button)findViewById(R.id.enter_details);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    void checkNameListner()
    {
        EditText enter_name=(EditText)findViewById(R.id.enter_name);
        enter_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String name=editable.toString();
                DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                db_ref.child("NAMES").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        EditText enter_name=(EditText)findViewById(R.id.enter_name);
                        TextView status=(TextView)findViewById(R.id.status);

                        if(dataSnapshot.exists())
                        {
                            enter_name.setTextColor(Color.RED);
                            status.setText("Name unavailable");
                            status.setTextColor(Color.RED);
                        }
                        else
                        {

                            enter_name.setTextColor(Color.GREEN);
                            status.setText("Name available");
                            status.setTextColor(Color.GREEN);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

    }
}
