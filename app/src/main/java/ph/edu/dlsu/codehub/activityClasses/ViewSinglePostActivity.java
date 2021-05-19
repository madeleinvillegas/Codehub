package ph.edu.dlsu.codehub.activityClasses;

import androidx.appcompat.app.AppCompatActivity;
import ph.edu.dlsu.codehub.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ViewSinglePostActivity extends AppCompatActivity {
    private String uidOfPost;
    private DatabaseReference postRef, likesRef;
    private ImageButton likeBtn, commentBtn, optionsBtn, reportBtn;
    private TextView noOfLikes, postDetails, postBody, postTitle;
    private int numberOfLikes;
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_post);
        uidOfPost = getIntent().getExtras().get("pos").toString();
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


        optionsBtn.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(this, optionsBtn);
            popupMenu.inflate(R.menu.triple_dots_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.menu1:
                            Intent intent = new Intent(ViewSinglePostActivity.this, EditPostActivity.class);
//                            intent.putExtra("pos", pos);
//                            intent.putExtra("title", title);
//                            intent.putExtra("body", body);
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

    }
}