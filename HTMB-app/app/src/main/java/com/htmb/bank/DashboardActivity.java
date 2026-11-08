
package com.htmb.bank;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.htmb.bank.api.ApiClient;

import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardActivity extends AppCompatActivity {

    private TextView welcomeText;
    private TextView accountNumberText;
    private TextView balanceText;

    private Button cashinButton;
    private Button cashoutButton;
    private Button transferButton;
    private Button transactionHistoryButton;
    private Button logoutButton;

    private SharedPreferences preferences;
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();

    private String name;
    private String mobileNumber;
    private double balance;

    private SwipeRefreshLayout dashboardSwipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        dashboardSwipeRefresh =
                findViewById(R.id.dashboardSwipeRefresh);

        dashboardSwipeRefresh.setOnRefreshListener(this::fetchDashboard);

        preferences = getSharedPreferences("HTMB_SESSION", MODE_PRIVATE);

        welcomeText = findViewById(R.id.welcomeText);
        accountNumberText = findViewById(R.id.accountNumberText);
        balanceText = findViewById(R.id.balanceText);

        cashinButton = findViewById(R.id.cashinButton);
        cashoutButton = findViewById(R.id.cashoutButton);
        transferButton = findViewById(R.id.transferButton);
        transactionHistoryButton =
                findViewById(R.id.transactionHistoryButton);
        logoutButton = findViewById(R.id.logoutButton);

        name = preferences.getString("name", "HTMB User");
        mobileNumber = preferences.getString("mobileNumber", "");

        welcomeText.setText("Welcome, " + name + "!");
        accountNumberText.setText("Mobile Number: " + mobileNumber);



        cashinButton.setOnClickListener(v -> openActivity(CashInActivity.class));
        cashoutButton.setOnClickListener(v -> openActivity(CashOutActivity.class));
        transferButton.setOnClickListener(v -> openActivity(TransferActivity.class));

        transactionHistoryButton.setOnClickListener(v ->
                openActivity(TransactionHistoryActivity.class));

        logoutButton.setOnClickListener(v -> logout());
    }

    private void fetchDashboard() {

        String token = preferences.getString("token", null);

        if (token == null || token.isEmpty()) {
            logout();
            return;
        }

        executor.execute(() -> {
            try {
                String response = ApiClient.request(
                        "POST",
                        "/api/dashboard",
                        "{}",
                        token
                );

                JSONObject result = new JSONObject(response);

                String newName = result.optString("name");
                String newMobile = result.optString("mobileNumber");
                double newBalance = result.getDouble("balance");

                runOnUiThread(() -> {

                    name = newName;
                    mobileNumber = newMobile;
                    balance = newBalance;

                    welcomeText.setText("Welcome, " + name + "!");
                    accountNumberText.setText(
                            "Mobile Number: " + mobileNumber
                    );
                    balanceText.setText(formatMoney(balance));

                    preferences.edit()
                            .putString("name", name)
                            .putString("mobileNumber", mobileNumber)
                            .apply();

                    dashboardSwipeRefresh.setRefreshing(false);
                });

            }  catch (Exception e) {
            runOnUiThread(() -> {
                dashboardSwipeRefresh.setRefreshing(false);

                showMessage("Unable to load dashboard: "
                        + e.getMessage());
            });
        }
        });
    }

    private void openActivity(Class<?> activityClass) {

        Intent intent = new Intent(this, activityClass);
        intent.putExtra("name", name);
        intent.putExtra("mobileNumber", mobileNumber);

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (preferences != null && welcomeText != null) {
            fetchDashboard();
        }
    }

    private String formatMoney(double amount) {
        return String.format(Locale.US, "₱%,.2f", amount);
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void logout() {

        preferences.edit().clear().apply();

        Intent intent = new Intent(
                DashboardActivity.this,
                MainActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
