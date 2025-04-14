package api.healthfunctionproductinfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class HealthProductInfo {
    public static void main(String[] args) throws IOException {
        // API 요청 변수 설정
        String keyId = ""; // API 인증키
        String serviceId = "I2710"; // 서비스 ID
        String dataType = "xml"; // 응답 데이터 형식
        String startIdx = "1"; // 요청 시작 위치
        String endIdx = "10"; // 요청 종료 위치

        // 추가 요청 인자
        String productName = ""; // 품목명 (예시)

        // 품목명 URL 인코딩
        String encodedProductName = URLEncoder.encode(productName, "UTF-8");

        // URL 구성
        StringBuilder urlBuilder = new StringBuilder("http://openapi.foodsafetykorea.go.kr/api/");
        urlBuilder.append(keyId).append("/").append(serviceId).append("/").append(dataType).append("/")
                  .append(startIdx).append("/").append(endIdx);
        urlBuilder.append("?PRDCT_NM=").append(encodedProductName); // 쿼리 파라미터 추가

        // URL 객체 생성
        URL url = new URL(urlBuilder.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // 응답 코드 확인
        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);

        if (responseCode == 200) {
            // 응답 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // 응답 데이터 출력 (디버깅)
            System.out.println("응답 데이터 원본: \n" + response.toString());

            // XML 파싱 및 데이터 출력
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new InputSource(new StringReader(response.toString())));
                document.getDocumentElement().normalize();

                // 품목 정보 출력
                NodeList rowList = document.getElementsByTagName("row");
                if (rowList.getLength() == 0) {
                    System.out.println("검색된 데이터가 없습니다.");
                } else {
                    for (int i = 0; i < rowList.getLength(); i++) {
                        Node rowNode = rowList.item(i);
                        if (rowNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element rowElement = (Element) rowNode;

                            System.out.println("========================================================");
                            printTagValue("품목명", "PRDCT_NM", rowElement);
                            printTagValue("섭취시 주의사항", "IFTKN_ATNT_MATR_CN", rowElement);
                            printTagValue("주된 기능성", "PRIMARY_FNCLTY", rowElement);
                            printTagValue("일일 섭취량 (하한)", "DAY_INTK_LOWLIMIT", rowElement);
                            printTagValue("일일 섭취량 (상한)", "DAY_INTK_HIGHLIMIT", rowElement);
                            System.out.println("========================================================");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("XML 파싱 중 오류 발생: " + e.getMessage());
            }
        } else {
            System.out.println("API 호출 실패: " + responseCode);
        }

        // 연결 종료
        connection.disconnect();
    }

    // XML 태그에서 값 추출하는 헬퍼 메소드
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag);
        if (nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) {
            return nodeList.item(0).getTextContent();
        }
        return "데이터 없음";
    }

    // 태그 출력 헬퍼 메소드
    private static void printTagValue(String label, String tag, Element element) {
        System.out.println(label + ": " + getTagValue(tag, element));
    }
}
