package api.notion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

public class Notion {
  private static final String NOTION_API_VERSION = "2022-06-28";
  private static final String TOKEN = "토큰"; // integration token
  private static final String DATABASE_ID = "DB 아이디"; // database id

  public static void main(String[] args) {
    try {
      String url = "https://api.notion.com/v1/databases/" + DATABASE_ID + "/query";
      HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();

      // 요청 설정
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Authorization", "Bearer " + TOKEN);
      conn.setRequestProperty("Notion-Version", NOTION_API_VERSION);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setDoOutput(true);

      // 바디(JSON) 작성 (필터 없이 전체 조회)
      String jsonInput = "{}";
      try (OutputStream os = conn.getOutputStream()) {
        byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
        os.write(input, 0, input.length);
      }

      // 응답 읽기
      int responseCode = conn.getResponseCode();
      InputStream responseStream = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

      BufferedReader in = new BufferedReader(new InputStreamReader(responseStream));
      String line;
      StringBuilder response = new StringBuilder();
      while ((line = in.readLine()) != null) {
        response.append(line);
      }
      in.close();
      JSONObject json = new JSONObject(response.toString());
      JSONArray results = json.getJSONArray("results");

      for (int i = 0; i < results.length(); i++) {
        JSONObject page = results.getJSONObject(i);
        JSONObject properties = page.getJSONObject("properties");
        JSONObject titleProperty = properties.getJSONObject("이름");
        JSONArray titleArray = titleProperty.getJSONArray("title");

        if (titleArray.length() > 0) {
          String title = titleArray.getJSONObject(0)
              .getJSONObject("text").getString("content");
          System.out.println("제목: " + title);
        }
      }

      System.out.println("응답 코드: " + responseCode);
      System.out.println("응답 내용: " + response.toString());

    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
