package com.meek;

import android.app.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

/**
 * Created by User on 08-Jun-18.
 */

public class DestinationPlace extends AppCompatActivity {

    private static final String TAG ="DEST_PLACE" ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_destination);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                placeSelectSet(place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

    }

    void placeSelectSet(final Place place)
    {
        LinearLayout place_sel=(LinearLayout)findViewById(R.id.place_select);
        place_sel.setVisibility(View.VISIBLE);
        TextView setplace=(TextView)findViewById(R.id.place_name);
        setplace.setText(place.getName()+"");
        Button done=(Button)findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("Place name",place.getName());
                returnIntent.putExtra("Place selected",true);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });

    }
}
