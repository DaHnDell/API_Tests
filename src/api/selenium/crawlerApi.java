package api.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class crawlerApi {
    public static void main(String[] args) {
        System.out.println("Selenium 크롤러 테스트 시작!");

        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        try {
            driver.get("https://dhlottery.co.kr/gameResult.do?method=byWin");
            System.out.println("페이지 타이틀: " + driver.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}