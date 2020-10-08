package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton,phoneLoginButton;
    private EditText UserEmail,UserPassword;
    private FirebaseAuth mAuth;
    private ProgressDialog loading;
    private TextView NeedNewAccountLink,ForgetPassword;
    private DatabaseReference usersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Initialize();

        NeedNewAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                 startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });

        phoneLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,PhoneLoginActivity.class);
                startActivity(intent);
            }
        });




    }


    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
           SendToMainActivity();
        }
    }


    private void AllowUserToLogin() {

       String email = UserEmail.getText().toString();
       String password = UserPassword.getText().toString();



        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this,"Please enter your email",Toast.LENGTH_SHORT).show();
        }


        if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this,"Please enter your password",Toast.LENGTH_SHORT).show();
        }

        else{
            loading.setTitle("Sign In");
            loading.setMessage("Please wait.. Logging In.. ");
            loading.setCanceledOnTouchOutside(true);
            loading.show();

            mAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String currentUserId = mAuth.getCurrentUser().getUid();
                                String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                usersRef.child(currentUserId).child("device_token").setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful())
                                                {
                                                    SendToMainActivity();
                                                    Toast.makeText(LoginActivity.this,"Logged in successfully",Toast.LENGTH_SHORT).show();
                                                    loading.dismiss();
                                                }
                                            }
                                        });


                            }
                            else{
                                String message = task.getException().toString();
                                Toast.makeText(LoginActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                                loading.dismiss();

                            }
                        }
                    });

        }

    }

    private void Initialize() {
        loginButton = (Button) findViewById(R.id.login_button);
        phoneLoginButton = (Button) findViewById(R.id.phone_number);
        UserEmail = (EditText) findViewById(R.id.login_email);
        UserPassword = (EditText) findViewById(R.id.login_password);
        NeedNewAccountLink = (TextView) findViewById(R.id.need_new_account);
        ForgetPassword = (TextView) findViewById(R.id.forget_password_link);
        loading = new ProgressDialog(this);

    }



    private void SendToMainActivity(){
        Intent mainintent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(mainintent);
        finish();
    }


}