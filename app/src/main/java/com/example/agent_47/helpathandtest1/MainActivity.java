package com.example.agent_47.helpathandtest1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.*;
import com.example.agent_47.helpathandtest1.ListActivity.*;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE1 = "Intent.Extra.MESSAGE.LOCATION";
    public static final String EXTRA_MESSAGE2 = "Intent.Extra.MESSAGE.SERVICE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        final MultiAutoCompleteTextView textView = findViewById(R.id.simpleMultiAutoCompleteTextView);
        textView.setAdapter(adapter);
        textView.setTokenizer(new SpaceTokenizer());

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, SERVICES);
        final MultiAutoCompleteTextView textView2 = findViewById(R.id.simpleMultiAutoCompleteTextView2);
        textView2.setAdapter(adapter2);
        textView2.setTokenizer(new SpaceTokenizer());

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MainActivity.this
//                        , textView.getText().toString()+" "+textView2.getText().toString()
//                        , Toast.LENGTH_LONG)
//                        .show();
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
//                String message = textView.getText().toString()+" + "+textView2.getText().toString();
                String userLocation = new StringTokenizer(textView.getText().toString()).nextToken();
                String userService = new StringTokenizer(textView2.getText().toString()).nextToken();

                intent.putExtra(EXTRA_MESSAGE1, userLocation);
                intent.putExtra(EXTRA_MESSAGE2, userService);
                startActivity(intent);
            }
        });
        Button button_exit = (Button)findViewById(R.id.button2);
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                try {
                    ListActivity.mRequest.getDatabase().close();
                } catch(SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    private static final String[] COUNTRIES = new String[] {
            "bahaddarhat_chittagong", "muradpur_chittagong", "2_no._gate_chittagong", "gec_chittagong"
    };
    private static final String[] SERVICES = new String[] {
            "bus_stop", "bank", "atm_booth", "police_station", "hospital", "hotel", "school",
            "customer_care", "restaurant"
    };
}
