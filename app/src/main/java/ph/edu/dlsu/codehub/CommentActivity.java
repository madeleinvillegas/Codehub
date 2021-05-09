package ph.edu.dlsu.codehub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

public class CommentActivity extends AppCompatActivity {
    // TODO: Convert to fragment
    private ImageButton commentBtn;
    private RecyclerView comments;
    private EditText commentInput;
    private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentBtn = findViewById(R.id.post_comment_btn);
        comments = findViewById(R.id.comments_recycler_view);
        commentInput = findViewById(R.id.comment_input);
        comment = commentInput.getText().toString();
    }
}