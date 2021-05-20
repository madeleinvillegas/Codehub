package ph.edu.dlsu.codehub.activityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import ph.edu.dlsu.codehub.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerName, registerEmail, registerPassword;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerBtn = findViewById(R.id.register_btn);
        registerEmail = findViewById(R.id.register_email);
        registerName = findViewById(R.id.register_name);
        registerPassword = findViewById(R.id.register_password);
        mAuth = FirebaseAuth.getInstance();

        registerBtn.setOnClickListener(view -> {
            createAccount();
        });

        TextView backToLogin = findViewById(R.id.login_here);
        backToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void createAccount() {
        String email = registerEmail.getText().toString();
        String name = registerName.getText().toString();
        String password = registerPassword.getText().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "You forgot to add your name", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "You forgot to add your email", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "You forgot to add your password", Toast.LENGTH_SHORT).show();
        }
        else if (password.length() < 8) {
            Toast.makeText(this, "Your password should be at least 8 characters long", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        String currentUserId = mAuth.getCurrentUser().getUid();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name).build();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            DatabaseReference UsersDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

                                            HashMap userMap = new HashMap();
                                            userMap.put("fullNameInLowerCase", name.toLowerCase());
                                            userMap.put("fullName", name);
                                            userMap.put("address", "");
                                            userMap.put("occupation", "");

                                            UsersDatabaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                                @Override
                                                public void onComplete(@NonNull Task task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(RegisterActivity.this, "Your account has been created", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(RegisterActivity.this, EditProfileDataActivity.class);
                                                        intent.putExtra("prior", "register");
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                    else {
                                                        String errorMessage = task.getException().getMessage();
                                                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                    else {
                        String message = Objects.requireNonNull(task.getException()).getMessage();
                        Toast.makeText(RegisterActivity.this, "Your account has not been created due to " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}