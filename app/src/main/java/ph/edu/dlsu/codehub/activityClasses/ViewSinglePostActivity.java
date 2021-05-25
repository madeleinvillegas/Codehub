package ph.edu.dlsu.codehub.activityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.helperClasses.FirebaseNotificationsApi;
import ph.edu.dlsu.codehub.helperClasses.Notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
    private String userIdOfUser, title, body, uidOfThePostAuthor;
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
        reportBtn = findViewById(R.id.reportss_btn);

        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        userIdOfUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        setLikeBtnColor(uidOfPost);
        String[] uids = uidOfPost.split(" ");
        uidOfThePostAuthor = uids[0];

        userRef.child(uidOfThePostAuthor).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.d("Exist", String.valueOf(snapshot.exists()));
                String name = Objects.requireNonNull(snapshot.getValue(), "Name").toString();
                postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        title = Objects.requireNonNull(snapshot.child("title").getValue(), "Title").toString();
                        body = Objects.requireNonNull(snapshot.child("body").getValue(), "Body").toString();
                        postTitle.setText(title);
                        postBody.setText(body);
                        postDetails.setText(name + " | " +
                                Objects.requireNonNull(snapshot.child("date").getValue(), "MM DD, YY").toString() + " | " +
                                Objects.requireNonNull(snapshot.child("time").getValue(), "HH:mm").toString());
                    }

                    @Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
                });
            }

            @Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });

        if (uidOfThePostAuthor.equals(userIdOfUser)) {
            reportBtn.setVisibility(View.INVISIBLE);
            optionsBtn.setVisibility(View.VISIBLE);
        } else {
            optionsBtn.setVisibility(View.INVISIBLE);
            reportBtn.setVisibility(View.VISIBLE);
        }

        reportBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, ReportPostActivity.class);
            intent.putExtra("postId", uidOfPost);
            startActivity(intent);
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
                        if (snapshot.child(uidOfPost).hasChild(userIdOfUser)) {
                            likesRef.child(uidOfPost).child(userIdOfUser).removeValue();
                            isLiked = false;
                        }
                        // Post will be liked
                        else {
                            likesRef.child(uidOfPost).child(userIdOfUser).setValue(true).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //put code to display on notification on like here
                                    FirebaseNotificationsApi firebaseNotificationsApi = new FirebaseNotificationsApi(uidOfThePostAuthor, userIdOfUser, uidOfPost, "like");
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

    public void setLikeBtnColor(String pos) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.child(pos).hasChild(userIdOfUser)) {
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

            @Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
        });
    }
}