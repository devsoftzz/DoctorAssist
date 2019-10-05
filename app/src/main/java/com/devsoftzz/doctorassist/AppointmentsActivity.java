package com.devsoftzz.doctorassist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.devsoftzz.doctorassist.Database.DatabaseHandler;
import com.devsoftzz.doctorassist.Models.Adapter_Appoinment;
import com.devsoftzz.doctorassist.Models.AppointmentPojo;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsActivity extends AppCompatActivity {



    DatabaseHandler db;
    RecyclerView recycleLayout;
    Adapter_Appoinment adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);


        db= new DatabaseHandler(this);
        List<AppointmentPojo> arrayList = new ArrayList<>();
        arrayList = db.getAllRecords();


        recycleLayout = findViewById(R.id.appointment_recycler);
        recycleLayout.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter_Appoinment(arrayList,this);
        recycleLayout.setAdapter(adapter);

    }
}
