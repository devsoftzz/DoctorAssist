package com.devsoftzz.doctorassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.devsoftzz.doctorassist.Database.DatabaseHandler;
import com.devsoftzz.doctorassist.Models.AppointmentPojo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;

public class SlotBooking extends AppCompatActivity implements View.OnClickListener {

    private TextView date;
    String datestring,dt2;
    private FirebaseAuth mAuth;
    String HospitalName;
    private DatabaseReference mdatabase;
    private int mYear,mMonth,mDay;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_booking);


        String HospitalId = getIntent().getStringExtra("HospitalId");
        HospitalName = getIntent().getStringExtra("HospitalName");
        date = findViewById(R.id.date);
        sharedPreferences = getSharedPreferences("ROG",MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference("Hospitals").child(HospitalId).child(sharedPreferences.getString("Dicease","Physiotherapist"));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(SlotBooking.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                updateDate(year,monthOfYear,dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);


                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()- (1000 * 60 * 60 * 24));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis()+ (1000 * 60 * 60 * 24 * 15));
                datePickerDialog.show();

            }
        });


        findViewById(R.id.slot1).setOnClickListener(this);
        findViewById(R.id.slot2).setOnClickListener(this);
        findViewById(R.id.slot3).setOnClickListener(this);
        findViewById(R.id.slot4).setOnClickListener(this);
        findViewById(R.id.slot5).setOnClickListener(this);
        findViewById(R.id.slot6).setOnClickListener(this);
    }

    private void updateDate(int year, int monthOfYear, int dayOfMonth) {
        date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        dt2=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        datestring = dayOfMonth + "_" + (monthOfYear + 1) + "_" + year;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.slot1:
                SendToDatabase("11_00");
                break;
            case R.id.slot2:
                SendToDatabase("11_30");
                break;
            case R.id.slot3:
                SendToDatabase("12_00");
                break;
            case R.id.slot4:
                SendToDatabase("12_30");
                break;
            case R.id.slot5:
                SendToDatabase("13_00");
                break;
            case R.id.slot6:
                SendToDatabase("13_30");
                break;
        }

    }


    void SendToDatabase(final String time)
    {
        mdatabase.child(datestring).child(time).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Toast.makeText(SlotBooking.this,"Slot Already Booked",Toast.LENGTH_LONG).show();
                }
                else
                {
                    HashMap<String,String> mapp= new HashMap<>();
                    mapp.put("User Id",mAuth.getCurrentUser().getUid());
                    mdatabase.child(datestring).child(time).setValue(mapp).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                            databaseHandler.addAppoinment(new AppointmentPojo(HospitalName,dt2,time));
                            startActivity(new Intent(SlotBooking.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            Toast.makeText(SlotBooking.this,"Booked Successfully",Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
