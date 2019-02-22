package com.example.agent_47.helpathandtest1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.StringTokenizer;
import com.example.agent_47.helpathandtest1.Request.*;

public class ListActivity extends AppCompatActivity  {
    public static Request mRequest;
    String userLocation = null;
    String userService = null;
    public static final String EXTRA_MESSAGE_NAME = "LIST_ACTIVITY_NAME";
    public static final String EXTRA_MESSAGE_ADDRESS = "LIST_ACTIVITY_ADDRESS";
    public static final String EXTRA_MESSAGE_DISTANCE = "LIST_ACTIVITY_DISTANCE";
    public static final String EXTRA_MESSAGE_DESCRIPTION = "LIST_ACTIVITY_DESCRIPTION";
    public static final String EXTRA_MESSAGE_RATING = "LIST_ACTIVITY_RATING";
    public static final String EXTRA_MESSAGE_LOCATION = "LIST_ACTIVITY_LOCATION";
    public static final String EXTRA_MESSAGE_USER_LOCATION = "LIST_ACTIVITY_USER_LOCATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent = getIntent();
        userLocation = intent.getStringExtra(MainActivity.EXTRA_MESSAGE1);
        userService = intent.getStringExtra(MainActivity.EXTRA_MESSAGE2);
        Toast.makeText(this
                , "Location : "+userLocation+"\nService : "+userService
                , Toast.LENGTH_LONG)
                .show();

        final Help[] helpArray = connectDatabase();
        if (helpArray == null) {
            Toast.makeText(this,"No Value Exists",Toast.LENGTH_SHORT).show();
        }
        else {
            int SIZE = helpArray.length;
            String[] strAllSortedName = new String[SIZE];
            for (int i = 0; i < SIZE; i++) {
                strAllSortedName[i] = helpArray[i].getName();
            }
            ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(this,
                    R.layout.list_view, strAllSortedName);
            ListView listView = (ListView) findViewById(R.id.string_list);
            listView.setAdapter(mArrayAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        String name = helpArray[i].getName();
                        String location = helpArray[i].getLocation();
                        String rating = Double.toString((Double)helpArray[i].calculateRating());
                        String distance = Double.toString((double) helpArray[i].getDistance());
                        String description = helpArray[i].getDescription();
                        String address = helpArray[i].getAddress();

                        Intent intent2 = new Intent(ListActivity.this,Description.class);
                        intent2.putExtra(EXTRA_MESSAGE_NAME,name);
                        intent2.putExtra(EXTRA_MESSAGE_ADDRESS,address);
                        intent2.putExtra(EXTRA_MESSAGE_DESCRIPTION,description);
                        intent2.putExtra(EXTRA_MESSAGE_DISTANCE,distance);
                        intent2.putExtra(EXTRA_MESSAGE_RATING,rating);
                        intent2.putExtra(EXTRA_MESSAGE_LOCATION,location);
                        intent2.putExtra(EXTRA_MESSAGE_USER_LOCATION,userLocation);
                        startActivity(intent2);
            }
            });
        }

    }


    public Help[] connectDatabase(){
        String FILENAME = "test.db";
        Help[] helpArray = null;

        SharedPreferences sharedPref = ListActivity.this.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.Installed);
        int instl = sharedPref.getInt(getString(R.string.Installed), Integer.parseInt(defaultValue));

        if (instl == 0) {
            ////////////////////Database
            InputStream testInputStream = getResources().openRawResource(R.raw.dbfile);
            BufferedInputStream bis = new BufferedInputStream(testInputStream);

//        Context context = getApplicationContext();
            FileOutputStream fos = null;
            try {
                fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                int b;
                while ((b = bis.read()) != -1) {
                    fos.write(b);
                }
                fos.close();
                bis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }


            //////////////
            sharedPref = ListActivity.this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(getString(R.string.Installed), 1);
            editor.commit();
        }
        //////////////connection
        File testFile = getApplicationContext().getFilesDir();
        File file = new File(testFile, FILENAME);
        try {
            Database mDatabase = new Database(file.getPath());
            //////////////get help for sorted list
            Catagory mCatagory = new Catagory(mDatabase);
            Location mLocation = new Location(mDatabase);
            Path mPath = new Path(mDatabase,mLocation);
            Service mService = new Service(mDatabase, mLocation, mCatagory);
            Source mSource = new Source(mDatabase, mLocation);
            mRequest = new Request(mDatabase, mSource, mService, mPath, mLocation, mCatagory);
            helpArray = mRequest.getHelps(userLocation,userService);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return helpArray;
    }
}
