package api.bloomberg;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

public class BloombergSeleniumCrawler {
  public static void main(String[] args) throws Exception {  // ← 여기 중요!
    WebDriver driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

    try {
      driver.get("https://www.bloomberg.com/");
      Thread.sleep(3000);

      // GDPR 쿠키 동의 (존재하면 클릭)
      try {
        try {
          WebElement saveBtn = driver.findElement(By.cssSelector("button[title='Save My Choices']"));
          saveBtn.click();
          Thread.sleep(1000);
          System.out.println("쿠키 동의 처리 완료");
        } catch (Exception ignored) {
          System.out.println("쿠키 버튼을 찾지 못했거나 이미 동의됨");
        }
      } catch (Exception ignored) {}

      List<WebElement> articles = driver.findElements(By.cssSelector("a[href*='/news/articles/']"));

      for (WebElement article : articles) {
        String title = article.getText();
        String link = article.getAttribute("href");

        if (title == null || title.isBlank()) continue;

        if (!link.startsWith("http")) {
          link = "https://www.bloomberg.com" + link;
        }

        System.out.println("기사 제목: " + title);
        System.out.println("링크: " + link);
        System.out.println("------");
      }

    } finally {
      driver.quit();
    }
  }
}
