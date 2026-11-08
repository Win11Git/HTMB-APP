
package com.htmb.bank;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageButton;
import android.provider.Settings;


import androidx.appcompat.app.AppCompatActivity;

import com.htmb.bank.api.ApiClient;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneInput;
    private EditText pinInput;
    private Button loginButton;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phoneInput = findViewById(R.id.phoneInput);
        pinInput = findViewById(R.id.pinInput);
        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> login());
        ImageButton backButton = findViewById(R.id.loginBackButton);
        backButton.setOnClickListener(v -> finish());
    }

    private void login() {

        String phone = phoneInput.getText().toString().trim();
        String pin = pinInput.getText().toString();
        String deviceId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );


        if (phone.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this,
                    "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pin.matches("[0-9]{4}")) {
            pinInput.setError("PIN must contain exactly 4 digits");
            return;
        }

        loginButton.setEnabled(false);

        executor.execute(() -> {

            try {
                JSONObject request = new JSONObject();

                request.put("mobileNumber", phone);
                request.put("pin", pin);
                request.put("deviceId", deviceId);

                String response = ApiClient.request(
                        "POST",
                        "/api/login",
                        request.toString(),
                        null
                );

                JSONObject result = new JSONObject(response);

                String token = result.optString("token");

                if (token.isEmpty()) {
                    String message = result.optString(
                            "message",
                            "Login failed. Please try again."
                    );

                    throw new Exception(message);
                }

                String name = result.optString("name");
                long userId = result.optLong("userId");
                double balance = result.optDouble("balance");

                // Save login information
                SharedPreferences preferences =
                        getSharedPreferences("HTMB_SESSION", MODE_PRIVATE);

                preferences.edit()
                        .putString("token", token)
                        .putString("name", name)
                        .putLong("userId", userId)
                        .putString("mobileNumber", phone)
                        .apply();

                runOnUiThread(() -> {

                    Intent intent = new Intent(
                            LoginActivity.this,
                            DashboardActivity.class
                    );

                    intent.putExtra("name", name);
                    intent.putExtra("userId", userId);
                    intent.putExtra("balance", balance);

                    startActivity(intent);
                    finish();

                });

            } catch (Exception e) {

                runOnUiThread(() -> {
                    Toast.makeText(
                            LoginActivity.this,
                            "Login failed: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                    loginButton.setEnabled(true);
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
