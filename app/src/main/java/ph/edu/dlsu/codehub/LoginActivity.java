package ph.edu.dlsu.codehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail;
    private EditText loginPassword;
    private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPass);
        Button loginButton = findViewById(R.id.btnLogin);
        TextView signUpHere = findViewById(R.id.signUpHere);
        loading = new ProgressDialog(this);

        signUpHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    private void login() {
        String user = loginEmail.getText().toString();
        String pass = loginPassword.getText().toString();

        if (TextUtils.isEmpty(user)) {
            Toast.makeText(this, "You forgot to add your email", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "You forgot to add your password", Toast.LENGTH_SHORT).show();
        }
        else {
            loading.setTitle("Log In");
            loading.setMessage("Please wait while we are checking your account.");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            checkCredentials(user, pass);
        }
    }

    public void checkCredentials(final String username, final String password) {
        final DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").child(username).exists()) {
                    User person = dataSnapshot.child("Users").child(username).getValue(User.class);
                    if (person.getUsername().equals(username)) {
                        if (person.getPassword().equals(password)) {
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                            Intent intent = new Intent(Login.this, MainWindow.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(Login.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                            loading.dismiss();
                        }
                    }
                }
                else {
                    loading.dismiss();
                    Toast.makeText(Login.this, "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}