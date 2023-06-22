package com.example.caloriecounter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword, edtAge, edtHeight, edtWeight;
    private Spinner spnGender, spnGoal;
    private Button btnSignUp;
    private TextView txtLoginLink;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtAge = findViewById(R.id.edtAge);
        edtHeight = findViewById(R.id.edtHeight);
        edtWeight = findViewById(R.id.edtWeight);
        spnGender = findViewById(R.id.spnGender);
        spnGoal = findViewById(R.id.spnGoal);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtLoginLink = findViewById(R.id.txtLoginLink);

        databaseHelper = new DatabaseHelper(this);

        txtLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();
                String ageStr = edtAge.getText().toString().trim();
                String gender = spnGender.getSelectedItem().toString();
                String goal = spnGoal.getSelectedItem().toString();
                String heightStr = edtHeight.getText().toString().trim();
                String weightStr = edtWeight.getText().toString().trim();

                // Check if any field is empty
                if (email.isEmpty() || password.isEmpty() || ageStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty() || gender.isEmpty() || goal.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check email format
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignUpActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check password format
                if (!password.matches("(?=.*[A-Za-z])(?=.*\\d).{7,}")) {
                    Toast.makeText(SignUpActivity.this, "Password must be at least 7 characters long and contain at least 1 letter and 1 digit", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check age format
                if (!ageStr.matches("\\d+")) {
                    Toast.makeText(SignUpActivity.this, "Invalid age", Toast.LENGTH_SHORT).show();
                    return;
                }
                int age = Integer.parseInt(ageStr);

                // Check height format
                if (!heightStr.matches("\\d+(\\.\\d+)?")) {
                    Toast.makeText(SignUpActivity.this, "Invalid height", Toast.LENGTH_SHORT).show();
                    return;
                }
                double height = Double.parseDouble(heightStr);

                // Check weight format
                if (!weightStr.matches("\\d+(\\.\\d+)?")) {
                    Toast.makeText(SignUpActivity.this, "Invalid weight", Toast.LENGTH_SHORT).show();
                    return;
                }
                double weight = Double.parseDouble(weightStr);

                // All fields are valid, proceed with creating the user
                long userId = databaseHelper.addUser(email, password, age, gender, goal, height, weight);

                 if (userId != -1) {
                    Toast.makeText(SignUpActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();

                    // Move to MainActivity with the user ID
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.putExtra("user_id", (int) userId);
                    startActivity(intent);
                    finish(); // Close the current activity
                } else {
                    Toast.makeText(SignUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}