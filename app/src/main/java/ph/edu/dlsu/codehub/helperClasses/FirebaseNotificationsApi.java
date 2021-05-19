package ph.edu.dlsu.codehub.helperClasses;

//for cleaner code, I created this class

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class FirebaseNotificationsApi {

    private DatabaseReference notificationRef, usersRef, postsRef;
    private String uidOfThePostAuthor, userIdOfActor, linkId;
    private int mode;
    private Calendar calendar;
    private  SimpleDateFormat currTime, currDate;


    @SuppressLint("SimpleDateFormat")
    public FirebaseNotificationsApi() {
        currTime = new SimpleDateFormat("HH:mm:ss:SS");
        currDate = new SimpleDateFormat("dd MMMM yyyy");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        calendar = Calendar.getInstance();
    }

    public FirebaseNotificationsApi(String uidOfThePostAuthor, String userIdOfActor, String linkId, String mode) {
        this();
        this.uidOfThePostAuthor = uidOfThePostAuthor;
        this.userIdOfActor = userIdOfActor;
        this.linkId = linkId;

        if(mode.equals("comment"))
        {
            this.mode = 0;

        } else if (mode.equals("like"))
        {
            this.mode = 1;

        } else if (mode.equals("followTo"))
        {
            this.mode = 3;

        } else if (mode.equals("followedBy"))
        {
            this.mode = 2;

        }else if (mode.equals("keep"))
        {
            this.mode = 6;

        }else if (mode.equals("delete"))
        {
            this.mode = 5;

        }
    }


    public void addLikeNotification()
    {
        String currentTime = currTime.format(calendar.getTime());
        String currentDate = currDate.format(calendar.getTime());
        String NotificationID = currentDate + " | " + currentTime + " | " + uidOfThePostAuthor + " | " + userIdOfActor ;

        Notifications notification = new Notifications();
        notification.setCreationDate(currentDate);
        notification.setProfileImageLink(FirebaseDatabase.getInstance().getReference().child("Users").child(userIdOfActor).child("profileImageLink").toString());
        notification.setLinkUID(linkId);
        notification.setNotificationType(mode);
        notification.setTime(currentTime);
        notification.setActorUid(userIdOfActor);

        //some complicated stuff
        usersRef.child(userIdOfActor).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                Log.d("Like ID", userIdOfActor);
                Log.d("Like ID", snapshot.getValue().toString());

                String notificationContent = snapshot.getValue().toString() + " liked your post";
                notification.setNotificationContent(notificationContent);
                notificationRef.child(uidOfThePostAuthor).child(NotificationID).setValue(notification);

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }
    
    public void addCommentNotification()
    {
        String currentTime = currTime.format(calendar.getTime());
        String currentDate = currDate.format(calendar.getTime());
        String timestamp = currentDate + " " + currentTime + userIdOfActor + " ";
        
        
        usersRef.child(userIdOfActor).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.d("ID", userIdOfActor);
                Log.d("ID", snapshot.getValue().toString());

                String notificationContent = snapshot.getValue().toString()+ " commented on your post";
                
                Notifications notification = new Notifications();
                notification.setCreationDate(currentDate);
                notification.setProfileImageLink(usersRef.child(userIdOfActor).child("profileImageLink").toString());
                notification.setLinkUID(linkId);
                notification.setNotificationContent(notificationContent);
                notification.setNotificationType(mode);
                notification.setTime(currentTime);
                notification.setActorUid(userIdOfActor);

                postsRef.child(linkId).child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String authorUID = Objects.requireNonNull(snapshot.getValue()).toString();
                        notificationRef.child(authorUID).child(timestamp).setValue(notification);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });


    }
    
    public void addFollowedTo(String someoneThatYouFollowed)
    {
        String currentTime = currTime.format(calendar.getTime());
        String currentDate = currDate.format(calendar.getTime());
        String NotificationID = currentDate + " | " + currentTime + " | " + someoneThatYouFollowed + " | " + userIdOfActor ;

        //some complicated stuff
        usersRef.child(someoneThatYouFollowed).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Notifications notification = new Notifications();
                notification.setCreationDate(currentDate);
                notification.setProfileImageLink(FirebaseDatabase.getInstance().getReference().child("Users").child(userIdOfActor).child("profileImageLink").toString());
                notification.setLinkUID(someoneThatYouFollowed);
                notification.setNotificationType(mode);
                notification.setTime(currentTime);
                notification.setActorUid(someoneThatYouFollowed);


                String notificationContent = "You followed " + snapshot.getValue().toString();
                notification.setNotificationContent(notificationContent);
                notificationRef.child(userIdOfActor).child(NotificationID).setValue(notification);

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });


    }

    public void addFollowedBy(String someoneThatFollowedYou)
    {
        String currentTime = currTime.format(calendar.getTime());
        String currentDate = currDate.format(calendar.getTime());
        String NotificationID = currentDate + " | " + currentTime + " | " + someoneThatFollowedYou + " | " + userIdOfActor ;

        //some complicated stuff
        usersRef.child(someoneThatFollowedYou).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Notifications notification = new Notifications();
                notification.setCreationDate(currentDate);
                notification.setProfileImageLink(FirebaseDatabase.getInstance().getReference().child("Users").child(userIdOfActor).child("profileImageLink").toString());
                notification.setLinkUID(someoneThatFollowedYou);
                notification.setNotificationType(mode);
                notification.setTime(currentTime);
                notification.setActorUid(someoneThatFollowedYou);


                String notificationContent = snapshot.getValue().toString() + "Followed you ";
                notification.setNotificationContent(notificationContent);
                notificationRef.child(userIdOfActor).child(NotificationID).setValue(notification);

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });


    }
    public String getUidOfThePostAuthor() {
        return uidOfThePostAuthor;
    }

    public void setUidOfThePostAuthor(String uidOfThePostAuthor) {
        this.uidOfThePostAuthor = uidOfThePostAuthor;
    }

    public String getuserIdOfActor() {
        return userIdOfActor;
    }

    public void setuserIdOfActor(String userIdOfActor) {
        this.userIdOfActor = userIdOfActor;
    }

    public String getlinkId() {
        return linkId;
    }

    public void setlinkId(String linkId) {
        this.linkId = linkId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void addDeleteNotification() {
        String currentTime = currTime.format(calendar.getTime());
        String currentDate = currDate.format(calendar.getTime());
        String timestamp = currentDate + " " + currentTime + " " + userIdOfActor + " ";

        usersRef.child(userIdOfActor).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.d("ID", userIdOfActor);
                Log.d("ID", snapshot.getValue().toString());

                String notificationContent = "The admin has decided to delete the reported post on the app";

                Notifications notification = new Notifications();
                notification.setCreationDate(currentDate);
                notification.setNotificationContent(notificationContent);
                notification.setNotificationType(mode);
                notification.setTime(currentTime);
                notification.setProfileImageLink(usersRef.child(userIdOfActor).child("profileImageLink").toString());

                notificationRef.child(uidOfThePostAuthor).child(timestamp).setValue(notification);
                notification.setNotificationContent("The admin has decided to delete the post you reported on the app");
                notificationRef.child(userIdOfActor).child(timestamp).setValue(notification);


            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });
    }

    public void addKeepNotification() {
        String currentTime = currTime.format(calendar.getTime());
        String currentDate = currDate.format(calendar.getTime());
        String timestamp = currentDate + " " + currentTime + " " + userIdOfActor + " ";


        usersRef.child(userIdOfActor).child("fullName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Log.d("ID", userIdOfActor);
                Log.d("ID", snapshot.getValue().toString());

                String notificationContent = "The admin has decided to keep the post on the app";

                Notifications notification = new Notifications();
                notification.setCreationDate(currentDate);
                notification.setProfileImageLink(usersRef.child(userIdOfActor).child("profileImageLink").toString());
                notification.setLinkUID(linkId);
                notification.setNotificationContent(notificationContent);
                notification.setNotificationType(mode);
                notification.setTime(currentTime);

                postsRef.child(linkId).child("uid").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        String authorUID = Objects.requireNonNull(snapshot.getValue()).toString();
                        notificationRef.child(authorUID).child(timestamp).setValue(notification);
                        notification.setNotificationContent("The admin has decided to keep the post you reported on the app");
                        notificationRef.child(userIdOfActor).child(timestamp).setValue(notification);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }
}
