package ph.edu.dlsu.codehub.activityClasses;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.dlsu.codehub.helperClasses.FirebaseNotificationsApi;
import ph.edu.dlsu.codehub.helperClasses.Notifications;
import ph.edu.dlsu.codehub.helperClasses.Post;
import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.fragmentClasses.HomeFragment;


public class ViewProfileActivity extends BaseToolbarActivity {
    private DatabaseReference postRef, likesRef, userRef;
    private RecyclerView postList;
    private Boolean isLiked = false;
    private String userId;
    private TextView name, address, work, followers, following;
    private ImageButton edit;
    private ImageView bgpic;
    private CircleImageView profilepic;
    private String uidOfThePostAuthor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        setToolBar();
        name = findViewById(R.id.textView);
        address = findViewById(R.id.textView2);
        work = findViewById(R.id.textView3);
        followers = findViewById(R.id.textView4);
        following = findViewById(R.id.textView5);
        edit = findViewById(R.id.editProfileBtn);
        profilepic = findViewById(R.id.profilePic);
        bgpic = findViewById(R.id.bgPhoto);
        edit.setOnClickListener(view-> {
            Intent intent = new Intent(this, EditProfileDataActivity.class);
            intent.putExtra("prior", "viewProfile");
            startActivity(intent);
            finish();
        });

        postList = findViewById(R.id.recyclerViewProfile);
        postList.setHasFixedSize(true);
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        //get the list of people that you are following
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityDisplayFollows.class);
                intent.putExtra("mode", "following");
                startActivity(intent);
            }
        });

        //get a list of followers
        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ActivityDisplayFollows.class);
                intent.putExtra("mode", "followers");
                startActivity(intent);
            }
        });


        userRef.child("profileImageLink").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.getValue()!=null)
                {
                    Picasso.get()
                            .load(Objects.toString(snapshot.getValue(), "nullValue"))
                            .placeholder(R.drawable.profile_image)
                            .into(profilepic);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        userRef.child("backgroundImageLink").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                Picasso.get()
                        .load(Objects.toString(snapshot.getValue(), "nullValue"))
                        .placeholder(R.drawable.background_image)
                        .into(bgpic);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                name.setText(Objects.toString(snapshot.child("fullName").getValue(), "nullValue"));
                String forDisplay = Objects.toString(snapshot.child("address").getValue(), "nullValue");
                if (forDisplay.equals("")) {
                    forDisplay = "Address";
                }
                address.setText(forDisplay);
                forDisplay = Objects.toString(snapshot.child("occupation").getValue(), "nullValue");
                if (forDisplay.equals("")) {
                    forDisplay = "Workplace";
                }
                work.setText(forDisplay);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Query query = postRef.orderByChild("uid").equalTo(userId);
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        FirebaseRecyclerAdapter<Post, ViewProfileViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Post, ViewProfileViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ViewProfileViewHolder holder, int position, @NonNull @NotNull Post model) {
                String pos = getRef(position).getKey();
                String[] arr = pos.split(" ");
                uidOfThePostAuthor = arr[0];

                userRef.child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        holder.postDetails.setText(Objects.toString(snapshot.getValue(), "nullValue") + " | " + model.getDate() + " | " + model.getTime());
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                holder.postBody.setText(model.getBody());
                holder.postTitle.setText(model.getTitle());
                holder.setLikeBtnColor(pos);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent;
                        intent = new Intent(ViewProfileActivity.this, ViewSinglePostActivity.class);
                        intent.putExtra("Position", pos);
                        startActivity(intent);
                    }
                });

                holder.reportBtn.setVisibility(View.INVISIBLE);
                holder.commentBtn.setOnClickListener(view -> {
                    Intent intent = new Intent(ViewProfileActivity.this, CommentActivity.class);
                    intent.putExtra("Position", pos);
                    startActivity(intent);
                });

                holder.optionsBtn.setOnClickListener(view -> {
                    HomeFragment.PostViewHolder.showMenu(view, pos, model.getTitle(), model.getBody());
                });

                holder.noOfLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), ActivityDisplayLikes.class);
                        intent.putExtra("postId", pos);
                        startActivity(intent);
                    }
                });

                holder.likeBtn.setOnClickListener(view -> {
                    isLiked = true;
                    likesRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            // User already liked the post so the post will be unliked
                            if(isLiked) {
                                if (snapshot.child(pos).hasChild(userId)) {
                                    likesRef.child(pos).child(userId).removeValue();
                                    isLiked = false;
                                }
                                // Post will be liked
                                else {
                                    HomeFragment.Likes likes = new HomeFragment.Likes();
                                    likes.setLiked(true);
                                    likesRef.child(pos).child(userId).setValue(likes).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            //put code to display on notification on like here
                                            FirebaseNotificationsApi firebaseNotificationsApi = new FirebaseNotificationsApi(uidOfThePostAuthor, userId, pos, "like");
                                            firebaseNotificationsApi.addLikeNotification();
                                        }
                                    });
                                    // Stops the infinite loop
                                    isLiked = false;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                });
            }

            @NonNull
            @NotNull
            @Override
            public ViewProfileViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(ViewProfileActivity.this).inflate(R.layout.post_layout, parent, false);
                return new ViewProfileViewHolder(view);
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class ViewProfileViewHolder extends RecyclerView.ViewHolder {
        private ImageButton likeBtn, commentBtn, optionsBtn, reportBtn;
        private TextView noOfLikes, postDetails, postBody, postTitle;
        private int numberOfLikes;
        private String userId;
        private DatabaseReference likesRef;

        public ViewProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            postDetails = itemView.findViewById(R.id.single_post_details);
            postBody = itemView.findViewById(R.id.single_post_body);
            postTitle = itemView.findViewById(R.id.single_post_title);
            likeBtn = itemView.findViewById(R.id.like_btn);
            commentBtn = itemView.findViewById(R.id.comment_btn);
            reportBtn = itemView.findViewById(R.id.report_btn);
            noOfLikes = itemView.findViewById(R.id.number_of_likes);
            likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            optionsBtn = itemView.findViewById(R.id.options_btn);

        }


        public static void showMenu(@NonNull View itemView, String pos, String title, String body) {
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
            popupMenu.inflate(R.menu.triple_dots_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu1:
                            Intent intent = new Intent(itemView.getContext(), EditPostActivity.class);
                            intent.putExtra("pos", pos);
                            intent.putExtra("title", title);
                            intent.putExtra("body", body);
                            itemView.getContext().startActivity(intent);
                            return true;
                        case R.id.menu2:
                            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(pos);
                            postRef.removeValue();
                            Toast.makeText(itemView.getContext(), "Successfully deleted the post", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        }
        public void setLikeBtnColor(String pos) {
            likesRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                    if(snapshot.child(pos).hasChild(userId)) {
                        likeBtn.setImageResource(R.drawable.like);
                        numberOfLikes = (int) snapshot.child(pos).getChildrenCount();
                        noOfLikes.setText(numberOfLikes + " likes");
                    }
                    else {
                        likeBtn.setImageResource(R.drawable.unlike);
                        numberOfLikes = (int) snapshot.child(pos).getChildrenCount();
                        noOfLikes.setText(numberOfLikes + " likes");
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
    }
}