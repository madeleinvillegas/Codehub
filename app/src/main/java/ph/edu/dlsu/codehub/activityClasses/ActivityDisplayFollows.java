package ph.edu.dlsu.codehub.activityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.activityClasses.EditPostActivity;
import ph.edu.dlsu.codehub.helperClasses.User;



//TODO: display functions could be simplified

//if showWhat == followers, show list of people that the profile is following
//else if showWhat == followedBy , show list of people being followed by the user

public class ActivityDisplayFollows extends BaseToolbarActivity{

    private RecyclerView followerListRecyclerView;
    public String showWhat;
    private DatabaseReference usersDatabaseReference;
    private String userId;
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_list);

        setToolBar();

        showWhat = getIntent().getExtras().get("mode").toString();
        Log.d("showWhat", showWhat);
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        title = findViewById(R.id.followersHeadingText);


        followerListRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        followerListRecyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        followerListRecyclerView.setLayoutManager(linearLayoutManager);



        if ( showWhat.equals("followers") )
        {
            displayFollows("FollowqwedBy");
            title.setText("Followers: ");
        } else if (showWhat.equals("following"))
        {
            displayFollows("FollowTo");
            title.setText("Following: ");
        }

    }

    public static class follow
    {
        public follow()
        {

        }
        public String follow;

        public String getFollow() {
            return follow;
        }

        public void setFollow(String follow) {
            this.follow = follow;
        }
    }
    
    public static class FollowViewHolder extends RecyclerView.ViewHolder{
        View mView;
        CircleImageView profilePic;
        TextView name;

        public FollowViewHolder(View itemView)
        {
            super(itemView);

            mView = itemView;
            name = (TextView) mView.findViewById(R.id.user_display_profile_name);
            profilePic = (CircleImageView) mView.findViewById(R.id.user_display_profile_image);
            this.mView = itemView;
        }

        public void setProfilePicture(String profilePicture) {
            if(profilePicture != null)
            {
                Log.d("DEBUGGING", profilePicture + "");
                Picasso.get()
                        .load(profilePicture)
                        .placeholder(R.drawable.ic_baseline_event_seat_24)
                        .into(profilePic);
            }
        }

        public void setName(String fullName) {
            name.setText(fullName);
        }

    }



    private void displayFollows(String s) {
        Query listOfFollowers = FirebaseDatabase.getInstance().getReference().child(s).child(userId);

        //should be FirebaseRecyclerOptions<Nothing>
        //the model methods will not work
        FirebaseRecyclerOptions<follow> options =
                new FirebaseRecyclerOptions.Builder<follow>()
                        .setQuery(listOfFollowers, follow.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<follow, ActivityDisplayFollows.FollowViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ActivityDisplayFollows.FollowViewHolder holder, int position, @NonNull @NotNull follow model) {
                String holderUid = getRef(position).getKey();
                    Log.d("Holder", holderUid + "|| " + userId);
                    usersDatabaseReference.child(holderUid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(snapshot.exists())
                            {

                                String fullName = Objects.toString(snapshot.child("fullName").getValue(), "nullValue") ;
                                String profileImg =  Objects.toString(snapshot.child("profileImageLink").getValue(), "def");
                                holder.setName(fullName);
                                holder.setProfilePicture(profileImg);
                            }

                            holder.itemView.setOnClickListener(view -> {
                                Intent intent;
                                if (holderUid.equals(userId)) {
                                    intent = new Intent(ActivityDisplayFollows.this, ViewProfileActivity.class);
                                } else {
                                    intent = new Intent(ActivityDisplayFollows.this, ViewOtherProfileActivity.class);
                                    intent.putExtra("Position", holderUid);
                                }
                                startActivity(intent);

                            });

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        }
                    });




//                holder.itemView.setOnClickListener(view -> {
//                    String id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//                    if (id.equals(userId)) {
//                        Intent intent = new Intent(ActivityDisplayFollows.this, ViewProfileActivity.class);
//                        startActivity(intent);
//                    } else {
//                        Intent intent = new Intent(ActivityDisplayFollows.this, ViewOtherProfileActivity.class);
//                        intent.putExtra("Position", holderUid);
//                        startActivity(intent);
//                    }
//
//                });


            }

            @Override
            public ActivityDisplayFollows.FollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_display_layout, parent, false);

                return new ActivityDisplayFollows.FollowViewHolder(view);
            }

        };
        followerListRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
    }



}
