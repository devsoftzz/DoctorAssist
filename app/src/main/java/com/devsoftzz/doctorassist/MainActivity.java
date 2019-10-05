package com.devsoftzz.doctorassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.devsoftzz.doctorassist.LogIn.MainLogin;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private CardView mUser;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser==null)
        {
            Intent it = new Intent(MainActivity.this, MainLogin.class);
            it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
            finish();
        }

        username= findViewById(R.id.usernamemain);
        user= FirebaseAuth.getInstance().getCurrentUser();
        pref = getSharedPreferences("ROG",MODE_PRIVATE);
        mUser = findViewById(R.id.userdetails);

        handler = new Handler();
        mUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,UserDetailsActivity.class);
                intent.putExtra("Home",true);
                startActivity(intent);
            }
        });
        username.setText("Hello,");
        FirebaseDatabase.getInstance().getReference("Users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String nnn= String.valueOf(dataSnapshot.child("Name").getValue());
                    username.setText("Hello, "+ nnn);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}});

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
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                window.setStatusBarColor(Color.rgb(255,87,34));
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);
        spinner = findViewById(R.id.spinner);
        search = findViewById(R.id.search);
        spinner.setOnItemSelectedListener(this);

        final SharedPreferences reminder = getSharedPreferences("Reminder",MODE_PRIVATE);
        Menu menu = navigationView.getMenu();
        SwitchCompat s=(SwitchCompat) MenuItemCompat.getActionView(menu.findItem(R.id.reminder)).findViewById(R.id.switchReminder);
        if(reminder.getBoolean("Reminder",true)){
            s.setChecked(true);
        }else {
            s.setChecked(false);
        }
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = reminder.edit();
                if(isChecked){
                    editor.putBoolean("Reminder",true);
                    delay();
                }else {
                    editor.putBoolean("Reminder",false);
                    delay();
                }
                editor.commit();
            }
        });
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

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if(position==0){
            search.setClickable(false);
            search.setBackgroundResource(R.drawable.button_disabled);
        }else {
            search.setClickable(true);
            search.setBackgroundResource(R.drawable.button_rnd_org);
            item = parent.getItemAtPosition(position).toString();
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {}

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            startActivity(new Intent(this,MainActivity.class));
            delay();
            finish();
        } else if (id == R.id.appointment) {
            startActivity(new Intent(this,AppointmentsActivity.class));
            delay();
            finish();
        }
        return true;
    }
    public void delay(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                drawer.closeDrawer(GravityCompat.START);
            }
        },200);
    }
}