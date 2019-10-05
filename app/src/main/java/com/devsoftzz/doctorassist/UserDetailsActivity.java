package com.devsoftzz.doctorassist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.devsoftzz.doctorassist.LogIn.MainLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UserDetailsActivity extends AppCompatActivity {


    EditText username,from,age,weight;
    Button register;
    FirebaseUser user;
    DatabaseReference ref,ref2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);


        username = findViewById(R.id.username);
        register = findViewById(R.id.register);
        from = findViewById(R.id.from);
        age = findViewById(R.id.age);
        weight = findViewById(R.id.weight);


         user = FirebaseAuth.getInstance().getCurrentUser();
        ref= FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());


         register.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {


                 HashMap<String,Object> mapdata = new HashMap<>();
                 mapdata.put("id",user.getUid());
                 mapdata.put("name",username.getText().toString());
                 mapdata.put("from",from.getText().toString());
                 mapdata.put("age",age.getText().toString());
                 mapdata.put("weight",weight.getText().toString());
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
