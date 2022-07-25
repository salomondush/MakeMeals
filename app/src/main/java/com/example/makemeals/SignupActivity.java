package com.example.makemeals;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.makemeals.databinding.ActivitySignupBinding;
import com.example.makemeals.models.Recommendation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {


    private EditText signupUsernameEditText;
    private EditText signupPasswordEditText;
    private EditText confirmSignupPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.makemeals.databinding.ActivitySignupBinding binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        signupUsernameEditText = binding.signupUsernameEditText;
        signupPasswordEditText = binding.signupPasswordEditText;
        confirmSignupPasswordEditText = binding.confirmSignupPasswordEditText;
        Button signupMaterialButton = binding.signupMaterialButton;

        signupMaterialButton.setOnClickListener(v -> {
            String username = signupUsernameEditText.getText().toString();
            String password = signupPasswordEditText.getText().toString();
            String confirmPassword = confirmSignupPasswordEditText.getText().toString();

            if (password.equals(confirmPassword)){
                signupUser(username, password);
            } else {
                Toast.makeText(SignupActivity.this, R.string.passwords_do_not_match, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signupUser(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(e -> {
            if (e != null) {
                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            } else {
                Recommendation recommendation = new Recommendation();
                recommendation.setUser(ParseUser.getCurrentUser());
                recommendation.saveInBackground();
                goToMainActivity();
                Toast.makeText(SignupActivity.this, R.string.sign_up_successful, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}