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
        String sqlPrompt = createPrompt(userQuestion).trim();

        String chatResponse = chatModel.call(new Prompt(sqlPrompt)).getResult().getOutput().getText().trim();

        if(!chatResponse.startsWith("SELECT") && !chatResponse.startsWith("select")){
            return chatResponse;
        }

        // Step 2: Execute SQL query
        List<Map<String, Object>> results;
        try {
            results = jdbcTemplate.queryForList(chatResponse);
        } catch (Exception e) {
            return "There was an error while executing the SQL: " + e.getMessage();
        }

        // Step 3: Convert result to natural language
        return convertResultToHumanText(userQuestion, chatResponse, results);
    }

    private String createPrompt(String userInput) {
//        return "You are an expert postgres SQL generator. Given a user question and a database schema, generate a valid SQL query.\n\n" +
//                "Here is the Schema:\n" +
//                "Table: Employee\n" +
//                "- id: int\n" +
//                "- employee_id: text\n" +
//                "- name: text\n" +
//                "- project_name: text\n" +
//                "- dob: date\n" +
//                "- salary: text\n\n" +
//                "Question: " + userInput+ "\n\n"+
//                "Guidelines for SQL generation:\n" +
//                "First, check if the question is relevant to the datasource and can be answered using the available data.\n" +
//                "1. If yes, generate a valid SQL query using:\n" +
//                "- Perform case-insensitive comparisons using `ILIKE` instead of `=`, especially for string-based columns (e.g., names, titles).\n" +
//                "- Handle partial name matches using wildcards (`%`) where appropriate. For example, if the user asks about \"Mark\", generate:  \n" +
//                "   `WHERE name ILIKE '%Mark%'`\n" +
//                "- Prefer `LIMIT 1` when querying for a single record like salary, email, etc.\n" +
//                "- Use table and column names exactly as provided in the schema.\n" +
//                "- Avoid joins unless explicitly needed.\n" +
//                "- Do not hallucinate any column/table names—use only what's in the schema.\n" +
//                "- Important: Ensure the SQL is valid and can be directly executed on a PostgreSQL/MySQL database.\n" +
//                "- Only respond with a valid supported SQL string query without any additional explanation, markdown and other information.\n" +
//                "\n" +
//                "2. If the question is **general knowledge** (e.g., “What is 4 + 4?”, “What is the capital of Japan?”), answer it directly in plain English without using SQL.\n" +
////                "3. If the question is **small talk or not related to the schema or general knowledge** (e.g., “Hi”, “How are you?”, “Tell me a joke”), return a polite message like:\n" +
////                "   - “This doesn’t seem related to the database. Let me know if you'd like to ask about something specific.\n" +
//                "";

        return "\n" +
                "You are a helpful assistant that generates SQL queries and answers data-related questions using the given database schema.\n" +
                "If the question doesn't relate to the schema, reply politely or answer general knowledge queries in plain English.\n" +
                "\n" +
                "Here is the Database Schema:\n" +
                "---\n" +
                getSqlSchema() +
//                "Table: Employee\n" +
//                "- id: int\n" +
//                "- employee_id: text\n" +
//                "- name: text\n" +
//                "- project_name: text\n" +
//                "- dob: date\n" +
//                "- salary: text\n" +
                "---\n" +
                "Instructions:\n" +
                "1. If the question can be answered using the schema, return a valid SQL query string only:\n" +
                "Use ILIKE (PostgreSQL) or LOWER(column) LIKE LOWER(...) (MySQL) for case-insensitive string matches\n" +
                "Use % wildcards for partial name matches (e.g., '%Mark%')\n" +
                "Use LIMIT 1 for single-value lookups (e.g., salary, DOB)\n" +
                "Only use fields/tables from the schema — no assumptions or extra columns\n" +
                "Avoid joins unless explicitly required\n" +
                "Ensure SQL works for both PostgreSQL and MySQL\n" +
                //"If returning salary, show it in INDIAN currency" +
                "Return only the SQL query string — no markdown, comments, or extra text\n" +
                "2. If the question is general knowledge (e.g., \"What is 4 + 4?\", \"What is the capital of Japan?\") — answer it clearly in plain English\n" +
                "3. If the question is greeting, small talk, or unrelated (e.g., \"Hi\", \"Tell me a joke\") — answer it\n" +
//                "“Hi there! I'm here to help you with data from the Employee database. Feel free to ask any question about employees, salaries, or projects.”\n" +
//                "\n" +
                "Here is the User Question: "+userInput;

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
                        "- Only use data in the result."),
                new SystemMessage("Your job is to:\n" +
                        "1. Understand exactly what the question is asking for (e.g., name, count, date, etc.).\n" +
                        "2. If the result is empty or no matching data is found, reply politely in plain English.\n" +
                        "   Example: “I couldn't find any information. It's possible that the specified item doesn't exist or there is no data available.”\n" +
                        "3. If data exists, provide only the **relevant** information needed to answer the question.\n" +
                        "4. Do **not** include any extra details not directly asked for.\n" +
                        "5. Do **not** repeat column headers or format the output like a table.\n" +
                        "6. Summarize the result clearly in natural, conversational English.\n" +
                        "\n" +
                        "Return only the final answer text — no reasoning, no SQL, no internal notes.")
        ));

        return chatModel.call(explanationPrompt).getResult().getOutput().getText();
    }

    public String getSqlSchema(){
        return "CREATE TABLE employees (\n" +
                "    id UUID PRIMARY KEY,\n" +
                "    employee_id VARCHAR(50),\n" +
                "    name VARCHAR(100),\n" +
                "    project_name VARCHAR(100),\n" +
                "    dob DATE,\n" +
                "    doj DATE,\n" +
                "    salary DECIMAL(10, 2),\n" +
                "    designation VARCHAR(100),\n" +
                "    chapter VARCHAR(100),\n" +
                "    departement VARCHAR(100)\n" +
                "    bench_status boolean\n" +
                ");\n"+
                "CREATE TABLE projects (\n" +
                "    id UUID PRIMARY KEY,\n" +
                "    project_id VARCHAR(50) UNIQUE NOT NULL,\n" +
                "    project_name VARCHAR(150) NOT NULL,\n" +
                "    start_date DATE,\n" +
                "    end_date DATE\n" +
                "); \n"+
                "CREATE TABLE virtual_machines (\n" +
                "    id UUID PRIMARY KEY,\n" +
                "    machine_id VARCHAR(100) UNIQUE NOT NULL,\n" +
                "    machine_name VARCHAR(100) NOT NULL,\n" +
                "    configuration TEXT NOT NULL,\n" +
                "    is_active BOOLEAN\n" +
                ");";
    }

//    public String getChatResponse(String prompt){
//        Prompt chatPrompt = new Prompt(prompt);
//        return chatModel.call(chatPrompt).getResult().getOutput().getText();
//    }
}
