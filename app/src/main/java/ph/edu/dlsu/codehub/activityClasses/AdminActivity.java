package ph.edu.dlsu.codehub.activityClasses;

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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.helperClasses.FirebaseNotificationsApi;
import ph.edu.dlsu.codehub.helperClasses.Report;

public class AdminActivity extends AppCompatActivity {
    private RecyclerView reportedPosts;
    private DatabaseReference userRef, postsRef, reportsRef;
    public static String uidOfThePostAuthor, uidOfTheReporter;

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
                new FirebaseRecyclerOptions.Builder<Report>().setQuery(reportsRef, Report.class)
                        .build();


        FirebaseRecyclerAdapter<Report, ReportViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Report, ReportViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ReportViewHolder holder, int position, @NonNull @NotNull Report model) {
                String pos = model.getPostId();
                holder.setReporterReason(model.getReason());


                postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        //Something wrong with the code below
                        holder.setPostTitle(snapshot.child(pos).child("title").getValue().toString());
                        holder.setPostBody(snapshot.child(pos).child("body").getValue().toString());

                        //NOTE: deets = details
                        String deets =  snapshot.child(pos).child("fullName").getValue().toString() + " | " +
                                snapshot.child(pos).child("date").getValue().toString() + " | " +
                                snapshot.child(pos).child("time").getValue().toString();

                        String title = snapshot.child(pos).child("title").getValue().toString();
                        uidOfThePostAuthor = snapshot.child(pos).child("uid").getValue().toString();


                        holder.posterDetails.setText(deets);
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });


                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        holder.setReporterDetails("Reported by: " +
                                snapshot.child(model.getReporter()).child("fullName").getValue().toString());
                        uidOfTheReporter = snapshot.child(model.getReporter()).getKey();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });

                holder.optionsBtn.setOnClickListener(view -> {
                    ReportViewHolder.showMenu(view, pos);
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

    public void logout(View view) {
        Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        Log.d("Entered login","function is logout");
    }


    public static class ReportViewHolder extends RecyclerView.ViewHolder {
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

        public void setPostTitle(String postTitle) {
            this.postTitle.setText(postTitle);
        }

        public void setPostBody(String postBody) {
            this.postBody.setText(postBody);
        }

        public void setPosterDetails(String posterDetails) {
            this.posterDetails.setText(posterDetails);
        }

        public void setReporterDetails(String reporterDetails) {
            this.reporterDetails.setText(reporterDetails);
        }

        public void setReporterReason(String reporterReason) {
            this.reporterReason.setText(reporterReason);
        }

        public static void showMenu(@NonNull View itemView, String pos) {
            Log.d("POST AUTHOR", uidOfThePostAuthor);
            Log.d("REPORTER", uidOfTheReporter);
            Log.d("POS", pos);
            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
            popupMenu.inflate(R.menu.admin_options_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    FirebaseNotificationsApi firebaseNotificationsApi;
                    switch (item.getItemId()) {
                        case R.id.delete:
//                            DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference().child("Reported_Posts").child(pos);
//                            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(pos);
//                            reportsRef.removeValue();
//                            postRef.removeValue();
                            firebaseNotificationsApi = new FirebaseNotificationsApi(uidOfThePostAuthor, uidOfTheReporter, "", "delete");
                            firebaseNotificationsApi.addDeleteNotification();

                            Toast.makeText(itemView.getContext(), "Successfully deleted the post", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.keep:
//                            DatabaseReference reportRef = FirebaseDatabase.getInstance().getReference().child("Reported_Posts").child(pos);
//                            reportRef.removeValue();
                            firebaseNotificationsApi = new FirebaseNotificationsApi(uidOfThePostAuthor, uidOfTheReporter, pos, "keep");
                            firebaseNotificationsApi.addKeepNotification();
                            return true;
                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        }

    }
}