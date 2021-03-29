package ph.edu.dlsu.codehub;

import android.os.Bundle;

public class SignupActivity extends AppCompatActivity {

    private EditText signEmail;
    private EditText signPassword;
    private EditText signName;
    private CheckBox terms;
    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signName = findViewById(R.id.signUpName);
        signEmail = findViewById(R.id.signUpUsername);
        signPassword = findViewById(R.id.signUpPass);
        terms = findViewById(R.id.confirm);
        Button signUpClick = findViewById(R.id.btnSignUp);
        TextView backToLogin = findViewById(R.id.logInHere);
        loading = new ProgressDialog(this);

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        signUpClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    public void createAccount() {
        String name = signName.getText().toString();
        String email = signEmail.getText().toString();
        String pass = signPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "You forgot to add your name", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "You forgot to add your email", Toast.LENGTH_SHORT).show();
        }

        else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(this, "You forgot to add your password", Toast.LENGTH_SHORT).show();
        }
        else if (pass.length() < 8) {
            Toast.makeText(this, "Your password should be at least 8 characters long", Toast.LENGTH_SHORT).show();
        }
        else if (!terms.isChecked()) {
            Toast.makeText(this, "You need to consent to our Terms and Conditions and Privacy Policy.", Toast.LENGTH_SHORT).show();
        }
        else {
            loading.setTitle("Create Account");
            loading.setMessage("Please wait while we are creating you account.");
            loading.setCanceledOnTouchOutside(false);
            loading.show();
            checkEmail(name, email, pass);
        }
    }

    public void checkEmail(final String name, final String username, final String pass) {
        final DatabaseReference reference;
        reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("Users").child(username).exists()) {
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("username", username);
                    user.put("password", pass);
                    reference.child("Users").child(username).updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Signup.this, "Your account has been created", Toast.LENGTH_SHORT).show();
                                loading.dismiss();
                                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else {
                                loading.dismiss();
                                Toast.makeText(SignupActivity.this, "Your account has not been created. PLease try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else {
                    Toast.makeText(Signup.this, "The username has been taken", Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                    Toast.makeText(Signup.this, "Try again using a different username", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}