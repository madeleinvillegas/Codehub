package ph.edu.dlsu.codehub.fragmentClasses;

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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import ph.edu.dlsu.codehub.R;

public class CreatePostFragment extends Fragment {
    private EditText title, post;
    private StorageReference postsRef;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View views = inflater.inflate(R.layout.fragment_create_post,container,false);
        title = views.findViewById(R.id.input_title);
        post = views.findViewById(R.id.input_post);
        postsRef = FirebaseStorage.getInstance().getReference();
        Button create = views.findViewById(R.id.post_btn);
        create.setOnClickListener(view -> createPost());
        return views;
    }
    private void createPost() {
        String post_title, post_body;
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
        SimpleDateFormat currDate = new SimpleDateFormat("dd MMMM yyyy");
        String currentDate = currDate.format(calendar.getTime());
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm");
        String currentTime = currTime.format(calendar.getTime());

        StorageReference filePath;
    }
}
