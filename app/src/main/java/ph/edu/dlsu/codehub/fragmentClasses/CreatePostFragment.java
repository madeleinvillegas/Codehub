package ph.edu.dlsu.codehub.fragmentClasses;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import ph.edu.dlsu.codehub.R;

public class CreatePostFragment extends Fragment {
    private EditText title, post;
    private DatabaseReference postsRef, userRef;
    private String currentUserID, post_title, post_body;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_create_post,container,false);
        title = views.findViewById(R.id.input_title);
        post = views.findViewById(R.id.input_post);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUserID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);

        Button create = views.findViewById(R.id.post_btn);
        create.setOnClickListener(view -> createPost());
        return views;
    }
    private void createPost() {
        post_title = title.getText().toString();
        post_body = post.getText().toString();
        if(TextUtils.isEmpty(post_title)) {
            Toast.makeText(getActivity(),"Please input the title of your post",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(post_body)) {
            Toast.makeText(getActivity(), "Please say something", Toast.LENGTH_SHORT).show();
        }
        else {
            savePostToFirebase();
        }
    }
    private void savePostToFirebase() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currDate = new SimpleDateFormat("dd MMMM yyyy");
        String currentDate = currDate.format(calendar.getTime());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
        String currentTime = currTime.format(calendar.getTime());
        String timestamp = " " + currentDate + " " + currentTime;
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String userFullName = dataSnapshot.child("fullName").getValue().toString();
//                    String userProfileImage = dataSnapshot.child("profilePicture").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", currentUserID);
                    postsMap.put("date", currentDate);
                    postsMap.put("time", currentTime);
                    postsMap.put("title", post_title);
                    postsMap.put("body", post_body);
//                    postsMap.put("profilePic", userProfileImage); // No need na based on figma, let me know if this is a keep
                    postsMap.put("fullName", userFullName);

                    postsRef.child(currentUserID + timestamp).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Successfully created post", Toast.LENGTH_SHORT).show();
                                title.setText("");
                                post.setText("");
                            }
                            else {
                                Toast.makeText(getActivity(), "Error occurred while updating your post.", Toast.LENGTH_SHORT).show();
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
