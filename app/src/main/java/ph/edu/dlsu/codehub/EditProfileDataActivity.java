                                                                                                                                                                               package ph.edu.dlsu.codehub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
    private DatabaseReference UsersDatabaseReference;
    private String currentUserId;

    private final static int gallery_pick = 1;

//    private ProgressBar progressBar;

    private StorageReference userProfileImageRef;

    private String TAG = "DEBUGGING_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        //file structure would be currentUserId/
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child(currentUserId);

        UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        //ID references
        editProfilePicture = (TextView) findViewById(R.id.edit_profile_picture);
//        editBackgroundPicture = (TextView) findViewById(R.id.edit_background_image);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        currentUserName = (EditText) findViewById(R.id.current_user_name);
        currentAddress = (EditText) findViewById(R.id.current_address);
        currentOccupation = (EditText) findViewById(R.id.current_occupation);
        saveChanges = findViewById(R.id.save_changes_button);
        currentProfilePicture = (CircleImageView) findViewById(R.id.profile_picture) ;
//        currentBackgroundPicture = (ImageView) findViewById(R.id.background_image);

        saveChanges.setOnClickListener(view -> {
            saveAccountInformation();
        });

        editProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, gallery_pick);
            }
        });
//        editBackgroundPicture.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent galleryIntent = new Intent();
//                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//                galleryIntent.setType("image/*");
//                startActivityForResult(galleryIntent, gallery_pick);
//            }
//        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == gallery_pick && resultCode == RESULT_OK && data != null)
        {
//            Uri ImageUri = data.getData();

            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                Uri resultUri = result.getUri();
                String extension = resultUri.toString().substring(resultUri.toString().lastIndexOf("."));
                //TODO: add code to upload image to firebase, download image and set image attributes in real time data base
                //upload profile picture
                Log.d(TAG, resultUri.toString());

                StorageReference profilePic = userProfileImageRef.child("profile_image" + extension);
                UploadTask uploadTask = profilePic.putFile(resultUri);
                Log.d(TAG, "Image cropped and Uploading File Here");

                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Log.d(TAG, "Uploading of Image was Successful");
                            //get download url (nasty workaround)
                            String downloadUrl = profilePic.getDownloadUrl().toString();
                            Log.d(TAG, downloadUrl);
                            //set database reference in real time data base
                            UsersDatabaseReference.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //download and set profile picture

                                }
                            });
                        }
                        else
                        {
                            Log.d(TAG, "Uploading of Image Failed: " + task.getException());
                        }
                    }
                });




            }
            else {
                Log.d(TAG, "Error. Image cannot be cropped.");
            }

        }
    }

    private void saveAccountInformation(){
        Log.d(TAG, "Calling Save Account Information");

        String profilePictureText, backgroundPictureText, firstNameText, lastNameText, currentUserNameText, currentAddressText, currentOccupationText;

        //assumption is that every field is mandatory

        //picture related stuff here: (get the source of image)



        //non picture related stuff here
        firstNameText = firstName.getText().toString();
        lastNameText = lastName.getText().toString();
        currentUserNameText = currentUserName.getText().toString();
        currentAddressText = currentAddress.getText().toString();
        currentOccupationText = currentOccupation.getText().toString();

        if(TextUtils.isEmpty(firstNameText) || TextUtils.isEmpty(lastNameText))
        {
            Log.d(TAG, "Empty first name or last name");
            Toast.makeText(this, "Please Input Your Name", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(currentUserNameText))
        {
            Log.d(TAG, "Empty username");

            Toast.makeText(this, "Please Input Current Username", Toast.LENGTH_SHORT);
        }
        else if (TextUtils.isEmpty(currentAddressText))
        {
            Log.d(TAG, "Empty Address");

            Toast.makeText(this, "Please Input An Address", Toast.LENGTH_SHORT);
        }
        else if(TextUtils.isEmpty(currentOccupationText))
        {
            Log.d(TAG, "Empty Occupation");
            Toast.makeText(this, "Please Input Your Current Occupation", Toast.LENGTH_SHORT);
        }
        else
        {
//            progressBar.setVisibility(View.VISIBLE);

            HashMap userMap = new HashMap();
            userMap.put("username", currentUserNameText);
            userMap.put("firstName", firstNameText);
            userMap.put("lastName", lastNameText);
            userMap.put("address", currentAddressText);
            userMap.put("occupation", currentOccupationText);
            userMap.put("profilePicture", "");
            userMap.put("backgroundImage", "");


            //I noticed that theis doesn't check if there are duplicates

            //
            UsersDatabaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful())
                    {
                        sendUserToHomePage();  // send user to main activity
                        Log.d(TAG, "Data Change Successful");

                        Toast.makeText(getApplicationContext(), "Profile Data Changed Successfully", Toast.LENGTH_LONG);
//                        progressBar.setVisibility(View.GONE);

                    }
                    else
                    {
                        Log.d(TAG, "Data Change Failed");

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
//                        progressBar.setVisibility(View.GONE);


                    }
                }
            });
        }

        Log.d(TAG, "Finished calling save account information");

    }

    private void sendUserToHomePage() {
        Intent intent = new Intent(getApplicationContext(), ProfileTemplate.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        //what does this function do exactly will google later
    }
}
