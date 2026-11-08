
package com.htmb.bank;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.htmb.bank.api.ApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class TransactionHistoryActivity extends AppCompatActivity {

    private LinearLayout transactionList;
    private TextView emptyMessage;

    private SwipeRefreshLayout historySwipeRefresh;

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density + 0.5f);
    }

    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        SwipeRefreshLayout swipeRefresh =
                findViewById(R.id.historySwipeRefresh);

        ScrollView scrollView =
                findViewById(R.id.transactionScrollView);

        swipeRefresh.setOnChildScrollUpCallback((parent, child) ->
                scrollView.canScrollVertically(-1)
        );

        historySwipeRefresh = findViewById(R.id.historySwipeRefresh);
        historySwipeRefresh.setOnRefreshListener(this::loadTransactions);

        transactionList = findViewById(R.id.transactionList);
        emptyMessage = findViewById(R.id.emptyMessage);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        loadTransactions();
    }

    private void loadTransactions() {

        transactionList.removeAllViews();
        emptyMessage.setText("Loading transactions...");
        emptyMessage.setVisibility(View.VISIBLE);

        String token = getSharedPreferences(
                "HTMB_SESSION", MODE_PRIVATE
        ).getString("token", null);

        if (token == null || token.isEmpty()) {
            emptyMessage.setText("Please log in again.");
            historySwipeRefresh.setRefreshing(false);
            return;
        }

        executor.execute(() -> {
            try {
                String response = ApiClient.request(
                        "POST",
                        "/api/transaction-history",
                        "{}",
                        token
                );

                JSONObject json = new JSONObject(response);
                JSONArray transactions =
                        json.getJSONArray("transactions");

                runOnUiThread(() -> {
                    displayTransactions(transactions);
                    historySwipeRefresh.setRefreshing(false);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    historySwipeRefresh.setRefreshing(false);

                    emptyMessage.setText(
                            "Unable to load transaction history."
                    );

                    Toast.makeText(
                            this,
                            "Please check your connection.",
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    private void displayTransactions(JSONArray transactions) {

        transactionList.removeAllViews();

        if (transactions.length() == 0) {
            emptyMessage.setText("No transactions yet.");
            emptyMessage.setVisibility(View.VISIBLE);
            return;
        }

        emptyMessage.setVisibility(View.GONE);

        for (int i = 0; i < transactions.length(); i++) {

            JSONObject transaction =
                    transactions.optJSONObject(i);

            if (transaction == null) {
                continue;
            }

            String type = transaction.optString("type", "TRANSACTION");
            String reference = transaction.optString(
                    "referenceNumber", "N/A"
            );
            String date = formatDate(transaction.optString("datetime", "N/A"));
            String details = transaction.optString("details", "");

            double amount = transaction.optDouble("amount", 0.0);

            String title;
            String sign;

            switch (type) {
                case "CASHIN":
                    title = "CASH-IN";
                    sign = "+";
                    break;

                case "CASHOUT":
                    title = "CASH-OUT";
                    sign = "-";
                    break;

                case "TRANSFER SENT":
                    title = "Transfer Sent";
                    sign = "-";
                    break;

                case "TRANSFER RECEIVED":
                    title = "Transfer Received";
                    sign = "+";
                    break;

                default:
                    title = type;
                    sign = "";
                    break;
            }

            String formattedAmount = String.format(
                    Locale.getDefault(),
                    "₱%,.2f",
                    amount
            );

            // Main transaction card
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(16), dp(16), dp(16), dp(16));

// Rounded white background with subtle border
            GradientDrawable background = new GradientDrawable();
            background.setColor(Color.WHITE);
            background.setCornerRadius(dp(14));
            background.setStroke(dp(1), Color.rgb(230, 234, 240));

            card.setBackground(background);

// Transaction title and amount row
            LinearLayout topRow = new LinearLayout(this);
            topRow.setOrientation(LinearLayout.HORIZONTAL);
            topRow.setGravity(Gravity.CENTER_VERTICAL);

// Transaction title
            TextView titleText = new TextView(this);
            titleText.setText(title);
            titleText.setTextColor(Color.rgb(35, 45, 60));
            titleText.setTextSize(16);
            titleText.setTypeface(null, Typeface.BOLD);

// Amount
            TextView amountText = new TextView(this);
            amountText.setText(sign + formattedAmount);

            int amountColor;

            if (sign.equals("+")) {
                amountColor = Color.rgb(22, 128, 74);
            } else if (sign.equals("-")) {
                amountColor = Color.rgb(210, 65, 65);
            } else {
                amountColor = Color.rgb(35, 45, 60);
            }

            amountText.setTextColor(amountColor);
            amountText.setTextSize(16);
            amountText.setTypeface(null, Typeface.BOLD);
            amountText.setGravity(Gravity.END);

            LinearLayout.LayoutParams titleParams =
                    new LinearLayout.LayoutParams(
                            0,
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            1
                    );

            topRow.addView(titleText, titleParams);

            topRow.addView(amountText);

            card.addView(topRow);

// Reference number
            TextView referenceText = new TextView(this);
            referenceText.setText("Reference: " + reference);
            referenceText.setTextColor(Color.rgb(110, 120, 135));
            referenceText.setTextSize(13);
            referenceText.setMaxLines(2);
            referenceText.setPadding(0, dp(12), 0, 0);

            card.addView(referenceText);

// Transaction details
            if (!details.isEmpty()) {
                TextView detailsText = new TextView(this);
                detailsText.setText("Details: " + details);
                detailsText.setTextColor(Color.rgb(90, 100, 115));
                detailsText.setTextSize(14);
                detailsText.setPadding(0, dp(8), 0, 0);

                card.addView(detailsText);
            }

// Transaction date
            TextView dateText = new TextView(this);
            dateText.setText(date);
            dateText.setTextColor(Color.rgb(130, 140, 155));
            dateText.setTextSize(12);
            dateText.setPadding(0, dp(10), 0, 0);

            card.addView(dateText);

// Card spacing
            LinearLayout.LayoutParams cardParams =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

            cardParams.setMargins(0, 0, 0, dp(12));

            card.setLayoutParams(cardParams);

            transactionList.addView(card);
        }

    }
    private String formatDate(String date) {
        if (date == null || date.equals("N/A")) {
            return "N/A";
        }

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

            return outputFormat.format(parsedDate);

        } catch (ParseException e) {
            return date;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
