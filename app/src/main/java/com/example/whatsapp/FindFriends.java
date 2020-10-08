package com.example.whatsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriends extends AppCompatActivity {
    private Toolbar mToolBar;
    private RecyclerView FindFriendsRecyclerList;
    private DatabaseReference Usersref;
    private String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);


        Usersref = FirebaseDatabase.getInstance().getReference().child("Users");
        user = getIntent().getStringExtra("user");

        FindFriendsRecyclerList = (RecyclerView) findViewById(R.id.find_friends_recycler_list);
        FindFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mToolBar = (Toolbar) findViewById(R.id.find_friends_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                Intent intent = new Intent(FindFriends.this,MainActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                                                    new FirebaseRecyclerOptions.Builder<Contacts>()
                                                    .setQuery(Usersref,Contacts.class)
                                                    .build();


        FirebaseRecyclerAdapter<Contacts,FindFriendsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsViewHolder findFriendsViewHolder, final int position, @NonNull Contacts contacts) {
                if(!contacts.getUid().equals(user)) {
                    findFriendsViewHolder.userName.setText(contacts.getName());
                    findFriendsViewHolder.userStatus.setText(contacts.getStatus());
                    Picasso.get().load(contacts.getImage()).placeholder(R.drawable.profile_image).into(findFriendsViewHolder.ProfileImage);


                    findFriendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String visit_user_id = getRef(position).getKey();

                            Intent profileIntent = new Intent(FindFriends.this, ProfileActivity.class);
                            profileIntent.putExtra("visit_user_id", visit_user_id);
                            startActivity(profileIntent);
                        }
                    });




                }

                else{
                   findFriendsViewHolder.userName.setVisibility(View.GONE);
                   findFriendsViewHolder.userStatus.setVisibility(View.GONE);
                   findFriendsViewHolder.ProfileImage.setVisibility(View.GONE);


                }

            }

            @NonNull
            @Override
            public FindFriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
               View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
               FindFriendsViewHolder viewHolder = new FindFriendsViewHolder(view);
               return viewHolder;
            }
        };

        FindFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }


    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        CircleImageView ProfileImage;

        public FindFriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_profile_name);
            userStatus = itemView.findViewById(R.id.user_status);
            ProfileImage = itemView.findViewById(R.id.users_profile_image);

        }
    }



}