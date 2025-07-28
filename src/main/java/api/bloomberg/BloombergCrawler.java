package api.bloomberg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class BloombergCrawler {
  public static void main(String[] args) throws Exception {
    // 1. 메인 페이지 접속
    Document doc = Jsoup.connect("https://www.bloomberg.com/")
            .userAgent("Mozilla/5.0")
            .get();

    // 2. 주요 기사 타이틀 추출 시도
    Elements titles = doc.select("a.story-package-module__story__headline-link");

    for (var element : titles) {
      System.out.println("기사 제목: " + element.text());
      System.out.println("기사 링크: https://www.bloomberg.com" + element.attr("href"));
      System.out.println("------");
    }
  }
}
