package com.example.crawling.base.search.service;

import com.example.crawling.base.search.entity.Search;
import com.example.crawling.base.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final SearchRepository searchRepository;

    public void searchBunjang(WebDriver driver) {

    }

    public void searchJoongna(WebDriver driver) {
        List<WebElement> webElementList;

        // 4페이지까지 크롤링
        // 1페이지당 80개, 120페이지까지 해야 1시간씩 됨.
        for (int i = 1; i < 5; i++) {
            String url = "https://web.joongna.com/search?page=" + i;
            try {
                driver.get(url);

                ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return driver.findElement(By.cssSelector("[class=\"ant-col col css-t7ixlq e312bqk0\"] a")).isDisplayed();
                    }
                };

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

                wait.until(pageLoadCondition);

                // 게시글 사이트 링크 a 태그
                webElementList = driver.findElements(By.cssSelector("[class=\"ant-col col css-t7ixlq e312bqk0\"] a"));

                if (!webElementList.isEmpty()) {
                    for (WebElement webElement : webElementList) {
                        // 게시글 사이트 링크
                        String siteLink = webElement.getAttribute("href");

                        // 광고가 아닌 실제 게시글만 필터링
                        if (!siteLink.startsWith("https://web.joongna")) {
                            continue;
                        }

                        // 게시글이 올라온 시간과 상품 가격, 게시글 제목, 게시글 사진
                        WebElement webElementDetail = webElement.findElement(By.cssSelector("[class=\"css-1kiruf2\"]"));

                        // 중고나라는 registInfo가 2개인 경우도 있다. 1개인 경우는 시간만, 2개인 경우는 지역 + 시간
                        List<WebElement> registInfoList = webElementDetail.findElements(By.cssSelector("[class=\"registInfo\"] span"));
                        String date;
                        String area = "";

                        if (registInfoList.size() == 1) {
                            date = registInfoList.get(0).getText();
                        } else {
                            area = registInfoList.get(0).getText();
                            date = registInfoList.get(1).getText();
                        }

                        String price = webElementDetail.findElement(By.cssSelector("[class=\"priceTxt\"]")).getText();
                        String title = webElementDetail.findElement(By.cssSelector("[class=\"css-5uwdmz\"]")).getText();
                        String imgLink = webElementDetail.findElement(By.cssSelector("[class=\"css-jib2h7\"] img")).getAttribute("src");

                        Search search = Search
                                .builder()
                                .link(siteLink)
                                .sellDate(date)
                                .price(price)
                                .title(title)
                                .area(area)
                                .imageLink(imgLink)
                                .provider("중고나라")
                                .build();

                        searchRepository.save(search);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
