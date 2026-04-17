package ph.edu.usc24100050;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {

    private final CebuAIService aiService;
    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public ChatViewModel(Application application) {
        super(application);
        aiService = new CebuAIService(application);
    }

    public LiveData<List<Message>> getMessages() { return messages; }
    public LiveData<Boolean> getIsLoading()      { return isLoading; }

    public void sendMessage(String userInput) {
        List<Message> currentHistory = messages.getValue() != null
                ? new ArrayList<>(messages.getValue())
                : new ArrayList<>();

        // Show user message immediately
        currentHistory.add(new Message("user", userInput));
        messages.setValue(new ArrayList<>(currentHistory));
        isLoading.setValue(true);

        final List<Message> historySnapshot =
                new ArrayList<>(currentHistory.subList(0, currentHistory.size() - 1));

        aiService.chat(userInput, historySnapshot, new CebuAIService.AICallback() {
            @Override
            public void onSuccess(String response) {
                List<Message> updated = new ArrayList<>(
                        messages.getValue() != null ? messages.getValue() : new ArrayList<>()
                );
                updated.add(new Message("assistant", response));
                messages.setValue(updated);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                List<Message> updated = new ArrayList<>(
                        messages.getValue() != null ? messages.getValue() : new ArrayList<>()
                );
                updated.add(new Message("assistant",
                        "Sorry, I had trouble connecting. Please try again!"));
                messages.setValue(updated);
                isLoading.setValue(false);
            }
        });
    }

    public void generateItinerary(int days, List<String> interests) {
        isLoading.setValue(true);
        aiService.generateItinerary(days, interests, new CebuAIService.AICallback() {
            @Override
            public void onSuccess(String response) {
                List<Message> updated = new ArrayList<>(
                        messages.getValue() != null ? messages.getValue() : new ArrayList<>()
                );
                updated.add(new Message("assistant", response));
                messages.setValue(updated);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
            }
        });
    }

    public void recommendSpots(String category) {
        isLoading.setValue(true);
        aiService.recommendSpots(category, new CebuAIService.AICallback() {
            @Override
            public void onSuccess(String response) {
                List<Message> updated = new ArrayList<>(
                        messages.getValue() != null ? messages.getValue() : new ArrayList<>()
                );
                updated.add(new Message("assistant", response));
                messages.setValue(updated);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String errorMessage) {
                isLoading.setValue(false);
            }
        });
    }
}
