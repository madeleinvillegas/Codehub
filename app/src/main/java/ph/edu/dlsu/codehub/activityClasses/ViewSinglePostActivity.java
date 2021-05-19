package ph.edu.dlsu.codehub.activityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.helperClasses.Notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class ViewSinglePostActivity extends AppCompatActivity {
    private String uidOfPost;
    private DatabaseReference postRef, likesRef, userRef;
    private ImageButton likeBtn, commentBtn, optionsBtn, reportBtn;
    private TextView noOfLikes, postDetails, postBody, postTitle;
    private int numberOfLikes;
    private String userId, title, body;
    private Boolean isLiked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_post);
        uidOfPost = getIntent().getExtras().get("Position").toString();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(uidOfPost);
        postDetails = findViewById(R.id.singles_post_details);
        postBody = findViewById(R.id.singles_post_body);
        postTitle = findViewById(R.id.singles_post_title);
        likeBtn = findViewById(R.id.like_btns);
        commentBtn = findViewById(R.id.comment_btns);
        optionsBtn = findViewById(R.id.optionss_btn);
        noOfLikes = findViewById(R.id.number_of_likess);

        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        setLikeBtnColor(uidOfPost);
        final String[] deets = new String[1];
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                // TODO: I don't think this works
                deets[0] = snapshot.child("fullName").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                title = snapshot.child("title").getValue().toString();
                body = snapshot.child("body").getValue().toString();
                postTitle.setText(title);
                postBody.setText(body);
                postDetails.setText(deets[0] + " | " +
                        snapshot.child("date").getValue().toString() + " | " +
                        snapshot.child("time").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        commentBtn.setOnClickListener(view -> {
            Intent intent = new Intent(ViewSinglePostActivity.this, CommentActivity.class);
            intent.putExtra("Position", uidOfPost);
            startActivity(intent);
        });

        optionsBtn.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, optionsBtn);
            popupMenu.inflate(R.menu.triple_dots_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu1:
                            Intent intent = new Intent(ViewSinglePostActivity.this, EditPostActivity.class);
                            intent.putExtra("pos", uidOfPost);
                            intent.putExtra("title", title);
                            intent.putExtra("body", body);
                            startActivity(intent);
                            return true;
                        case R.id.menu2:
                            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(uidOfPost);
                            postRef.removeValue();
                            Toast.makeText(ViewSinglePostActivity.this, "Successfully deleted the post", Toast.LENGTH_SHORT).show();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        });


        likeBtn.setOnClickListener(view -> {
            isLiked = true;
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    // User already liked the post so the post will be unliked
                    if(isLiked) {
                        if (snapshot.child(uidOfPost).hasChild(userId)) {
                            likesRef.child(uidOfPost).child(userId).removeValue();
                            isLiked = false;
                        }
                        // Post will be liked
                        else {
                            likesRef.child(uidOfPost).child(userId).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //put code to display on notification on like here
                                    putLikeNotification(uidOfPost);
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
    public void putLikeNotification(String postID)
    {
        DatabaseReference notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        //Format: "AUTHOR | PERSON WHO LIKED"
        String NotificationID = userId + " | " + userId;

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

        usersRef.child(userId).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String notificationContent = snapshot.getValue().toString() + " liked your post";
                notification.setNotificationContent(notificationContent);
                notificationRef.child(userId).child(NotificationID).setValue(notification);

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }
}