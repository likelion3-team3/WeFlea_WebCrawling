package com.example.crawling.base.search.component;

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

    @Scheduled(cron = "0 50 8 * * ?", zone = "Asia/Seoul")
    public void crawlingKeywords(){
        WebDriver driver = setCrawling();

        searchService.crawlingDaangnKeywords(driver);

        driver.quit();
    }

    @Scheduled(cron = "0 0 9-21/1 * * ?", zone = "Asia/Seoul")
    public void crawlingJoongna() {
        List<String> keywords = getKeywords();
        WebDriver driver = setCrawling();
        searchService.searchJoongna(driver, keywords);
        driver.quit();
    }

    @Scheduled(cron = "0 0 9-21/1 * * ?", zone = "Asia/Seoul")
    public void crawlingBunjang() {
        List<String> keywords = getKeywords();
        WebDriver driver = setCrawling();
        searchService.searchBunjang(driver, keywords);
        driver.quit();
    }

    @Scheduled(cron = "0 0 9-21/1 * * ?", zone = "Asia/Seoul")
    public void crawlingHello() {
        List<String> keywords = getKeywords();
        WebDriver driver = setCrawling();
        searchService.searchHello(driver, keywords);
        driver.quit();
    }

    @Scheduled(cron = "0 0 9-21/1 * * ?", zone = "Asia/Seoul")
    public void crawlingDaangn() {
        List<String> keywords = getKeywords();
        WebDriver driver = setCrawling();
        searchService.searchDaangn(driver, keywords);
        driver.quit();
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

    public List<String> getKeywords(){
        List<SearchKeyword> keywordList = searchService.getKeywords();
        List<String> keywords = keywordList.stream().map(SearchKeyword::getName).collect(Collectors.toList());

        return keywords;
    }
}
