package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.Inet4Address;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail,UserPassword;
    private TextView AlreadyHaveAnAccount;
    private String email,password;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();


        Initialize();
        AlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             SendToLoginActivity();
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateNewAccount();
            }
        });





    }



    private void Initialize() {
        CreateAccountButton = (Button) findViewById(R.id. register_button);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        AlreadyHaveAnAccount = (TextView) findViewById(R.id.already_have_an_account);
        loadingBar = new ProgressDialog(this);
    }

    private void CreateNewAccount(){

        email = UserEmail.getText().toString();
        password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(RegisterActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
        }


        if(TextUtils.isEmpty(password)){
            Toast.makeText(RegisterActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }

        else{

            loadingBar.setTitle("Create New Account");
            loadingBar.setMessage("Please wait,while we are creating an account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();



            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                String CurrentUserId = mAuth.getCurrentUser().getUid();
                                RootRef.child("Users").child(CurrentUserId).setValue("");
                                RootRef.child("Users").child(CurrentUserId).child("device_token").setValue(deviceToken);
                                SendToMainActivity();
                                Toast.makeText(RegisterActivity.this,"Account Created Successfully",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                            else{
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                        }
                    });
        }

    }

    private void SendToLoginActivity(){
        Intent loginintent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginintent);
    }

    private void SendToMainActivity(){
        Intent mainintent = new Intent(RegisterActivity.this,MainActivity.class);
        mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
        finish();
    }




}