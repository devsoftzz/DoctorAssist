package com.devsoftzz.doctorassist.LogIn;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;

import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import com.devsoftzz.doctorassist.MainActivity;
import com.devsoftzz.doctorassist.R;
import com.devsoftzz.doctorassist.UserDetailsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class MainLogin extends AppCompatActivity {

    private Button sendcode,enter;
    private String verificationId;
    TextInputLayout phoneout,codeout;
    FirebaseAuth mAuth;
    TextInputEditText ph_num,code_text;
    FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);


        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        ph_num = findViewById(R.id.phone_number);
        code_text = findViewById(R.id.verification_code);
        enter= findViewById(R.id.enter);
        sendcode =findViewById(R.id.sendcode);
        phoneout = findViewById(R.id.phoneout);
        codeout = findViewById(R.id.codeout);

        sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(ph_num.getText().toString().isEmpty())
                {
                        ph_num.setError("Enter Phone Number");
                        ph_num.requestFocus();
                        return;
                }else if(ph_num.getText().toString().length()!=10){
                    ph_num.setError("Enter Phone Number Properly");
                    ph_num.requestFocus();
                    return;
                }
                sendVerificationCode("+91" + ph_num.getText().toString());
                phoneout.setVisibility(View.GONE);
                sendcode.setVisibility(View.GONE);
                codeout.setVisibility(View.VISIBLE);
                enter.setVisibility(View.VISIBLE);
            }
        });

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = code_text.getText().toString().trim();

                if (code.isEmpty() || code.length() < 6) {
                    code_text.setError("Enter Code Properly");
                    code_text.requestFocus();
                    return;
                }
                verifyCode(code);
            }
        });
    }
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }
    private void signInWithCredential(final PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            final String userid = user.getUid();
                            final DatabaseReference ref  = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists())
                                    {
                                        Toast.makeText(MainLogin.this,"Welcome Again",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(MainLogin.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Intent intent = new Intent(MainLogin.this, UserDetailsActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {}
                            });
                        } else {
                            Toast.makeText(MainLogin.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void sendVerificationCode(String number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                120,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallBack
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                code_text.setText(code);
                verifyCode(code);
            }
        }
        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(MainLogin.this, "Data Request Executed", Toast.LENGTH_LONG).show();
        }
    };





}