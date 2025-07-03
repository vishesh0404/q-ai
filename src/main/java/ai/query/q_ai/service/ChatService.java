package ai.query.q_ai.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatModel chatModel;

    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String getChatResponse(String prompt){
        Prompt chatPrompt = new Prompt(prompt);
        return chatModel.call(chatPrompt).getResult().getOutput().getText();
    }
}
