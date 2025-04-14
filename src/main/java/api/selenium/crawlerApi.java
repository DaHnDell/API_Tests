package api.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

public class crawlerApi {

    public static void main(String[] args) {
        System.out.println("로또 전체 회차 크롤링 시작");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        // DB 연결
        String url = "jdbc:mariadb://np.kcanmin.com:3306/APITEST?serverTimezone=Asia/Seoul";
        String user = "dahndell";
        String password = System.getProperty("DB_PASSWORD");
        if (password == null) {
            throw new RuntimeException("환경변수 DB_PASSWORD가 설정되지 않았습니다.");
        }

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // 크롤링 할 횟수 지정
            int latestRound = 5;

            for (int round = 1; round <= latestRound; round++) {
                String link = "https://dhlottery.co.kr/gameResult.do?method=byWin&drwNo=" + round;
                driver.get(link);
                Thread.sleep(500);

                List<WebElement> winBalls = driver.findElements(By.cssSelector(".win_result .nums span.ball_645"));
                String bonus = "";
                try {
                    WebElement bonusBall = driver.findElement(By.cssSelector(".win_result .bonus span.ball_645"));
                    bonus = bonusBall.getText();
                } catch (Exception e) {
                    bonus = "0";  // 보너스 번호 없는 회차 처리
                }

                // 콘솔 출력
                System.out.printf("%4d회차 → 당첨 번호: ", round);
                for (WebElement ball : winBalls) {
                    System.out.print(ball.getText() + " ");
                }
                System.out.println("+ 보너스: " + bonus);

                // DB 저장 호출
                insertToDatabase(conn, round, winBalls, bonus);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public static void insertToDatabase(Connection conn, int round, List<WebElement> winBalls, String bonus) {
        String sql = "INSERT INTO lotto_history (round, n1, n2, n3, n4, n5, n6, bonus) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, round);
            for (int i = 0; i < 6; i++) {
                pstmt.setInt(i + 2, Integer.parseInt(winBalls.get(i).getText()));
            }
            pstmt.setInt(8, Integer.parseInt(bonus));
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.printf("[%d회차] 저장 실패: %s\n", round, e.getMessage());
        }
    }
}
