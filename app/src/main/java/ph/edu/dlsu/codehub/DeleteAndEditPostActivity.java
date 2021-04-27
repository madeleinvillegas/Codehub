package ph.edu.dlsu.codehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import ph.edu.dlsu.codehub.fragmentClasses.ProfileTemplate;

public class DeleteAndEditPostActivity extends AppCompatActivity {
    private TextView editTitle, editBody;
    private Button editBtn, deleteBtn;
    private String pos, currentUserID, dbUserID, body, title;
    private DatabaseReference postRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_and_edit_post);
        editTitle = findViewById(R.id.edit_title);
        editBody = findViewById(R.id.edit_post);
        editBtn = findViewById(R.id.edit_post_btn);
        deleteBtn = findViewById(R.id.delete_post_btn);
        editBtn.setVisibility(View.INVISIBLE);
        deleteBtn.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        pos = getIntent().getExtras().get("Position").toString();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(pos);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    body = Objects.requireNonNull(snapshot.child("body").getValue()).toString();
                    title = Objects.requireNonNull(snapshot.child("title").getValue()).toString();
                    dbUserID = Objects.requireNonNull(snapshot.child("uid").getValue()).toString();
                    editTitle.setText(title);
                    editBody.setText(body);
                    // Shows the delete and edit buttons only if the user authored the post
                    if(dbUserID.equals(currentUserID)) {
                        editBtn.setVisibility(View.VISIBLE);
                        deleteBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        deleteBtn.setOnClickListener(view -> {
            postRef.removeValue();
            Toast.makeText(this, "Successfully deleted the post", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteAndEditPostActivity.this, ProfileTemplate.class);
            startActivity(intent);
            finish();
        });
        editBtn.setOnClickListener(view -> {

        });
    }
}