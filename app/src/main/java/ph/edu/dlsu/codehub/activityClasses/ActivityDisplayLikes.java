package ph.edu.dlsu.codehub.activityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.dlsu.codehub.R;



public class ActivityDisplayLikes extends BaseToolbarActivity{

    private RecyclerView peopleWhoLiked;
    public String postId;
    private DatabaseReference LikesRef;
    private String userId;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers_list);
        setToolBar();


        title = findViewById(R.id.followersHeadingText);
        title.setText("People Who Liked");
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).toString();

        postId = getIntent().getStringExtra("postId");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        peopleWhoLiked = (RecyclerView) findViewById(R.id.recyclerView);
        peopleWhoLiked.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        peopleWhoLiked.setLayoutManager(linearLayoutManager);

        displayLike();
    }

    public static class Likes {
        public boolean isLiked;

        public Likes() {
        }

        public boolean isLiked() {
            return isLiked;
        }

        public void setLiked(boolean liked) {
            isLiked = liked;
        }
    }


    private void displayLike() {
        Query listOfLikes = LikesRef.child(postId);

        //should be FirebaseRecyclerOptions<Nothing>
        //the model methods will not work
        FirebaseRecyclerOptions<Likes> options =
                new FirebaseRecyclerOptions.Builder<Likes>()
                        .setQuery(listOfLikes, Likes.class)
                        .build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Likes, ActivityDisplayFollows.FollowViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ActivityDisplayFollows.FollowViewHolder holder, int position, @NonNull @NotNull Likes model) {
                String holderUid = getRef(position).getKey();
                DatabaseReference usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

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
                                intent = new Intent(ActivityDisplayLikes.this, ViewProfileActivity.class);
                            } else {
                                intent = new Intent(ActivityDisplayLikes.this, ViewOtherProfileActivity.class);
                                intent.putExtra("Position", holderUid);
                            }
                            startActivity(intent);
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });

            }

            @Override
            public ActivityDisplayFollows.FollowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.users_display_layout, parent, false);

                return new ActivityDisplayFollows.FollowViewHolder(view);
            }

        };

        peopleWhoLiked.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
