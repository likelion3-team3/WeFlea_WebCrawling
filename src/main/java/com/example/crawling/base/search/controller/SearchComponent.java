package com.example.crawling.base.search.controller;

import com.example.crawling.base.search.entity.SearchKeyword;
import com.example.crawling.base.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SearchComponent {
    private final SearchService searchService;

    //@Scheduled(cron = "0 50 8 * * ?", zone = "Asia/Seoul")
    @Scheduled(cron = "0 50 8 * * ?", zone = "Asia/Seoul")
    public void crawlingKeywords(){
        WebDriver driver = setCrawling();

        searchService.crawlingDaangnKeywords(driver);

        driver.quit();
    }

    // 오전 9시부터 오후 9시까지 매 1시간마다 실행한다.
    //@Scheduled(cron = "0 0 */1 * * *", zone = "Asia/Seoul")
    //@Scheduled(cron = "0 0 9-21/1 * * ?", zone = "Asia/Seoul")
    @Scheduled(cron = "0 0 9-21/1 * * ?", zone = "Asia/Seoul")
    public void crawling() {
        WebDriver driver = setCrawling();

        List<SearchKeyword> keywordList = searchService.getKeywords();
        List<String> keywords = keywordList.stream().map(SearchKeyword::getName).collect(Collectors.toList());

        crawlingJoongna(driver, keywords);
        crawlingHello(driver, keywords);
        crawlingDaangn(driver, keywords);
        crawlingBunjang(driver, keywords);

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
        chromeOptions.addArguments("--disable-popup-blocking"); // 팝업 무시
        chromeOptions.addArguments("--headless");               // 브라우저 안띄움
        chromeOptions.addArguments("--disable-gpu");            // GPU를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요
        chromeOptions.addArguments("--no-sandbox");             // Sandbox 프로세스를 사용하지 않음, Linux에서 headless를 사용하는 경우 필요
        chromeOptions.addArguments("--disable-dev-shm-usage");

        WebDriver driver = new ChromeDriver(chromeOptions);

        return driver;
    }
}
