package com.example.smishingdetectionapp.riskmeter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.compose.ui.platform.ComposeView;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import static com.example.smishingdetectionapp.riskmeter.PulseInjectorKt.injectPulsing;

public class RiskScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riskscanner);

        ComposeView pulseView = findViewById(R.id.pulseComposeView);
        injectPulsing(pulseView);

        TextView scanningText = findViewById(R.id.scanningText);

        scanningText.setVisibility(View.VISIBLE);
        pulseView.setVisibility(View.VISIBLE);


        boolean disableSmsRisk = getIntent().getBooleanExtra("DISABLE_SMS_RISK", false);
        boolean disableAgeRisk = getIntent().getBooleanExtra("DISABLE_AGE_RISK", false);
        boolean disableSecurityRisk = getIntent().getBooleanExtra("DISABLE_SECURITY_RISK", false);


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(RiskScannerActivity.this, RiskResultActivity.class);
            intent.putExtra("DISABLE_SMS_RISK", disableSmsRisk);
            intent.putExtra("DISABLE_AGE_RISK", disableAgeRisk);
            intent.putExtra("DISABLE_SECURITY_RISK", disableSecurityRisk);
            startActivity(intent);
            finish();
        }, 9000);

        BottomNavCoordinator.setup(this, R.id.nav_home);


        // back button
        ImageButton report_back = findViewById(R.id.RiskScanner_back);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, RiskScannerTCActivity.class));
            finish();
        });
    }
}
