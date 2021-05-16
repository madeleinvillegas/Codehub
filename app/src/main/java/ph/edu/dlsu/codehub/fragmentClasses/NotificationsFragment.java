package ph.edu.dlsu.codehub.fragmentClasses;

import android.annotation.SuppressLint;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.dlsu.codehub.Notifications;
import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.User;
//The function of this class is to read and display whatever notifications there are:



//EVENT TYPES:
//Event Codes:
// 0: Someone Liked Your Post
// 1: Someone Commented On Your Post
// 2: Someone Followed You
// 3: You Followed Someone



public class NotificationsFragment extends Fragment {

    //FIREBASE STUFF HERE:
    private DatabaseReference userNotificationsDatabase;
    private String userId;

    //ID VARIABLES
    private RecyclerView notificationsList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications,container,false);
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userNotificationsDatabase = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId);

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
            //NOTE: not sure if this function works 100%
            //TODO: Read https://stackoverflow.com/questions/36045522/android-picasso-image-does-not-load
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.ic_baseline_event_seat_24)
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
        FirebaseRecyclerOptions<Notifications> options =
                new FirebaseRecyclerOptions.Builder<Notifications>()
                        .setQuery(userNotificationsDatabase, Notifications.class)
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
                String content, image, time, date;
                content = model.getNotificationContent();
                image = model.getImage();
                date = model.getCreationDate();
                time = model.getTime();

                holder.setContent(content);
                holder.setImage(image);
                holder.setTimeStamp(date, time);
            }
        };
        notificationsList.setAdapter(adapter);
        adapter.startListening();

    }
}
