
package com.example.excessbaggage;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final double ECONOMY_LIMIT = 20.0;
    private static final double BUSINESS_LIMIT = 40.0;
    private static final double TAX_RATE = 0.08;

    private EditText etWeight, etRate;
    private RadioGroup rgClass;
    private Button btnCalculate;

    // Result fields
    private TextView tvExcessWeight, tvCharge, tvTax, tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Input fields
        etWeight = findViewById(R.id.etWeight);
        etRate = findViewById(R.id.etRate);

        // Ticket class
        rgClass = findViewById(R.id.rgClass);

        // Calculate button
        btnCalculate = findViewById(R.id.btnCalculate);

        // Result fields
        tvExcessWeight = findViewById(R.id.tvExcessWeight);
        tvCharge = findViewById(R.id.tvCharge);
        tvTax = findViewById(R.id.tvTax);
        tvTotal = findViewById(R.id.tvTotal);

        // About and Share buttons
        Button btnAbout = findViewById(R.id.btnAbout);
        Button btnShare = findViewById(R.id.btnShare);

        btnAbout.setOnClickListener(v -> openAboutActivity());
        btnShare.setOnClickListener(v -> shareApp());

        btnCalculate.setOnClickListener(v -> calculateExcessBaggage());
    }

    private void calculateExcessBaggage() {

        String weightText = etWeight.getText().toString().trim();
        String rateText = etRate.getText().toString().trim();

        // Validate empty input
        if (weightText.isEmpty()) {
            etWeight.setError("Enter baggage weight");
            return;
        }

        if (rateText.isEmpty()) {
            etRate.setError("Enter rate");
            return;
        }

        // Validate ticket class
        if (rgClass.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this,
                    "Please select ticket class",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double weight;
        double rate;

        // Convert input to numbers
        try {
            weight = Double.parseDouble(weightText);
            rate = Double.parseDouble(rateText);

        } catch (NumberFormatException e) {
            Toast.makeText(this,
                    "Please enter valid numerical values",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate negative values
        if (weight < 0 || rate < 0) {
            Toast.makeText(this,
                    "Values cannot be negative",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Determine baggage allowance
        double limit;

        if (rgClass.getCheckedRadioButtonId() == R.id.rbEconomy) {
            limit = ECONOMY_LIMIT;
        } else {
            limit = BUSINESS_LIMIT;
        }

        // Calculate excess baggage
        double excessWeight = Math.max(0, weight - limit);

        // Calculate excess baggage charge
        double charge = excessWeight * rate;

        // Calculate 8% service tax
        double tax = charge * TAX_RATE;

        // Calculate total payment
        double total = charge + tax;

        // Display results
        tvExcessWeight.setText(String.format(Locale.US,
                "Excess Weight: %.2f kg", excessWeight));

        tvCharge.setText(String.format(Locale.US,
                "Excess Charge: RM %.2f", charge));

        tvTax.setText(String.format(Locale.US,
                "Service Tax (8%%): RM %.2f", tax));

        tvTotal.setText(String.format(Locale.US,
                "Total Payable: RM %.2f", total));
    }

    // Open About page
    private void openAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    // Share application and copy text to clipboard
    private void shareApp() {

        String shareText = "Try my Excess Baggage Fee Calculator App!";

        // Copy text to Android Clipboard
        ClipboardManager clipboard =
                (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText(
                    "Excess Baggage App Link", shareText);

            clipboard.setPrimaryClip(clip);

            Toast.makeText(this,
                    "Link copied to clipboard!",
                    Toast.LENGTH_SHORT).show();
        }

        // Launch Share Intent Chooser
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);

        startActivity(Intent.createChooser(shareIntent, "Share using"));
    }

    // Display toolbar menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handle toolbar menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_home) {
            return true;

        } else if (id == R.id.menu_about) {
            openAboutActivity();
            return true;

        } else if (id == R.id.menu_share) {
            shareApp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
