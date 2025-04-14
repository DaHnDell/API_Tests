package api.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class crawlerApi {
    public static void main(String[] args) {
        System.out.println("로또 전체 회차 크롤링 시작");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            int latestRound = 1115; // 최신 회차 수 (매주 토요일 기준 업데이트 가능)
            for (int round = 1; round <= latestRound; round++) {
                String url = "https://dhlottery.co.kr/gameResult.do?method=byWin&drwNo=" + round;
                driver.get(url);
                Thread.sleep(500); // 로딩 대기
                List<WebElement> winBalls = driver.findElements(By.cssSelector(".win_result .nums span.ball_645"));
                String bonus = "";
                try {
                    WebElement bonusBall = driver.findElement(By.cssSelector(".win_result .bonus + span.ball_645"));
                    bonus = bonusBall.getText();
                } catch (Exception e) {
                    bonus = "(보너스 없음)";
                }
                System.out.printf("%4d회차 → 당첨 번호: ", round);
                for (WebElement ball : winBalls) {
                    System.out.print(ball.getText() + " ");
                }
                System.out.println("+ 보너스: " + bonus);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
