package com.devsoftzz.doctorassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devsoftzz.doctorassist.R;
import com.devsoftzz.doctorassist.SearchActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rupins.drawercardbehaviour.CardDrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , AdapterView.OnItemSelectedListener{

    private CardDrawerLayout drawer;
    TextView username;
    FirebaseUser user;
    private Spinner spinner;
    private Button search;
    private SharedPreferences pref;
    private String item;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username= findViewById(R.id.usernamemain);
        user= FirebaseAuth.getInstance().getCurrentUser();
        pref = getSharedPreferences("ROG",MODE_PRIVATE);

        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String nnn= String.valueOf(dataSnapshot.child("Name").getValue());
                    username.setText("Hello, "+ nnn);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                window.setStatusBarColor(Color.rgb(33,150,243));
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                window.setStatusBarColor(Color.rgb(255,87,34));
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);


        spinner = findViewById(R.id.spinner);
        search = findViewById(R.id.search);

        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Click To Choose");
        categories.add("Dentist");
        categories.add("Cardiologist");
        categories.add("Orthopedic");
        categories.add("Neurosurgeon");
        categories.add("Gynecologist");
        categories.add("Neurologist");
        categories.add("Physiotherapist");
        categories.add("Ayurvedic Practioner");
        categories.add("Radiologist");
        categories.add("Family Medicine Physician");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("Type",item);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Dicease",item);
                editor.apply();
                startActivity(intent);
            }
        });

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        if(position==0){
            search.setClickable(false);
            search.setBackgroundResource(R.drawable.button_disabled);
        }else {
            search.setClickable(true);
            search.setBackgroundResource(R.drawable.button_rnd_org);
            item = parent.getItemAtPosition(position).toString();
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}