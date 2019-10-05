package com.devsoftzz.doctorassist;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devsoftzz.doctorassist.Models.Place;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.rupins.drawercardbehaviour.CardDrawerLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class Hospital_Detailed extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CardDrawerLayout drawer;
    private TextView mName,mAddress,mRating;
    private Button mGet;
    private RatingBar mRat;
    private CircleImageView mLogo,mNavigaton;
    private ImageView mImage;
    private Window window;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital__detailed);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
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

        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);

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

        String data = getIntent().getStringExtra("Data");
        final Place.Result result = new Gson().fromJson(data, Place.Result.class);
        final String Lat = getIntent().getStringExtra("Lat");
        final String Lng = getIntent().getStringExtra("Lng");

        mName = findViewById(R.id.hospitalName);
        mAddress = findViewById(R.id.address);
        mRating = findViewById(R.id.ratingText);
        mGet = findViewById(R.id.getAppointment);
        mRat = findViewById(R.id.ratingBar);
        mLogo = findViewById(R.id.hospitalIcon);
        mNavigaton = findViewById(R.id.navigation);
        mImage = findViewById(R.id.expandedImage);

        mName.setText(result.getName());
        mAddress.setText(result.getVicinity());
        mRating.setText(String.valueOf(result.getRating())+"/5.0");

        mGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Hospital_Detailed.this,SlotBooking.class);
                intent.putExtra("HospitalId",result.getId());
                intent.putExtra("HospitalName",result.getName());
                startActivity(intent);
            }
        });

        mRat.setRating(result.getRating());
        Glide.with(getApplicationContext()).load(result.getIcon()).into(mLogo);
        try {
            Glide.with(getApplicationContext()).load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+result.getPhotos().get(0).getPhotoReference()+"&key=AIzaSyCHnirJxrTjyh0JYG9HZOe5RazRhtjYFl0").into(mImage);
        }
        catch (Exception e)
        {
                mImage.setVisibility(View.GONE);
                e.printStackTrace();
        }

        mNavigaton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr="+Lat+","+Lng+"&daddr="+result.getGeometry().getLocation().getLat()+","+result.getGeometry().getLocation().getLng()));
                startActivity(intent);
            }
        });
    }


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
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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


