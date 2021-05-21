package ph.edu.dlsu.codehub.activityClasses;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.helperClasses.FirebaseNotificationsApi;

public class ReportPostActivity extends AppCompatActivity {

    private Button reportBtn;
    private EditText reportReason;

    private DatabaseReference reportDataBaseReference; //database reference
    private String userIdOfTheReporter, uidOfThePostAuthor;
    private String postId;

    private RelativeLayout rootLayout;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);

        //Firebase Stuff
        userIdOfTheReporter = FirebaseAuth.getInstance().getUid();
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

    private void report_post() {
        DatabaseReference postReported = reportDataBaseReference.child(postId);
        String reason = reportReason.getText().toString();
        if (TextUtils.isEmpty(reason)) {
            Toast.makeText(this, "Please input the reason for reporting", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            HashMap data = new HashMap<String, String>();

            postReported.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild("reporter")) {
                        String pos = snapshot.child("reporter").getValue().toString();
                        String tmp = snapshot.getKey();
                        String[] tmps = tmp.split(" ");
                        uidOfThePostAuthor = tmps[0];
                        Log.d("DEBUGGING", uidOfThePostAuthor);
                        if (pos.equals(userIdOfTheReporter)) {
                            //if user already reported
                            Toast.makeText(getApplicationContext(), "Post Already Reported", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                        else {
                            saveToFirebase();
                        }
                    }
                    else {
                        saveToFirebase();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Pulling of  report database failed", Toast.LENGTH_LONG).show();

                }
                public void saveToFirebase() {
                    data.put("reporter", userIdOfTheReporter);
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
                                FirebaseNotificationsApi firebaseNotificationsApi = new
                                        FirebaseNotificationsApi(uidOfThePostAuthor, userIdOfTheReporter, postId, "report");
                                firebaseNotificationsApi.addReportNotification();
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
            });
        }

    }
}