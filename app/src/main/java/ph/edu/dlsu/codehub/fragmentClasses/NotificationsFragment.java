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
        CircleImageView notificationImage;
        TextView notificationContent, timeStamp;

        public notificationsViewHolder(View itemView)
        {
            super(itemView);
            notificationContent = (TextView) itemView.findViewById(R.id.notification_description);
            notificationImage = (CircleImageView) itemView.findViewById(R.id.notification_image);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
        }

        //Add class methods here
        public void setContent(String content)
        {
            //Screw bold text
            notificationContent.setText(content);
        }

        public void setImage(String image) {
            //TODO: for some reason this code doesn't work

            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.ic_baseline_person_24)
                    .into(notificationImage);
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
                String image, time, date;
                image = model.getProfileImageLink();
                date = model.getCreationDate();
                time = model.getTime();
                holder.setImage(image);
                Log.d("DEBUGGING", image);
                holder.setTimeStamp(date, time);



                userRef.child(model.getActorUid()).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                    int code = model.getNotificationType();
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(code == 0)
                        {
                            holder.setContent(Objects.requireNonNull(snapshot.getValue()).toString() + " commented on your post");
                        }

                        else if (code == 1)
                        {
                            holder.setContent(Objects.requireNonNull(snapshot.getValue()).toString() + " liked your post");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


                userRef.child(model.getActorUid()).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
                    int code = model.getNotificationType();

                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if(code == 2)
                        {
                            holder.setContent(Objects.requireNonNull(snapshot.getValue()).toString() + "followed you");
                        }
                        else if (code == 3)
                        {
                            holder.setContent("You followed " + Objects.requireNonNull(snapshot.getValue()).toString());
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

                //TODO: DO SOMETHING ABOUT THIS
                holder.itemView.setOnClickListener(view -> {
                    int code = model.getNotificationType();

                    if (code == 0) {
                        // 0: Someone Liked Your Post
                        Intent intent = new Intent(getActivity(), ViewSinglePostActivity.class);
                        intent.putExtra("Position", model.getLinkUID());
                        startActivity(intent);

                    } else if (code == 1)
                    {
                        // 1: Someone Commented On Your Post
                        Intent intent = new Intent(getActivity(), ViewSinglePostActivity.class);
                        intent.putExtra("Position", model.getLinkUID());
                        startActivity(intent);

                    } else if (code == 2)
                    {
                        // 2: Someone Followed You
                        Intent intent = new Intent(getActivity(), ViewOtherProfileActivity.class);
                        intent.putExtra("Position", model.getLinkUID());
                        startActivity(intent);


                    } else if (code == 3)
                    {
                        // 2: Someone Followed You
                        Intent intent = new Intent(getActivity(), ViewOtherProfileActivity.class);
                        intent.putExtra("Position", model.getLinkUID());
                        startActivity(intent);
                    }
                });






            }
        };
        notificationsList.setAdapter(adapter);
        adapter.startListening();

    }
}
