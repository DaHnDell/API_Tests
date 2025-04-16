package api.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class crawlerApi {

    public static void main(String[] args) {
        System.out.println("로또 전체 회차 크롤링 시작");
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();

        String url = "jdbc:mariadb://np.kcanmin.com:3306/APITEST?serverTimezone=Asia/Seoul";
        String user = "dahndell";
        String password = System.getProperty("DB_PASSWORD");
        if (password == null) throw new RuntimeException("환경변수 DB_PASSWORD가 설정되지 않았습니다.");

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            int latestRound = 100;

            for (int round = 1; round <= latestRound; round++) {
                String link = "https://dhlottery.co.kr/gameResult.do?method=byWin&drwNo=" + round;
                driver.get(link);
                Thread.sleep(500);

                // 당첨번호 6개
                List<WebElement> winBalls = driver.findElements(By.cssSelector(".win_result .nums span.ball_645"));

                // 보너스 번호
                String bonus = "0";
                try {
                    bonus = driver.findElement(By.cssSelector(".win_result .bonus span.ball_645")).getText();
                } catch (Exception ignored) {}

                // 추첨일자
                String drawDateStr = driver.findElement(By.cssSelector(".win_result .desc")).getText()
                        .replaceAll("[^0-9년월일 ]", "").trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
                LocalDate drawDate = LocalDate.parse(drawDateStr, formatter);

                // 1등 당첨자 수
                String winnersStr = driver.findElement(By.cssSelector(".tbl_data tr:nth-of-type(2) td:nth-of-type(2)")).getText().replaceAll("[^0-9]", "");
                long winners = Long.parseLong(winnersStr);

                // 1등 당첨금액
                String firstAmountStr = driver.findElement(By.cssSelector(".tbl_data tr:nth-of-type(2) td:nth-of-type(3)")).getText().replaceAll("[^0-9]", "");
                long firstAmount = Long.parseLong(firstAmountStr);

                // 총 판매금액
                long totalSales = 0L;
                try {
                    WebElement salesParent = driver.findElement(By.cssSelector(".win_result .desc"));
                    String totalSalesStr = salesParent.getText().replaceAll("[^0-9]", "");
                    totalSales = Long.parseLong(totalSalesStr);
                } catch (Exception e) {
                    System.err.printf("[%d회차] 판매금액 파싱 실패: %s\n", round, e.getMessage());
                }

                // 2등~5등 상세 정보 크롤링
                List<PrizeDetail> prizeDetails = new ArrayList<>();
                List<WebElement> prizeRows = driver.findElements(By.cssSelector(".tbl_data tbody tr"));

                for (int i = 1; i <= 5; i++) {
                    List<WebElement> cols = prizeRows.get(i).findElements(By.tagName("td"));

                    String rankStr = cols.get(0).getText().replaceAll("[^0-9]", "");
                    String winnerStr = cols.get(1).getText().replaceAll("[^0-9]", "");
                    String amountStr = cols.get(2).getText().replaceAll("[^0-9]", "");

                    int rank = Integer.parseInt(rankStr);
                    long winnerCount = Long.parseLong(winnerStr);
                    long prizeAmount = Long.parseLong(amountStr);

                    prizeDetails.add(new PrizeDetail(rank, winnerCount, prizeAmount));
                }

                // 저장
                insertToDatabase(conn, round, winBalls, bonus, drawDate, winners, firstAmount, totalSales);

                // 출력 (상세 정보)
                System.out.println("[" + round + "회차] 상세 등수별 정보:");
                for (PrizeDetail p : prizeDetails) {
                    System.out.println(p);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public static void insertToDatabase(Connection conn, int round, List<WebElement> winBalls, String bonus,
                                        LocalDate drawDate, long winners, long firstAmount, long totalSales) {
        String sql = """
            INSERT INTO lotto_history 
            (round, n1, n2, n3, n4, n5, n6, bonus, draw_date, first_prize_winners, first_prize_amount, total_sales_amount) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, round);
            for (int i = 0; i < 6; i++) {
                pstmt.setInt(i + 2, Integer.parseInt(winBalls.get(i).getText()));
            }
            pstmt.setInt(8, Integer.parseInt(bonus));
            pstmt.setDate(9, Date.valueOf(drawDate));
            pstmt.setLong(10, winners);
            pstmt.setLong(11, firstAmount);
            pstmt.setLong(12, totalSales);

            pstmt.executeUpdate();
            System.out.printf("%4d회차 저장 완료 ✅\n", round);

        } catch (Exception e) {
            System.err.printf("[%d회차] 저장 실패: %s\n", round, e.getMessage());
        }
    }

    // 2등~5등 정보를 담을 내부 DTO 클래스
    public static class PrizeDetail {
        int rank;
        long winnerCount;
        long prizeAmount;

        public PrizeDetail(int rank, long winnerCount, long prizeAmount) {
            this.rank = rank;
            this.winnerCount = winnerCount;
            this.prizeAmount = prizeAmount;
        }

        @Override
        public String toString() {
            return String.format("%d등 | 당첨자: %,d명 | 당첨금: %,d원", rank, winnerCount, prizeAmount);
        }
    }
}
