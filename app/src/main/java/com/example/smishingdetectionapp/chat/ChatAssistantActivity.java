package com.example.smishingdetectionapp.chat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.PopupMenu;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.chat.db.ChatDatabase;
import com.example.smishingdetectionapp.chat.db.ChatMessageEntity;

import java.util.HashMap;
import java.util.Map;

public class ChatAssistantActivity extends AppCompatActivity {

    private EditText userInput;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;
    private ProgressBar progressBar;

    private ChatAdapter chatAdapter;
    private OllamaClient ollamaClient;

    private int supportPromptCount = 0;
    private static final int MAX_SUPPORT_PROMPTS = 4;

    private final Map<String, String> supportPrompts = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_assistant);

        // UI
        userInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        ImageButton backButton = findViewById(R.id.btnBack);
        ImageButton menuButton = findViewById(R.id.menuButton);

        // Recycler
        chatAdapter = new ChatAdapter(this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        seedWelcomeIfEmpty();

        // Init LLM
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        ollamaClient = new OllamaClient(databaseAccess);

        // Buttons
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                hideKeyboard();
                finish();
            });
        }

        if (menuButton != null) {
            menuButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(this, v);
                popup.getMenuInflater().inflate(R.menu.chat_assistant_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == R.id.action_view_history) {
                        startActivity(new Intent(this, ChatHistoryActivity.class));
                        return true;
                    } else if (item.getItemId() == R.id.action_feedback) {
                        openSupportFeedback();
                        return true;
                    }
                    return false;
                });

                popup.show();
            });
        }

        sendButton.setOnClickListener(v -> sendMessage());

        initSupportPrompts();
    }

    private void initSupportPrompts() {
        supportPrompts.put("hi", "Hi! I’m your Smishing Assistant. How can I help?");
        supportPrompts.put("hello", "Hello! I can help detect smishing messages.");
        supportPrompts.put("help", "Ask me about smishing or how to use the app.");
        supportPrompts.put("what is smishing", "Smishing is phishing via SMS messages.");
        supportPrompts.put("report", "Go to Community Report to submit suspicious SMS.");
        supportPrompts.put("is this a scam", "Let me check that for you...");
        supportPrompts.put("bye", "Goodbye! Stay safe.");
    }

    private void seedWelcomeIfEmpty() {
        if (chatAdapter.getItemCount() == 0) {
            respondToUser(getString(R.string.chat_welcome_1));
            respondToUser(getString(R.string.chat_welcome_2));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void sendMessage() {
        String message = userInput.getText().toString().trim();
        if (message.isEmpty()) return;

        hideKeyboard();

        chatAdapter.addMessage(new ChatMessage(message, ChatMessage.USER));
        saveMessageToDb("user", message);

        chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        userInput.setText("");
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);

        String normalized = message.toLowerCase().replaceAll("[^a-z0-9\\s]", "").trim();

        Log.d("ChatDebug", "User: " + normalized);

        String botReply = findBestMatch(normalized);

        if (botReply != null) {
            handleSupportReply(botReply, normalized, message);
        } else {
            callLlm(message);
        }
    }

    private String findBestMatch(String input) {
        // Exact match
        if (supportPrompts.containsKey(input)) {
            return supportPrompts.get(input);
        }

        // Keyword match (word boundary safe)
        for (String key : supportPrompts.keySet()) {
            if (input.matches(".*\\b" + key + "\\b.*")) {
                return supportPrompts.get(key);
            }
        }

        return null;
    }

    private void handleSupportReply(String reply, String normalized, String originalMsg) {
        supportPromptCount++;

        runOnUiThread(() -> {
            respondToUser(reply);

            if ("is this a scam".equals(normalized)) {
                new Handler(Looper.getMainLooper()).postDelayed(() ->
                        respondToUser("This looks like a scam. Avoid clicking links."), 2000);
            }

            if (supportPromptCount >= MAX_SUPPORT_PROMPTS) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    respondToUser("Escalating to AI assistant...");
                    callLlm(originalMsg);
                }, 1200);
            } else {
                resetUi();
            }
        });
    }

    private void callLlm(String message) {
        supportPromptCount = 0;

        ollamaClient.getResponse(message, response ->
                runOnUiThread(() -> respondToUser(response))
        );
    }

    private void respondToUser(String response) {
        progressBar.setVisibility(View.GONE);
        sendButton.setEnabled(true);

        chatAdapter.addMessage(new ChatMessage(response, ChatMessage.BOT));
        saveMessageToDb("bot", response);

        chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    private void resetUi() {
        progressBar.setVisibility(View.GONE);
        sendButton.setEnabled(true);
    }

    private void saveMessageToDb(String sender, String text) {
        new Thread(() -> {
            try {
                ChatDatabase db = ChatDatabase.getInstance(getApplicationContext());
                db.chatMessageDao().insert(
                        new ChatMessageEntity(sender, text, System.currentTimeMillis())
                );
            } catch (Exception e) {
                Log.e("ChatDB", "Save failed", e);
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception ignored) {}
    }

    private void openSupportFeedback() {
        startActivity(new Intent(this,
                com.example.smishingdetectionapp.ui.SupportFeedbackActivity.class));
    }
}