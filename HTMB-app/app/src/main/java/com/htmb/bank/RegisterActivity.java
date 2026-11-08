
package com.htmb.bank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.htmb.bank.api.ApiClient;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText phoneInput;
    private EditText pinInput;
    private Button registerButton;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        pinInput = findViewById(R.id.pinInput);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> register());
        ImageButton backButton = findViewById(R.id.registerBackButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void register() {

        String name = nameInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String pin = pinInput.getText().toString();

        if (name.isEmpty() || phone.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phone.matches("^09\\d{9}$")) {
            phoneInput.setError(
                    "Enter a valid 11-digit mobile number"
            );
            return;
        }

        if (!pin.matches("[0-9]{4}")) {
            pinInput.setError(
                    "PIN must contain exactly 4 digits"
            );
            return;
        }

        registerButton.setEnabled(false);

        executor.execute(() -> {

            try {
                JSONObject request = new JSONObject();

                request.put("name", name);
                request.put("mobileNumber", phone);
                request.put("pin", pin);

                String response = ApiClient.request(
                        "POST",
                        "/api/register",
                        request.toString(),
                        null
                );

                JSONObject result = new JSONObject(response);

                String message = result.optString(
                        "message",
                        "Registration successful."
                );

                runOnUiThread(() -> {

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(
                            RegisterActivity.this,
                            LoginActivity.class
                    );

                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    registerButton.setEnabled(true);

                    String message = "Registration failed.";

                    try {
                        JSONObject error =
                                new JSONObject(e.getMessage());

                        message = error.optString(
                                "message",
                                message
                        );

                    } catch (Exception ignored) {
                        // Keep the default error message.
                    }

                    Toast.makeText(
                            this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
