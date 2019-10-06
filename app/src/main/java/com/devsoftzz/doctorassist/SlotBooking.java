package com.devsoftzz.doctorassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.devsoftzz.doctorassist.Database.DatabaseHandler;
import com.devsoftzz.doctorassist.Models.AppointmentPojo;
import com.devsoftzz.doctorassist.Models.JSONParser;
import com.devsoftzz.doctorassist.Models.RandomString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class SlotBooking extends AppCompatActivity implements View.OnClickListener, PaytmPaymentTransactionCallback {

    private TextView date;
    String datestring,dt2;
    private FirebaseAuth mAuth;
    String HospitalName;
    private DatabaseReference mdatabase;
    private int mYear,mMonth,mDay;
    private SharedPreferences sharedPreferences;
    private String MID,ORDERID,CUNSOMERID;
    private String TIME;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_booking);

        String HospitalId = getIntent().getStringExtra("HospitalId");
        HospitalName = getIntent().getStringExtra("HospitalName");
        date = findViewById(R.id.date);
        sharedPreferences = getSharedPreferences("ROG",MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();
        mdatabase = FirebaseDatabase.getInstance().getReference("Hospitals").child(HospitalId);


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


                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()/*- (1000 * 60 * 60 * 24)*/);
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

        if(dayOfMonth/10 ==0)
        {
            date.setText("0"+dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            dt2= "0"+dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            datestring = "0"+ dayOfMonth + "_" + (monthOfYear + 1) + "_" + year;
        }
        else
        {
            date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
            dt2= dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            datestring = dayOfMonth + "_" + (monthOfYear + 1) + "_" + year;
        }


    }

    @Override
    public void onClick(View v) {

        if(date.getText().equals("Select Date")){
            Toast.makeText(getApplicationContext(),"Select Date First",Toast.LENGTH_LONG).show();
            return;
        }
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
        TIME = time;
        mdatabase.child(datestring).child(time).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {
                    Toast.makeText(SlotBooking.this,"Slot Already Booked",Toast.LENGTH_LONG).show();
                }
                else
                {
                    if (ContextCompat.checkSelfPermission(SlotBooking.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SlotBooking.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                    }
                    payment();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void payment() {
        RandomString randomString = new RandomString();
        ORDERID = randomString.getAlphaNumericString(40);
        CUNSOMERID = randomString.getAlphaNumericString(40);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        MID = "heGGTZ50771034143755";
        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        private ProgressDialog dialog = new ProgressDialog(SlotBooking.this);
        //private String orderId , mid, custid, amt;
        String url ="https://doctorassist123.000webhostapp.com/mydata/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;
        String CHECKSUMHASH ="";
        @Override
        protected void onPreExecute() {
//            this.dialog.setMessage("Please wait");
//            this.dialog.show();
        }
        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(SlotBooking.this);
            String param=
                    "MID="+MID+
                            "&ORDER_ID=" + ORDERID+
                            "&CUST_ID="+ CUNSOMERID+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=100&WEBSITE=WEBSTAGING"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";
            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {
                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }
        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            PaytmPGService Service = PaytmPGService.getStagingService();
            HashMap<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("MID", MID); //MID provided by paytm
            paramMap.put("ORDER_ID", ORDERID);
            paramMap.put("CUST_ID", CUNSOMERID);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", "100");
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");
            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            Service.startPaymentTransaction(SlotBooking.this, true, true,SlotBooking.this);
        }
    }

    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
        HashMap<String,String> mapp= new HashMap<>();
        mapp.put("User Id",mAuth.getCurrentUser().getUid());
        mdatabase.child(datestring).child(TIME).setValue(mapp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                DatabaseHandler databaseHandler = new DatabaseHandler(getApplicationContext());
                databaseHandler.addAppoinment(new AppointmentPojo(HospitalName,dt2,TIME));
                startActivity(new Intent(SlotBooking.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                Toast.makeText(SlotBooking.this,"Booked Successfully",Toast.LENGTH_LONG).show();
            }
        });

    }
    @Override
    public void networkNotAvailable() {
    }
    @Override
    public void clientAuthenticationFailed(String s) {
    }
    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }
    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }
    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
    }
    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }

}
