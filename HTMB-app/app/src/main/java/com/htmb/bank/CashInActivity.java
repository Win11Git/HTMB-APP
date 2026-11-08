
package com.htmb.bank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.htmb.bank.api.ApiClient;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CashInActivity extends AppCompatActivity {

    private EditText amountInput;
    private Button cashinButton;

    private SharedPreferences preferences;

    private String name;
    private String mobileNumber;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashin);

        preferences = getSharedPreferences(
                "HTMB_SESSION", MODE_PRIVATE
        );

        amountInput = findViewById(R.id.amountInput);
        cashinButton = findViewById(R.id.cashinButton);
        ImageButton backButton = findViewById(R.id.cashinBackButton);

        name = preferences.getString("name", "HTMB User");
        mobileNumber = preferences.getString("mobileNumber", "");

        cashinButton.setOnClickListener(v -> cashin());
        backButton.setOnClickListener(v -> finish());

    }

    private void cashin() {

        String input = amountInput.getText().toString().trim();

        if (input.isEmpty()) {
            amountInput.setError("Enter an amount");
            return;
        }

        BigDecimal amount;

        try {
            amount = new BigDecimal(input);
        } catch (NumberFormatException e) {
            amountInput.setError("Enter a valid amount");
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            amountInput.setError("Amount must be greater than ₱0");
            return;
        }

        if (amount.scale() > 2 || amount.precision() > 15) {
            amountInput.setError("Enter a valid amount with up to 2 decimals");
            return;
        }

        String token = preferences.getString("token", null);

        if (token == null || token.isEmpty()) {
            Toast.makeText(this,
                    "Please login again.",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cashinButton.setEnabled(false);

        executor.execute(() -> {

            try {
                JSONObject request = new JSONObject();
                request.put("amount", amount);

                String response = ApiClient.request(
                        "POST",
                        "/api/cashin",
                        request.toString(),
                        token
                );

                JSONObject result = new JSONObject(response);

                String reference = result.getString("referenceNumber");
                String returnedName = result.optString("name", name);
                double cashInAmount = result.getDouble("amount");
                double newBalance = result.getDouble("balance");
                String date = result.getString("datetime");

                double previousBalance =
                        newBalance - cashInAmount;

                runOnUiThread(() -> {

                    Intent intent = new Intent(
                            CashInActivity.this,
                            ReceiptActivity.class
                    );

                    intent.putExtra("reference", reference);
                    intent.putExtra("name", returnedName);
                    intent.putExtra("accountNumber", mobileNumber);
                    intent.putExtra("amount", cashInAmount);
                    intent.putExtra("previousBalance", previousBalance);
                    intent.putExtra("newBalance", newBalance);
                    intent.putExtra("date", date);
                    intent.putExtra("type", "CASHIN");

                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {
                    Toast.makeText(
                            CashInActivity.this,
                            "Cash-In failed: Limit Exceed" ,
                            Toast.LENGTH_LONG
                    ).show();

                    cashinButton.setEnabled(true);
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
