package ph.edu.dlsu.codehub.fragmentClasses;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.dlsu.codehub.activityClasses.RegisterActivity;
import ph.edu.dlsu.codehub.activityClasses.ViewOtherProfileActivity;
import ph.edu.dlsu.codehub.activityClasses.ViewSinglePostActivity;
import ph.edu.dlsu.codehub.helperClasses.Notifications;
import ph.edu.dlsu.codehub.R;
//The function of this class is to read and display whatever notifications there are:

//TODO: Read further https://stackoverflow.com/questions/24471109/recyclerview-onclick
//TODO: Focus on the general functionalities


//EVENT TYPES:
//Event Codes:
// 0: Someone Liked Your Post
// 1: Someone Commented On Your Post
// 2: Someone Followed You
// 3: You Followed Someone
// 4: Your post has been reported
// 5: The reported post was deleted
// 6: The reported post was kept


//TODO: Make Event Notifications On Clickable



public class NotificationsFragment extends Fragment {

    //FIREBASE STUFF HERE:
    private DatabaseReference userNotificationsDatabase;
    private DatabaseReference userRef;

    private String userId;

    //ID VARIABLES
    private RecyclerView notificationsList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userNotificationsDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        Log.d("Notifications: ", "USERID: " + userId);

        //ID INITIALIZATIONS
        notificationsList = view.findViewById(R.id.notifications_list);
        notificationsList.setHasFixedSize(true);


        //Set Linear Layout
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        notificationsList.setLayoutManager(linearLayoutManager);


        return view;
    }

    public static class notificationsViewHolder extends RecyclerView.ViewHolder{
        TextView notificationContent, timeStamp;

        public notificationsViewHolder(View itemView) {
            super(itemView);
            notificationContent = (TextView) itemView.findViewById(R.id.notification_description);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
        }

        //Add class methods here
        public void setContent(String content) {
            notificationContent.setText(content);
        }


        @SuppressLint("SetTextI18n")
        public void setTimeStamp(String date, String time)
        {
            timeStamp.setText(date + " | " + time);
        }




    }

    @Override
    public void onStart() {
        super.onStart();
        displayNotifications();
    }

    public void displayNotifications()
    {

        Query query = userNotificationsDatabase.orderByKey();


        FirebaseRecyclerOptions<Notifications> options =
                new FirebaseRecyclerOptions.Builder<Notifications>()
                        .setQuery(query, Notifications.class)
                        .build();


        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Notifications, notificationsViewHolder>(options){
            @NonNull
            @NotNull
            @Override
            public notificationsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notification_display_layout, parent, false);

                return new notificationsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull @NotNull notificationsViewHolder holder, int position, @NonNull @NotNull Notifications model) {
                String time, date;
                date = model.getCreationDate();
                time = model.getTime();
                holder.setTimeStamp(date, time);
                int codes = model.getNotificationType();
//                Log.d("DEBUG", model.getActorUid());
                if (codes == 0 || codes == 1) {
                    Log.d("Actor UID", model.getActorUid());

                    userRef.child(model.getActorUid()).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(codes == 0) {
                                holder.setContent(Objects.requireNonNull(snapshot.getValue()).toString() + " commented on your post");
                            } else {
                                holder.setContent(Objects.requireNonNull(snapshot.getValue()).toString() + " liked your post");
                            }
                        }
                        @Override public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                    });
                } else if (codes == 2 || codes == 3) {
                    userRef.child(model.getActorUid()).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if(codes == 2) {
                                holder.setContent(Objects.requireNonNull(snapshot.getValue()).toString() + "followed you");
                            } else {
                                holder.setContent("You followed " + Objects.requireNonNull(snapshot.getValue()).toString());
                            }

                        }
                        @Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
                    });
                } else {
                    holder.setContent(model.getNotificationContent());
                }

                holder.itemView.setOnClickListener(view -> {
                    int code = model.getNotificationType();
                    Intent intent;
                    switch(code) {
                        case 0: // 0: Someone Liked Your Post
                        case 1: // 1: Someone Commented On Your Post
                        case 6: // 6: The reported post was kept
                            intent = new Intent(getActivity(), ViewSinglePostActivity.class);
                            intent.putExtra("Position", model.getLinkUID());
                            startActivity(intent);
                            break;
                        case 2: // 2: Someone Followed You
                        case 3: // 3: You followed someone
                        case 5: // 5: The reported post was deleted
                            intent = new Intent(getActivity(), ViewOtherProfileActivity.class);
                            intent.putExtra("Position", model.getLinkUID());
                            startActivity(intent);
                            break;
                        case 4: // 4: Your post has been reported
                            DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    if (snapshot.hasChild(model.getLinkUID())) {
                                        Intent intent = new Intent(getActivity(), ViewSinglePostActivity.class);
                                        intent.putExtra("Position", model.getLinkUID());
                                        startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(getActivity(), ViewOtherProfileActivity.class);
                                        String smth = model.getLinkUID();
                                        String[] uid = smth.split(" ");
                                        intent.putExtra("Position", uid[0]);
                                        startActivity(intent);
                                    }
                                }
                                @Override public void onCancelled(@NonNull @NotNull DatabaseError error) { }
                            });
                            break;

                    }
                });
            }
        };
        notificationsList.setAdapter(adapter);
        adapter.startListening();

    }
}
