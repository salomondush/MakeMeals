package com.example.makemeals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.makemeals.databinding.ActivitySignupBinding;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {


    private EditText signupUsername;
    private EditText signupPassword;
    private EditText getSignupPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.makemeals.databinding.ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signupUsername = binding.signupUsername;
        signupPassword = binding.signupPassword;
        getSignupPasswordConfirm = binding.confirmSignupPassword;
        Button signupButton = binding.signupButton;

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = signupUsername.getText().toString();
                String password = signupPassword.getText().toString();
                String confirmPassword = getSignupPasswordConfirm.getText().toString();

                if (password.equals(confirmPassword)){
                    signupUser(username, password);
                } else {
                    Toast.makeText(SignupActivity.this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void signupUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e != null) {
                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    goToMainActivity();
                    Toast.makeText(SignupActivity.this, R.string.sign_up_successful, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}