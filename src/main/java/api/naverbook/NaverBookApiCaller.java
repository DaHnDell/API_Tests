package api.naverbook;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NaverBookApiCaller {

  private final String clientID = "YOUR_NAVER_CLIENT_ID";
  private final String secret = "YOUR_NAVER_CLIENT_SECRET";
  private static int counter;

  public List<Book> call(String query) {
    List<Book> list = new ArrayList<>();
    int respCode;
    InputStream is = null;

    try {
      query = URLEncoder.encode(query, "UTF-8");
      String apiUrl = "https://openapi.naver.com/v1/search/book.json?query=" + query + "&display=100";

      HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("X-Naver-Client-Id", clientID);
      conn.setRequestProperty("X-Naver-Client-Secret", secret);
      respCode = conn.getResponseCode();

      if (respCode == HttpURLConnection.HTTP_OK) {
        is = conn.getInputStream();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
          String line;
          while ((line = br.readLine()) != null) {
            sb.append(line);
          }
          list = parseJsonToBookList(sb.toString());
        }
      } else {
        System.err.println("API 요청 실패: 응답 코드 " + respCode);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return list;
  }

  private List<Book> parseJsonToBookList(String json) {
    List<Book> list = new ArrayList<>();
    JSONObject jObject = new JSONObject(json);
    JSONArray jArray = jObject.getJSONArray("items");

    for (int i = 0; i < jArray.length(); i++) {
      JSONObject item = jArray.getJSONObject(i);
      Book book = new Book(
          String.format("%04d", ++counter),
          item.optString("title"),
          item.optString("author"),
          item.optString("publisher"),
          item.optString("isbn"),
          item.optString("description"),
          item.optInt("discount", 0),
          1000,
          false,
          false);
      list.add(book);
    }

    return list;
  }

  public static void main(String[] args) {
    NaverBookApiCaller caller = new NaverBookApiCaller();
    String[] queries = { "전래동화", "서양고전" };
    List<Book> results = new ArrayList<>();

    Arrays.stream(queries).forEach(q -> results.addAll(caller.call(q)));
    results.forEach(System.out::println);
  }
}
