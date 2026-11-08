
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

public class TransferActivity extends AppCompatActivity {

    private EditText recipientAccountInput;
    private EditText amountInput;
    private Button transferButton;

    private SharedPreferences preferences;

    private String name;
    private String mobileNumber;

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        preferences = getSharedPreferences(
                "HTMB_SESSION", MODE_PRIVATE
        );

        recipientAccountInput = findViewById(R.id.recipientAccountInput);
        amountInput = findViewById(R.id.amountInput);

        transferButton = findViewById(R.id.transferButton);
       ImageButton backButton = findViewById(R.id.transferBackButton);

        name = preferences.getString("name", "HTMB User");
        mobileNumber = preferences.getString("mobileNumber", "");

        transferButton.setOnClickListener(v -> transfer());
        backButton.setOnClickListener(v -> finish());
    }

    private void transfer() {

        String recipient = recipientAccountInput.getText()
                .toString().trim();

        String input = amountInput.getText().toString().trim();

        if (recipient.isEmpty()) {
            recipientAccountInput.setError(
                    "Enter recipient mobile number"
            );
            return;
        }

        if (!recipient.matches("^09\\d{9}$")) {
            recipientAccountInput.setError(
                    "Enter a valid 11-digit mobile number"
            );
            return;
        }

        if (recipient.equals(mobileNumber)) {
            recipientAccountInput.setError(
                    "You cannot transfer to yourself"
            );
            return;
        }

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
            amountInput.setError(
                    "Amount must be greater than ₱0"
            );
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            amountInput.setError(
                    "Amount must be greater than ₱0"
            );
            return;
        }

        if (amount.scale() > 2 || amount.precision() > 15) {
            amountInput.setError(
                    "Enter a valid amount with up to 2 decimals"
            );
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

        transferButton.setEnabled(false);

        executor.execute(() -> {

            try {
                JSONObject request = new JSONObject();

                request.put("recipientMobileNumber", recipient);
                request.put("amount", amount);

                String response = ApiClient.request(
                        "POST",
                        "/api/transfer",
                        request.toString(),
                        token
                );

                JSONObject result = new JSONObject(response);

                String reference = result.getString("referenceNumber");
                String senderName = result.optString("senderName", name);
                String recipientName = result.optString(
                        "recipientName", "Recipient"
                );

                String recipientMobile = result.getString(
                        "recipientMobileNumber"
                );

                double transferredAmount =
                        result.getDouble("amount");

                double newBalance =
                        result.getDouble("senderBalance");

                String date = result.getString("datetime");

                // The backend response does not provide the previous balance.
                // This value is only for displaying the receipt.
                double previousBalance =
                        newBalance + transferredAmount;

                runOnUiThread(() -> {

                    Intent intent = new Intent(
                            TransferActivity.this,
                            ReceiptActivity.class
                    );

                    intent.putExtra("reference", reference);
                    intent.putExtra("name", senderName);
                    intent.putExtra("accountNumber", mobileNumber);
                    intent.putExtra("recipientAccount", recipientMobile);
                    intent.putExtra("recipientName", recipientName);
                    intent.putExtra("amount", transferredAmount);
                    intent.putExtra("previousBalance", previousBalance);
                    intent.putExtra("newBalance", newBalance);
                    intent.putExtra("date", date);
                    intent.putExtra("type", "TRANSFER");

                    startActivity(intent);
                    finish();
                });

            } catch (Exception e) {

                runOnUiThread(() -> {

                    Toast.makeText(
                            TransferActivity.this,
                            "Transfer failed: Insufficient Balance or Recipient Not Found! ",
                            Toast.LENGTH_LONG
                    ).show();

                    transferButton.setEnabled(true);
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
