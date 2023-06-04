package com.example.crawling.base.search.controller;

import com.example.crawling.base.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchComponent {
    private final SearchService searchService;

    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Seoul")
    public void crawlingKeywords(){
        WebDriver driver = setCrawling();

        searchService.crawlingDaangnKeywords(driver);

        driver.quit();
    }

    // 매 1시간마다 실행한다.
    //@Scheduled(cron = "0 0 */1 * * *", zone = "Asia/Seoul")
    @Scheduled(cron = "0 36 20 * * *", zone = "Asia/Seoul")
    public void crawling() {
        WebDriver driver = setCrawling();
        List<String> keywords = List.of("자전거", "의자", "아이폰", "냉장고", "노트북", "아이패드", "모니터", "스타벅스", "책상", "가방", "에어팟", "신발");

        crawlingJoongna(driver, keywords);
        crawlingBunjang(driver, keywords);
        crawlingHello(driver, keywords);
        crawlingDaangn(driver, keywords);

        driver.quit();
    }

    public void crawlingJoongna(WebDriver driver, List<String> keywords) {
        searchService.searchJoongna(driver, keywords);
    }

    public void crawlingBunjang(WebDriver driver, List<String> keywords) {
        searchService.searchBunjang(driver, keywords);
    }

    public void crawlingHello(WebDriver driver, List<String> keywords) {
        searchService.searchHello(driver, keywords);
    }

    public void crawlingDaangn(WebDriver driver, List<String> keywords) {
        searchService.searchDaangn(driver, keywords);
    }

    public WebDriver setCrawling() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");        // 전체 화면으로 실행
        chromeOptions.addArguments("--disable-popup-blocking"); // 팝업 무시
        chromeOptions.addArguments("--headless");               // 브라우저 안띄움
        chromeOptions.addArguments("--disable-gpu");            // GPU를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요
        chromeOptions.addArguments("--no-sandbox");             // Sandbox 프로세스를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요
        chromeOptions.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(chromeOptions);

        return driver;
    }
}
