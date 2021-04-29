package ph.edu.dlsu.codehub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class ReportPostActivity extends AppCompatActivity {
    // TODO: Convert to a fragment

    private Button reportBtn;
    private EditText reportReason;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_post);
        reportBtn = findViewById(R.id.report_this_post_btn);
        reportReason = findViewById(R.id.report_reason);
        reportBtn.setOnClickListener(view -> {
            String reason = reportReason.getText().toString();
        });
    }
}