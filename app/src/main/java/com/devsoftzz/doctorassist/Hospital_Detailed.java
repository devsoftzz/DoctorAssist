package com.devsoftzz.doctorassist;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital__detailed);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);

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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
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
}


