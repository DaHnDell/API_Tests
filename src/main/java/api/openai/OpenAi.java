package api.openai;

import java.io.IOException;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OpenAi {
  private static final String API_KEY = "sk-xxxx"; // yml 대신 테스트용 상수
  private static final String ENDPOINT = "https://api.openai.com/v1/chat/completions";

  public static void main(String[] args) throws IOException {
      OkHttpClient client = new OkHttpClient();
      JSONObject message = new JSONObject()
              .put("role", "user")
              .put("content", "Hello! Summarize OpenAI API usage.");

      JSONObject body = new JSONObject()
              .put("model", "gpt-4o-mini") // 최신 모델로
              .put("messages", new org.json.JSONArray().put(message));

      Request request = new Request.Builder()
              .url(ENDPOINT)
              .post(RequestBody.create(
                      body.toString(),
                      MediaType.parse("application/json")
              ))
              .addHeader("Authorization", "Bearer " + API_KEY)
              .build();

      try (Response response = client.newCall(request).execute()) {
          if (response.isSuccessful()) {
              System.out.println(response.body().string());
          } else {
              System.err.println("Error: " + response.code() + " - " + response.message());
          }
      }
  }
}
