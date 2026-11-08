
package com.htmb.bank;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        TextView receiptTitle = findViewById(R.id.receiptTitle);
        TextView receiptStatus = findViewById(R.id.receiptStatus);
        TextView referenceNumberText = findViewById(R.id.referenceNumberText);
        TextView amountLabel = findViewById(R.id.amountLabel);
        TextView amountText = findViewById(R.id.amountText);
        TextView accountNameText = findViewById(R.id.accountNameText);
        TextView accountNumberText = findViewById(R.id.accountNumberText);
        TextView recipientAccountText = findViewById(R.id.recipientAccountText);
        TextView previousBalanceText = findViewById(R.id.previousBalanceText);
        TextView newBalanceText = findViewById(R.id.newBalanceText);
        TextView dateText = findViewById(R.id.dateText);
        Button doneButton = findViewById(R.id.doneButton);

        String type = getIntent().getStringExtra("type");
        String reference = getIntent().getStringExtra("reference");
        String name = getIntent().getStringExtra("name");
        String accountNumber = getIntent().getStringExtra("accountNumber");
        String recipientAccount = getIntent().getStringExtra("recipientAccount");
        String recipientName = getIntent().getStringExtra("recipientName");
        String date = getIntent().getStringExtra("date");

        if (type == null) {
            type = "DEPOSIT";
        }

        double amount = getIntent().getDoubleExtra("amount", 0);
        double previousBalance = getIntent().getDoubleExtra("previousBalance", 0);
        double newBalance = getIntent().getDoubleExtra("newBalance", 0);

        receiptStatus.setText("SUCCESSFUL");

        switch (type) {

            case "CASHOUT":
                receiptTitle.setText("Cash-Out Receipt");
                amountLabel.setText("Amount Cashed-Out");
                break;

            case "TRANSFER":
                receiptTitle.setText("Transfer Receipt");
                amountLabel.setText("Amount Transferred");

                String recipientDetails = "Recipient Account: "
                        + (recipientAccount != null
                        ? recipientAccount : "Not available");

                if (recipientName != null && !recipientName.isEmpty()) {
                    recipientDetails += "\nRecipient Name: " + recipientName;
                }

                recipientAccountText.setText(recipientDetails);
                recipientAccountText.setVisibility(View.VISIBLE);
                break;

            default:
                receiptTitle.setText("Cash-In Receipt");
                amountLabel.setText("Amount Cashed-In");
                break;
        }

        referenceNumberText.setText(
                reference != null ? reference : "Not available"
        );

        amountText.setText(
                String.format(Locale.US, "₱%,.2f", amount)
        );

        accountNameText.setText(
                "Account Name: " + (name != null ? name : "HTMB User")
        );

        accountNumberText.setText(
                "Your Number: " +
                        (accountNumber != null ? accountNumber : "Not assigned")
        );

        previousBalanceText.setText(String.format(
                Locale.US, "Previous Balance: ₱%,.2f", previousBalance
        ));

        newBalanceText.setText(String.format(
                Locale.US, "New Balance: ₱%,.2f", newBalance
        ));

        if (date == null) {
            date = new SimpleDateFormat(
                    "MMMM dd, yyyy hh:mm a",
                    Locale.getDefault()
            ).format(new Date());
        } else {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        Locale.getDefault()
                );

                Date parsedDate = inputFormat.parse(date);

                SimpleDateFormat outputFormat = new SimpleDateFormat(
                        "MMMM dd, yyyy hh:mm a",
                        Locale.getDefault()
                );

                date = outputFormat.format(parsedDate);

            } catch (Exception e) {
                // Keep the original date if parsing fails.
            }
        }

        dateText.setText("Date and Time: " + date);

        dateText.setText("Date and Time: " + date);

        doneButton.setOnClickListener(v -> finish());
    }
}
