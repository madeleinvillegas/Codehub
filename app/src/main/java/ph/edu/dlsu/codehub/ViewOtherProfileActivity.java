package ph.edu.dlsu.codehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import ph.edu.dlsu.codehub.fragmentClasses.HomeFragment;

public class ViewOtherProfileActivity extends AppCompatActivity {
    private String position, uidOfThePostAuthor, userId;
    private TextView name, address, work, followers, following;
    private Button follow;
    private CircleImageView profilePic;
    private ImageView bgPic;
    private DatabaseReference userRef, postRef, likesRef;
    private RecyclerView postList;
    private Boolean isLiked;
    String TAG = "DEBUGGING: ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_other);
        position = getIntent().getExtras().get("Position").toString();
        name = findViewById(R.id.other_fullName);
        address = findViewById(R.id.other_address);
        work = findViewById(R.id.other_work);
        followers = findViewById(R.id.other_followers);
        following = findViewById(R.id.other_following);
        follow = findViewById(R.id.followBtn);
        profilePic = findViewById(R.id.other_profilePic);
        bgPic = findViewById(R.id.other_bgPhoto);
        postList = findViewById(R.id.recyclerViewOtherProfile);
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(position);
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        displayUserInfo();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query = postRef.orderByChild("uid").equalTo(position);
        FirebaseRecyclerOptions<Post> options =
                new FirebaseRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        FirebaseRecyclerAdapter<Post, ViewOtherProfileActivity.ViewProfileViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Post, ViewOtherProfileActivity.ViewProfileViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull @NotNull ViewOtherProfileActivity.ViewProfileViewHolder holder, int position, @NonNull @NotNull Post model) {
                        String pos = getRef(position).getKey();
                        String[] arr = pos.split(" ");
                        uidOfThePostAuthor = arr[0];

                        holder.postDetails.setText(model.getFullName() + " | " + model.getDate() + " | " + model.getTime());
                        holder.postBody.setText(model.getBody());
                        holder.postTitle.setText(model.getTitle());
                        holder.setLikeBtnColor(pos);

                        holder.reportBtn.setVisibility(View.INVISIBLE);
                        holder.commentBtn.setOnClickListener(view -> {
                            Intent intent = new Intent(ViewOtherProfileActivity.this, CommentActivity.class);
                            intent.putExtra("Position", pos);
                            startActivity(intent);
                        });

                        holder.optionsBtn.setOnClickListener(view -> {
                            HomeFragment.PostViewHolder.showMenu(view, pos, model.getTitle(), model.getBody());
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
                                            likesRef.child(pos).child(userId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    //put code to display on notification on like here
                                                    putLikeNotification(pos);
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
                    public ViewOtherProfileActivity.ViewProfileViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(ViewOtherProfileActivity.this).inflate(R.layout.post_layout, parent, false);
                        return new ViewOtherProfileActivity.ViewProfileViewHolder(view);
                    }
                };
        postList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void displayUserInfo() {userRef.child("profileImageLink").addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

            Picasso.get()
                    .load(snapshot.getValue().toString())
                    .placeholder(R.drawable.boy_avatar)
                    .into(profilePic);
        }

        @Override
        public void onCancelled(@NonNull @NotNull DatabaseError error) {

        }
    });
        userRef.child("backgroundImageLink").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                Picasso.get()
                        .load(snapshot.getValue().toString())
                        .placeholder(R.drawable.background_image)
                        .into(bgPic);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                name.setText(snapshot.child("fullName").getValue().toString());
                String forDisplay = snapshot.child("address").getValue().toString();
                if (forDisplay.equals("")) {
                    forDisplay = "Address";
                }
                address.setText(forDisplay);
                forDisplay = snapshot.child("occupation").getValue().toString();
                if (forDisplay.equals("")) {
                    forDisplay = "Workplace";
                }
                work.setText(forDisplay);

//                followers.setText(snapshot.child("fullName").toString());
//                following.setText(snapshot.child("fullName").toString());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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
    public void putLikeNotification(String postID)
    {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        //Format: "AUTHOR | PERSON WHO LIKED"
        String NotificationID = uidOfThePostAuthor + " | " + userId;

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currDate = new SimpleDateFormat("dd MMMM yyyy");
        String currentTime = currTime.format(calendar.getTime());
        String currentDate = currDate.format(calendar.getTime());


        Notifications notification = new Notifications();
        notification.setCreationDate(currentDate);
        notification.setProfileImageLink(FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("profileImageLink").toString());
        notification.setLinkUID(postID);
        notification.setNotificationType(0);
        notification.setTime(currentTime);

        //some complicated stuff

        usersRef.child(uidOfThePostAuthor).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String notificationContent = snapshot.getValue().toString() + " liked your post";
                notification.setNotificationContent(notificationContent);
                notificationRef.child(uidOfThePostAuthor).child(NotificationID).setValue(notification);

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }
}