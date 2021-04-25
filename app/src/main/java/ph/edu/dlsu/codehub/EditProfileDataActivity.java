package ph.edu.dlsu.codehub;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ph.edu.dlsu.codehub.fragmentClasses.ProfileTemplate;

public class EditProfileDataActivity extends AppCompatActivity {
    private TextView editProfilePicture, editBackgroundPicture;
    private EditText firstName, lastName, currentUserName, currentAddress, currentOccupation;
    private Button saveChanges;
    private ImageView currentBackgroundPicture;
    private CircleImageView currentProfilePicture;


    private FirebaseAuth mAuth;
    private DatabaseReference usersDatabaseReference;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        usersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);



        //ID references
        editProfilePicture = (TextView) findViewById(R.id.edit_profile_picture);
        editBackgroundPicture = (TextView) findViewById(R.id.edit_background_image);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        currentUserName = (EditText) findViewById(R.id.current_user_name);
        currentAddress = (EditText) findViewById(R.id.current_address);
        currentOccupation = (EditText) findViewById(R.id.current_occupation);
        saveChanges = (Button) findViewById(R.id.save_changes_button);
        currentProfilePicture = (CircleImageView) findViewById(R.id.profile_picture) ;
        currentBackgroundPicture = (ImageView) findViewById(R.id.background_image);




        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAccountInformation();
            }
        });
        setContentView(R.layout.activity_edit_profile);
    }

    private void saveAccountInformation(){
        String profilePictureText, backgroundPictureText, firstNameText, lastNameText, currentUserNameText, currentAddressText, currentOccupationText;

        //assumption is that every field is mandatory

        //picture related stuff here: (get the source of image)

        profilePictureText = "";
        backgroundPictureText = "";


        //non picture related stuff here
        firstNameText = firstName.getText().toString();
        lastNameText = lastName.getText().toString();
        currentUserNameText = currentUserName.getText().toString();
        currentAddressText = currentAddress.getText().toString();
        currentOccupationText = currentOccupation.getText().toString();

        if(TextUtils.isEmpty(firstNameText) || TextUtils.isEmpty(lastNameText))
        {
            Toast.makeText(this, "Please Input Your Name", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(currentUserNameText))
        {
            Toast.makeText(this, "Please Input Current Username", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(currentAddressText))
        {
            Toast.makeText(this, "Please Input An Address", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(currentOccupationText))
        {
            Toast.makeText(this, "Please Input Your Current Occupation", Toast.LENGTH_SHORT);
        }
        else
        {
            HashMap userMap = new HashMap();
            userMap.put("username", currentUserNameText);
            userMap.put("firstName", firstNameText);
            userMap.put("lastName", lastNameText);
            userMap.put("address", currentAddressText);
            userMap.put("occupation", currentOccupationText);
            userMap.put("profilePicture", profilePictureText);
            userMap.put("backgroundImage", backgroundPictureText);


            //I noticed that theis doesn't check if there are duplicates

            //
            usersDatabaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        sendUserToHomePage();  // send user to main activity
                        Toast.makeText(EditProfileDataActivity.this, "Profile Data Changed Successfully", Toast.LENGTH_LONG);
                    }
                    else
                    {
                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(EditProfileDataActivity.this, errorMessage, Toast.LENGTH_LONG);

                    }
                }
            });
        }
    }

    private void sendUserToHomePage() {
        Intent intent = new Intent(EditProfileDataActivity.this, ProfileTemplate.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        //what does this function do exactly will google later
    }
}
