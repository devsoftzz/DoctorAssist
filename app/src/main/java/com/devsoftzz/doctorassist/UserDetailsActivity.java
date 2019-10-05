package com.devsoftzz.doctorassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.devsoftzz.doctorassist.LogIn.MainLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class UserDetailsActivity extends AppCompatActivity {

    EditText username,age;
    Button register;
    FirebaseUser user;
    DatabaseReference ref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        username = findViewById(R.id.username);
        register = findViewById(R.id.register);
        age = findViewById(R.id.age);


        user = FirebaseAuth.getInstance().getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        if(getIntent().getBooleanExtra("Home",false)){
            final ProgressDialog dialog = ProgressDialog.show(this,"Please Wait","Fetching Data",false,false);
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = String.valueOf(dataSnapshot.child("Name").getValue());
                    String age1 = String.valueOf(dataSnapshot.child("Age").getValue());
                    username.setText(name);
                    age.setText(age1);
                    dialog.dismiss();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            });
        }
        register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(username.getText().toString().trim().length()==0){
                     Toast.makeText(getApplicationContext(),"Enter Name",Toast.LENGTH_LONG).show();
                     return;
                 }if(age.getText().toString().trim().length()==0 || age.getText().toString().trim().length()>2){
                     Toast.makeText(getApplicationContext(),"Enter Age Properly",Toast.LENGTH_LONG).show();
                     return;
                 }
                 HashMap<String,Object> mapdata = new HashMap<>();
                 mapdata.put("id",user.getUid());
                 mapdata.put("Name",username.getText().toString());
                 mapdata.put("Age",age.getText().toString());
                 mapdata.put("Phone Number",user.getPhoneNumber().toString());
                 ref.setValue(mapdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful())
                         {
                             Intent it= new Intent(UserDetailsActivity.this,MainActivity.class);
                             it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                             startActivity(it);
                             finish();
                         }
                         else
                         {
                             Toast.makeText(UserDetailsActivity.this,"Verification Unsuccessful",Toast.LENGTH_LONG).show();
                         }
                     }
                 });
            }
         });
    }
}
