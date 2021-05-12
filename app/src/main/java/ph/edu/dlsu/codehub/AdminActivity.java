package ph.edu.dlsu.codehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ph.edu.dlsu.codehub.fragmentClasses.HomeFragment;

import static ph.edu.dlsu.codehub.R.layout.comment_layout;

public class AdminActivity extends AppCompatActivity {
    private RecyclerView reportedPosts;
    private DatabaseReference userRef, postsRef, reportsRef;
    private String userIdOfPoster, userIdOfReporter;

    String TAG = "DEBUGGING MESSAGE ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        reportsRef = FirebaseDatabase.getInstance().getReference().child("Reported_Posts");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        reportedPosts = findViewById(R.id.adminRecyclerView);
        reportedPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        reportedPosts.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayReportedPosts();

    }

    public void displayReportedPosts() {
        FirebaseRecyclerOptions<Report> options =
                new FirebaseRecyclerOptions.Builder<Report>().setQuery(reportsRef, Report.class).build();
        FirebaseRecyclerAdapter<Report, AdminActivity.ReportViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Report, ReportViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ReportViewHolder holder, int position, @NonNull @NotNull Report model) {
                String pos = getRef(position).getKey();
                holder.reporterReason.setText(model.getReason());

                postsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        holder.postTitle.setText(snapshot.child(pos).child("title").getValue().toString());
                        holder.postBody.setText(snapshot.child(pos).child("body").getValue().toString());
                        String deets =  snapshot.child(pos).child("fullName").getValue().toString() + " | " +
                                snapshot.child(pos).child("date").getValue().toString() + " | " +
                                snapshot.child(pos).child("time").getValue().toString();
                        Log.d(TAG, "Deets: " + deets);
//                        holder.posterDetails.setText(deets);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        holder.reporterDetails.setText("Reported by: " + snapshot.child(model.getReporter()).child("fullName").getValue().toString());
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                holder.reporterDetails.setText(model.getReporter());


                holder.optionsBtn.setOnClickListener(view -> {
                    Log.d(TAG, "Options btn was clicked" );
                });
            }

            @NonNull
            @NotNull
            @Override
            public ReportViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reported_post_layout, parent, false);
                return new ReportViewHolder(view);
            }
        };

        reportedPosts.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }


    public class ReportViewHolder extends RecyclerView.ViewHolder {
        private TextView postTitle, postBody, posterDetails, reporterDetails, reporterReason;
        private ImageButton optionsBtn;
        public ReportViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.reported_post_title);
            postBody = itemView.findViewById(R.id.reported_post_body);
            posterDetails = itemView.findViewById(R.id.reported_post_details);
            reporterDetails = itemView.findViewById(R.id.reporter);
            reporterReason = itemView.findViewById(R.id.reason_for_reporting);
            optionsBtn = itemView.findViewById(R.id.report_options_btn);
        }
    }
}