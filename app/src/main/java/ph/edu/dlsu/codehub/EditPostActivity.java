package ph.edu.dlsu.codehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ph.edu.dlsu.codehub.fragmentClasses.ProfileTemplate;

public class EditPostActivity extends AppCompatActivity {

    // TODO: Convert to a fragment
    private EditText updatedTitle, updatedBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        Button editBtn = findViewById(R.id.edit_the_post_btn);
        updatedTitle = findViewById(R.id.edit_title);
        updatedBody = findViewById(R.id.edit_post_body);
        editBtn.setOnClickListener(view -> {
            ViewAPostAndDelete.postRef.child("title").setValue(updatedTitle.getText().toString());
            ViewAPostAndDelete.postRef.child("body").setValue(updatedBody.getText().toString());
            Toast.makeText(this, "Successfully edited the post", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditPostActivity.this, ProfileTemplate.class);
            startActivity(intent);
            finish();
        });

    }
}