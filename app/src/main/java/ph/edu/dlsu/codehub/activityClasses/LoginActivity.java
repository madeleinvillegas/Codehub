package ph.edu.dlsu.codehub.activityClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ph.edu.dlsu.codehub.R;
import ph.edu.dlsu.codehub.fragmentClasses.ProfileTemplate;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        Button loginBtn = findViewById(R.id.login_btn);
        Button registerHere = findViewById(R.id.register_here);
        TextView forgotPassword = findViewById(R.id.forgot_password);

        registerHere.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        forgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
        loginBtn.setOnClickListener(view -> {
            login();
        });

    }
    private void login() {
        String email = loginEmail.getText().toString();
        String pass = loginPassword.getText().toString();
        mAuth = FirebaseAuth.getInstance();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "You forgot to add your email address", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "You forgot to add your password", Toast.LENGTH_SHORT).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                        if (userId.equals("p96ZIBEpCZZ7nShNuMZE4aKAhE22")) {
                            Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                DatabaseReference dataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getUid()));
                                dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        checkForNullValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });

                            } else {
                                mAuth.getCurrentUser().sendEmailVerification();
                                Toast.makeText(LoginActivity.this, "Please check your email and verify your account", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else {
                        String message = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(LoginActivity.this, "Login unsuccessful due to " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void checkForNullValue(){
        Log.d("mAuth", mAuth.getUid());


        DatabaseReference dataBase = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getUid()));
        dataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                boolean nullValue = false;

                for(DataSnapshot attribute: snapshot.getChildren())
                {
                    if(attribute.getValue() == null || Objects.toString(attribute.getValue(), "").isEmpty() || !snapshot.exists())
                    {
                        nullValue = true;
                    }
                }
                Intent intent;
                if(nullValue)
                {
                    Toast.makeText(getApplicationContext(), "You didn't properly fill out your data last time.", Toast.LENGTH_SHORT).show();
                    intent = new Intent(getApplicationContext(), EditProfileDataActivity.class);
                    intent.putExtra("prior", "login");
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
                    intent = new Intent(getApplicationContext(), ProfileTemplate.class);
                }
                startActivity(intent);
                finish(); //this would prevent any loose ends

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            if (currentUser.getUid().equals("p96ZIBEpCZZ7nShNuMZE4aKAhE22")) {
                Intent intent = new Intent(LoginActivity.this, AdminActivity.class);
                startActivity(intent);
            }
            else {
                if (currentUser.isEmailVerified()) {
                    checkForNullValue();
                } else {
                    mAuth.getCurrentUser().sendEmailVerification();
                    Toast.makeText(this, "Please check your email and verify your account", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
