package ph.edu.usc24100050;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ChatActivity extends AppCompatActivity {

    private ChatViewModel viewModel;
    private MessageAdapter adapter;
    private RecyclerView recyclerView;
    private EditText inputField;
    private Button sendButton;
    private ProgressBar progressBar;

    private final String[] suggestions = {
            "Best lechon spots", "Kawasan Falls tips",
            "3-day itinerary", "Beaches near Cebu City"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        recyclerView  = findViewById(R.id.recyclerView);
        inputField    = findViewById(R.id.inputField);
        sendButton    = findViewById(R.id.sendButton);
        progressBar   = findViewById(R.id.progressBar);

        // Set up RecyclerView
        adapter = new MessageAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Set up suggestion chips
        setupChips();

        // Observe messages
        viewModel.getMessages().observe(this, messages -> {
            adapter.setMessages(messages);
            if (!messages.isEmpty()) {
                recyclerView.smoothScrollToPosition(messages.size() - 1);
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, loading -> {
            progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            sendButton.setEnabled(!loading);
        });

        // Send button
        sendButton.setOnClickListener(v -> sendMessage());

        // Send on keyboard action
        inputField.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void sendMessage() {
        String text = inputField.getText().toString().trim();
        if (!text.isEmpty()) {
            viewModel.sendMessage(text);
            inputField.setText("");
        }
    }

    private void setupChips() {
        LinearLayout chipContainer = findViewById(R.id.chipContainer);
        for (String suggestion : suggestions) {
            com.google.android.material.chip.Chip chip =
                    new com.google.android.material.chip.Chip(this);
            chip.setText(suggestion);
            chip.setOnClickListener(v -> viewModel.sendMessage(suggestion));
            chipContainer.addView(chip);
        }
    }
}
