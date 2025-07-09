package ai.query.q_ai.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatModel chatModel;
    private final JdbcTemplate jdbcTemplate;

    public ChatService(ChatModel chatModel, JdbcTemplate jdbcTemplate) {

        this.chatModel = chatModel;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getHumanReadableResponse(String userQuestion) {
        // Step 1: Generate SQL query
        String sqlPrompt = createPrompt(userQuestion);
        String sqlQuery = chatModel.call(new Prompt(sqlPrompt)).getResult().getOutput().getText().trim();

        // Step 2: Execute SQL query
        List<Map<String, Object>> results;
        try {
            results = jdbcTemplate.queryForList(sqlQuery);
        } catch (Exception e) {
            return "There was an error while executing the SQL: " + e.getMessage();
        }

        // Step 3: Convert result to natural language
        return convertResultToHumanText(userQuestion, sqlQuery, results);
    }

    private String createPrompt(String userInput) {
        return "You are an expert postgres SQL generator. Given a user question and a database schema, generate a valid SQL query.\n\n" +
                "Here is the Schema:\n" +
                "Table: Employee\n" +
                "- id: int\n" +
                "- employee_id: text\n" +
                "- name: text\n" +
                "- project_name: text\n" +
                "- dob: date\n" +
                "- salary: text\n\n" +
                "Only respond with a valid supported SQL string query without any additional explanation, markdown and other information.\n" +
                "Question: " + userInput+ "\n\n"+
                "Guidelines for SQL generation:\n" +
                "1. Perform case-insensitive comparisons using `ILIKE` instead of `=`, especially for string-based columns (e.g., names, titles).\n" +
                "2. Handle partial name matches using wildcards (`%`) where appropriate. For example, if the user asks about \"Mark\", generate:  \n" +
                "   `WHERE name ILIKE '%Mark%'`\n" +
                "3. Prefer `LIMIT 1` when querying for a single record like salary, email, etc.\n" +
                "4. Use table and column names exactly as provided in the schema.\n" +
                "5. Avoid joins unless explicitly needed.\n" +
                "6. Do not hallucinate any column/table names—use only what's in the schema.\n" +
                "7. Return only the SQL query. Do not explain or comment.\n" +
                "\n" +
                "Important: Ensure the SQL is valid and can be directly executed on a PostgreSQL/MySQL database.";

    }

    private String convertResultToHumanText(String userQuestion, String sqlQuery, List<Map<String, Object>> result) {
        // Convert result to string (you could format it as JSON, CSV, etc.)
        StringBuilder resultBuilder = new StringBuilder();
        for (Map<String, Object> row : result) {
            resultBuilder.append(row.toString()).append("\n");
        }

        // Prompt ChatModel to summarize
        Prompt explanationPrompt = new Prompt(List.of(
                new SystemMessage("You are a helpful assistant that explains database query results in natural language."),
                new UserMessage("User question: " + userQuestion + "\n\nSQL Query: " + sqlQuery + "\n\nQuery Result:\n" + resultBuilder),
                new SystemMessage("Write a concise and accurate answer based only on the result above.\n" +
                        "- Do not include SQL.\n" +
                        "- Do not make assumptions.\n" +
                        "- Only use data in the result.")
        ));

        return chatModel.call(explanationPrompt).getResult().getOutput().getText();
    }

//    public String getChatResponse(String prompt){
//        Prompt chatPrompt = new Prompt(prompt);
//        return chatModel.call(chatPrompt).getResult().getOutput().getText();
//    }
}
