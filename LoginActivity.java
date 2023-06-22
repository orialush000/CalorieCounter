package com.example.caloriecounter;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtSignUpLink;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUpLink = findViewById(R.id.txtSignUpLink);

        databaseHelper = new DatabaseHelper(this);

        txtSignUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                }
                else {
                    int userId = authenticateUser(email, password);
                    if (userId != -1) {
                        Toast.makeText(LoginActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                        // Move to MainActivity with the user ID
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                        finish(); // Close the current activity
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private int authenticateUser(String email, String password) {
        Cursor cursor = databaseHelper.getUserByEmail(email);

        if (cursor.moveToFirst()) {
            int passwordColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_PASSWORD);
            int userIdColumnIndex = cursor.getColumnIndex(DatabaseContract.UserEntry.COLUMN_ID);

            if (passwordColumnIndex != -1 && userIdColumnIndex != -1) {
                String storedPassword = cursor.getString(passwordColumnIndex);
                int userId = cursor.getInt(userIdColumnIndex);

                if (password.equals(storedPassword)) {
                    cursor.close();
                    return userId;
                }
            }
        }

        cursor.close();
        return -1;
    }
}
