package com.example.agent_47.helpathandtest1;

import android.app.*;
import android.content.Intent;
import android.location.*;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.agent_47.helpathandtest1.Request.*;
import com.example.agent_47.helpathandtest1.ListActivity.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;


public class Description extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        TextView tvName = (TextView)findViewById(R.id.textview_name);
        TextView tvAddress = (TextView)findViewById(R.id.textview_address);
//        TextView tvLocation = (TextView)findViewById(R.id.textview_location);
        TextView tvDistance = (TextView)findViewById(R.id.textview_distance);
        TextView tvDescription = (TextView)findViewById(R.id.textview_description);
        TextView tvRating = (TextView)findViewById(R.id.textview_rating);
        final Intent intent = getIntent();
        tvName.setText(intent.getStringExtra(ListActivity.EXTRA_MESSAGE_NAME));
        tvAddress.setText(intent.getStringExtra(ListActivity.EXTRA_MESSAGE_ADDRESS));
//        tvLocation.setText(intent.getStringExtra(ListActivity.EXTRA_MESSAGE_LOCATION));
        tvDescription.setText(intent.getStringExtra(ListActivity.EXTRA_MESSAGE_DESCRIPTION));
        tvDistance.setText(intent.getStringExtra(ListActivity.EXTRA_MESSAGE_DISTANCE));
        tvRating.setText(intent.getStringExtra(ListActivity.EXTRA_MESSAGE_RATING));
        final String userLocation = intent.getStringExtra(ListActivity.EXTRA_MESSAGE_USER_LOCATION);
        final String location = intent.getStringExtra(ListActivity.EXTRA_MESSAGE_NAME )+ "_chittagong";
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new
                       Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?" +
                        "saddr=" + userLocation.replace("_"," ")+"&daddr=" +location.replace("_"," ")));
                intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                startActivity(intent);
            }
        });
        Button button_rate = (Button)findViewById(R.id.button_rate);
        button_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText)findViewById(R.id.edittext_rating);
                try {


                    boolean b = (ListActivity.mRequest.addRating(intent.getStringExtra(ListActivity.EXTRA_MESSAGE_NAME).trim(),
                            intent.getStringExtra(ListActivity.EXTRA_MESSAGE_LOCATION).trim(),
                            Double.parseDouble(editText.getText().toString()))
                    );
                    if (b == false){
                        Toast.makeText(Description.this
                                , "Wrong Rating"
                                , Toast.LENGTH_LONG)
                                .show();

                    }
                    else{

                        String name = intent.getStringExtra(ListActivity.EXTRA_MESSAGE_NAME).trim();
                        String loc_name = intent.getStringExtra(ListActivity.EXTRA_MESSAGE_LOCATION).trim();


                        double rat = ListActivity.mRequest.getService().getRating(name, loc_name);
                        int vote = ListActivity.mRequest.getService().getVote(name, loc_name);
                        Toast.makeText(Description.this
                                , "Rating successful"
                                , Toast.LENGTH_LONG)
                                .show();

                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
