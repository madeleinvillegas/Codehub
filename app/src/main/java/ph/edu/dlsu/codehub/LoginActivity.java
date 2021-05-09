package ph.edu.dlsu.codehub;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

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
                        Intent intent = new Intent(LoginActivity.this, ProfileTemplate.class);
                        startActivity(intent);
                    }
                    else {
                        String message = Objects.requireNonNull(task.getException()).toString();
                        Toast.makeText(LoginActivity.this, "Login unsuccessful due to " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

//  Allows the user to not login every time they open the app
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, ProfileTemplate.class);
            startActivity(intent);
        }
    }
}

//    Code for logging out the user
//    private FirebaseAuth mAuth;
//    mAuth = FirebaseAuth.getInstance();
//    mAuth.signOut();
//    go back to login