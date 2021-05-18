package ph.edu.dlsu.codehub.activityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.dlsu.codehub.R;

public class ViewOtherProfileActivity extends AppCompatActivity {
    private String position;
    private TextView name, address, work, followers, following;
    private Button follow;
    private CircleImageView profilePic;
    private ImageView bgPic;
    private DatabaseReference userRef, postRef, likesRef;
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

        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(position);
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
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
}