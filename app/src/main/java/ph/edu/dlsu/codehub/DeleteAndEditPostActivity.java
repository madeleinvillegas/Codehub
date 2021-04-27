package ph.edu.dlsu.codehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteAndEditPostActivity extends AppCompatActivity {
    private TextView editTitle, editBody;
    private Button editBtn, deleteBtn;
    private String pos;
    private DatabaseReference postRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_and_edit_post);
        editTitle = findViewById(R.id.edit_title);
        editBody = findViewById(R.id.edit_post);
        editBtn = findViewById(R.id.edit_post_btn);
        deleteBtn = findViewById(R.id.delete_post_btn);
        pos = getIntent().getExtras().get("Position").toString();
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(pos);
        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String body = snapshot.child("body").getValue().toString();
                String title = snapshot.child("title").getValue().toString();
                editTitle.setText(title);
                editBody.setText(body);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}