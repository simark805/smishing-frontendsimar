package com.example.smishingdetectionapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
import com.example.smishingdetectionapp.ui.FaqActivity;
import com.google.android.material.card.MaterialCardView;

public class HelpActivity extends SharedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_updated);

        BottomNavCoordinator.setup(this, R.id.bottom_navigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Back button
        ImageButton backBtn = findViewById(R.id.help_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> finish());
        }

        // Call Us
        MaterialCardView callUs = findViewById(R.id.cardCallUs);
        if (callUs != null) {
            callUs.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+1234567890"));
                startActivity(intent);
            });
        }

        // Mail Us
        MaterialCardView mailUs = findViewById(R.id.cardMailUs);
        if (mailUs != null) {
            mailUs.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:support@example.com"));
                startActivity(intent);
            });
        }

        // FAQ
        MaterialCardView faq = findViewById(R.id.cardFAQ);
        if (faq != null) {
            faq.setOnClickListener(v -> {
                startActivity(new Intent(this, FaqActivity.class));
            });
        }

        // Feedback
        MaterialCardView feedback = findViewById(R.id.cardFeedback);
        if (feedback != null) {
            feedback.setOnClickListener(v -> {
                startActivity(new Intent(this, FeedbackActivity.class));
            });
        }

        // Optional topic cards (safe if present)
        setupOptionalCard(R.id.cardTopic1, "How to detect a smishing message");
        setupOptionalCard(R.id.cardTopic2, "How to report a suspicious SMS");
        setupOptionalCard(R.id.cardTopic3, "What is smishing vs phishing?");
    }

    private void setupOptionalCard(int id, String message) {
        MaterialCardView card = findViewById(id);
        if (card != null) {
            card.setOnClickListener(v ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            );
        }
    }
}