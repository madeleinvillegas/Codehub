package ph.edu.dlsu.codehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReportPostActivity extends AppCompatActivity {
    // TODO: Convert to a fragment

    //FORMAT FOR REPORTED POSTS:
    //REPORT_POST_DATABASE = {userWhoReported: ReasonForUserReporting}

    private Button reportBtn;
    private EditText reportReason;

    private DatabaseReference reportDataBaseReference; //database reference
    private String userId;
    private String postId;

    private RelativeLayout rootLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);

        //Firebase Stuff
        userId = FirebaseAuth.getInstance().getUid();
        postId = getIntent().getStringExtra("postId");
        reportDataBaseReference = FirebaseDatabase.getInstance().getReference().child("Reported_Posts");

        //Initialize Ids
        reportBtn = findViewById(R.id.report_this_post_btn);
        reportReason = findViewById(R.id.report_reason);
        rootLayout = findViewById(R.id.root_layout);

        //Initialize Progress Bar
        progressBar = new ProgressBar(ReportPostActivity.this, null, android.R.attr.progressBarStyleLarge);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        //addProgressBar
        rootLayout.addView(progressBar, params);

        //Initially hide the progress bar
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        reportBtn.setOnClickListener(view -> {
            report_post();
        });

    }

    private void report_post()
    {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


        DatabaseReference postReported = reportDataBaseReference.child(postId);
        String reason = reportReason.getText().toString();
        HashMap data = new HashMap<String, String>();

        postReported.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // This will not work properly
                for (DataSnapshot child: snapshot.getChildren())
                {
                    data.put(child.getKey(), child.getValue());
                }
                
                if (data.containsKey(userId))
                {
                    //if user already reported
                    progressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "User Already Reported", Toast.LENGTH_LONG).show();
                }
                // Until here
                else
                {
                    data.put("reporter", userId);
                    data.put("reason", reason);
                    data.put("postId", postId);

                    progressBar.setVisibility(View.VISIBLE);
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


                    postReported.updateChildren(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(), "Report Successful", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Report Failed. Try Again.", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Pulling of  report database failed", Toast.LENGTH_LONG).show();

            }
        });
    }
}