package ph.edu.dlsu.codehub.activityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import ph.edu.dlsu.codehub.helperClasses.Comments;
import ph.edu.dlsu.codehub.helperClasses.FirebaseNotificationsApi;
import ph.edu.dlsu.codehub.helperClasses.Notifications;
import ph.edu.dlsu.codehub.R;

import static ph.edu.dlsu.codehub.R.layout.*;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView comments;
    private EditText commentInput;
    private String comment;
    private DatabaseReference userRef, postsRef, notificationRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_comment);
        ImageButton commentBtn = findViewById(R.id.post_comment_btn);
        comments = findViewById(R.id.comments_recycler_view);
        commentInput = findViewById(R.id.comment_input);

        comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        comments.setLayoutManager(linearLayoutManager);

        String pos = getIntent().getExtras().get("Position").toString();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(pos).child("Comments");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        commentBtn.setOnClickListener(view -> {
            userRef.child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    comment = commentInput.getText().toString();

                    if (TextUtils.isEmpty(comment)) {
                        Toast.makeText(getApplicationContext(), "Please add a comment", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Calendar calendar = Calendar.getInstance();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat currDate = new SimpleDateFormat("dd MMMM yyyy");
                        String currentDate = currDate.format(calendar.getTime());
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
                        String currentTime = currTime.format(calendar.getTime());
                        String timestamp = userId + " " + currentDate + " " + currentTime;
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    String userFullName = dataSnapshot.child(userId).child("fullName").getValue().toString();
                                    HashMap commentsMap = new HashMap();

                                    //just for clarity's sake: uid of the one who commented
                                    commentsMap.put("uid", userId);
                                    commentsMap.put("comment", comment);
                                    commentsMap.put("date", currentDate);
                                    commentsMap.put("time", currentTime);
                                    commentsMap.put("fullName", userFullName);

                                    postsRef.child(timestamp).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(getApplication(), "Successfully created comment", Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase.getInstance().getReference().child("Posts").child(pos).child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                        String authorUID = Objects.requireNonNull(snapshot.getValue()).toString();
                                                        FirebaseNotificationsApi firebaseNotificationsApi = new FirebaseNotificationsApi(authorUID, userId, pos, "comment");
                                                        firebaseNotificationsApi.addCommentNotification();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                    }
                                                });
                                                commentInput.setText("");
                                            }
                                            else {
                                                Toast.makeText(getApplication(), "Error occurred while updating your comment.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Comments> options =
                new FirebaseRecyclerOptions.Builder<Comments>().setQuery(postsRef, Comments.class).build();
        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter = new
                FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull CommentsViewHolder holder, int position, @NonNull @NotNull Comments model) {
                holder.theComment.setText(model.getComment());
                holder.theCommentor.setText(model.getFullName());
                holder.theDate.setText(model.getDate() + " | " + model.getTime());
            }

            @NonNull
            @NotNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getBaseContext()).inflate(comment_layout, parent, false);
                return new CommentsViewHolder(view);
            }
        };
        comments.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        private TextView theCommentor, theDate, theComment;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            theCommentor = itemView.findViewById(R.id.commentor_username);
            theDate= itemView.findViewById(R.id.commenting_date);
            theComment = itemView.findViewById(R.id.comment_text);
        }
    }
}