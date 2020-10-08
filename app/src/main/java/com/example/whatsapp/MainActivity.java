package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
private Toolbar mToolbar;
private ViewPager  mviewPager;
private TabLayout mtablayout;
private TabAccessorAdapter mTabAccessorAdapter;
private FirebaseUser currentUser;
private FirebaseAuth mAuth;
private String currentUserId;
private DatabaseReference rootref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ChatterBox");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootref = FirebaseDatabase.getInstance().getReference();

        mviewPager = (ViewPager)findViewById(R.id.main_tabs_pager);
        mTabAccessorAdapter = new TabAccessorAdapter(getSupportFragmentManager());
        mviewPager.setAdapter(mTabAccessorAdapter);

        mtablayout = (TabLayout) findViewById(R.id.main_tabs);
        mtablayout.setupWithViewPager(mviewPager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser == null){
            SendUserToLoginActivity();

        }
        else
        {
            VerifyUserExistence();
        }
    }

    private void VerifyUserExistence() {
        currentUserId = mAuth.getCurrentUser().getUid();
        rootref.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!(dataSnapshot.child("name").exists())){
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToLoginActivity() {
        Intent loginintent = new Intent(MainActivity.this,LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();

    }

    private void SendUserToFindFriendsActivity() {
        Intent findfriendsIntent = new Intent(MainActivity.this,FindFriends.class);
        findfriendsIntent.putExtra("user",currentUserId);

        startActivity(findfriendsIntent);
        finish();

    }

    private void SendUserToSettingsActivity() {
        Intent settingsintent = new Intent(MainActivity.this,SettingsActivity.class);
        settingsintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsintent);
        finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_option){


            updateUserStatus("offline");
            mAuth.signOut();
            SendUserToLoginActivity();   mAuth.signOut();
            SendUserToLoginActivity();
        }

        if(item.getItemId() == R.id.main_settings_option){
            SendUserToSettingsActivity();
        }
        if(item.getItemId() == R.id.main_create_group_option){
            RequestNewGroup();
        }

        if(item.getItemId() == R.id.main_find_friends_option){
            SendUserToFindFriendsActivity();


        }

        return true;

    }

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g StudentsGroup");
        builder.setView(groupNameField);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();
                if(TextUtils.isEmpty(groupName)){
                     Toast.makeText(MainActivity.this,"Please write group name",Toast.LENGTH_SHORT).show();
                }
                else{
                    CreateNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 dialogInterface.cancel();
            }
        });

        builder.show().getWindow().setLayout(1000,500);



    }

    private void CreateNewGroup(final String groupName){
        rootref.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,groupName + " is created successfully",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        rootref.child("Users").child(currentUserId).child("userState")
                .updateChildren(onlineStateMap);

    }


}