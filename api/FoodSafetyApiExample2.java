package api;
/* Java 1.8 샘플 코드 */

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;

public class FoodSafetyApiExample2 {
    public static void main(String[] args) throws IOException {
        // API 요청 변수 설정
        String keyId = "286a3e3b38cf4050b678"; // 인증키
        String serviceId = "C003"; // 서비스명 (예: C003)
        String dataType = "json"; // 응답 데이터 타입 (json 또는 xml)
        String startIdx = "1"; // 요청 시작 위치
        String endIdx = "10"; // 요청 종료 위치

        // URL 빌더 생성
        StringBuilder urlBuilder = new StringBuilder("http://openapi.foodsafetykorea.go.kr/api/");
        urlBuilder.append(keyId + "/" + serviceId + "/" + dataType + "/" + startIdx + "/" + endIdx); // 기본 URL과 인자 추가

        // 품목명 인코딩
        urlBuilder.append("&" + URLEncoder.encode("PRDLST_NM", "UTF-8") + "=" + URLEncoder.encode("홍삼", "UTF-8")); // 예시: 품목명 (홍삼)

        // URL 객체 생성
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        // 응답 코드 확인
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
        }

        // 응답 읽기
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        // 응답 출력
        System.out.println(sb.toString());
    }
}
