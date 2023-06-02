package com.example.crawling.base.search.service;

import com.example.crawling.base.search.entity.Search;
import com.example.crawling.base.search.repository.SearchRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {
    private final SearchRepository searchRepository;

    public void searchDaangn(WebDriver driver, List<String> keywords) {
        List<WebElement> webElementList;

        // 1키워드당 40개씩 keywords.get(j)
        for (int j = 0; j < keywords.size(); j++) {
            String url = "https://www.daangn.com/search/" + keywords.get(j);
            try {
                driver.get(url);

                String elementListCssSelector = "[class=\"flea-market-article-link\"]";
                String moreBtn = "[onclick=\"moreResult(this, 'flea_market', 'flea-market-wrap');\"]";

                WebElement moreBtnElement = driver.findElement(By.cssSelector(moreBtn));

                while (true) {
                    moreBtnElement.click();
                    waitPageLoading(driver, moreBtn);
                    if (moreBtnElement.getAttribute("data-page").equals("3")) {
                        break;
                    }
                }
                waitPageLoading(driver, elementListCssSelector);
                webElementList = driver.findElements(By.cssSelector(elementListCssSelector));

                if (!webElementList.isEmpty()) {
                    for (WebElement webElement : webElementList) {
                        String siteLink = webElement.getAttribute("href");
                        String imgLink = webElement.findElement(By.cssSelector("[class=\"card-photo\"] img")).getAttribute("src");

                        String title = webElement.findElement(By.cssSelector("[class=\"article-title\"]")).getText();

                        String price;
                        try {
                            price = webElement.findElement(By.cssSelector("[class=\"article-price \"]")).getText();
                        } catch (NoSuchElementException e) {
                            price = webElement.findElement(By.cssSelector("[class=\"article-price blank-price\"]")).getText();
                        }

                        String area = webElement.findElement(By.cssSelector("[class=\"article-region-name\"]")).getText();
                        // 게시글 내용
                        // String content = webElement.findElement(By.cssSelector("[class=\"article-content\"]")).getText();

                        Search search = Search
                                .builder()
                                .link(siteLink)
                                .sellDate("")
                                .price(price)
                                .title(title)
                                .area(area)
                                .imageLink(imgLink)
                                .provider("당근마켓")
                                .build();

                        searchRepository.save(search);
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public void searchHello(WebDriver driver, List<String> keywords) {
        List<WebElement> webElementList;

        // 1 키워드당 30개씩
        for (int j = 0; j < keywords.size(); j++) {
            String url = "https://www.hellomarket.com/search?q=" + keywords.get(j) + "&sort=current";
            try {
                driver.get(url);

                String elementListCssSelector = "[class=\"Item__Wrapper-sc-17ycp52-0 hScXrO\"]";

                waitPageLoading(driver, elementListCssSelector);

                webElementList = driver.findElements(By.cssSelector(elementListCssSelector));

                if (!webElementList.isEmpty()) {
                    for (WebElement webElement : webElementList) {
                        String siteLink = webElement.findElement(By.cssSelector("[class=\"Item__ThumbnailBox-sc-17ycp52-1 liZtWH\"] a")).getAttribute("href");
                        String imgLink = webElement.findElement(By.cssSelector("[class=\"Item__ThumbnailBox-sc-17ycp52-1 liZtWH\"] a img")).getAttribute("src");

                        WebElement webElementDetail = webElement.findElement(By.cssSelector("[class=\"Item__TextBox-sc-17ycp52-5 ivArQS\"]"));

                        String price = webElementDetail.findElement(By.cssSelector("[class=\"Item__Text-sc-17ycp52-4 fUCHku\"]")).getText();
                        String title = webElementDetail.findElement(By.cssSelector("[class=\"Item__Text-sc-17ycp52-4 cuyRaw\"]")).getText();
                        String date = webElementDetail.findElement(By.cssSelector("[class=\"Item__TimeTag-sc-17ycp52-9 fxCGUZ\"]")).getText();

                        Search search = Search
                                .builder()
                                .link(siteLink)
                                .sellDate(date)
                                .price(price)
                                .title(title)
                                .area("")
                                .imageLink(imgLink)
                                .provider("헬로마켓")
                                .build();

                        searchRepository.save(search);
                    }
                }

            } catch (Exception e) {
            }
        }
    }

    public void searchBunjang(WebDriver driver, List<String> keywords) {
        List<WebElement> webElementList;

        // 키워드 1개 1페이지 100개
        for (int j = 0; j < keywords.size(); j++) {
            for (int i = 1; i < 3; i++) {
                String url = "https://m.bunjang.co.kr/search/products?order=date&page=" + i + "&q=" + keywords.get(j);
                try {
                    driver.get(url);

                    // 게시글 요소 css Selector
                    String elementListCssSelector = "[class=\"sc-kcDeIU WTgwo\"] a";

                    waitPageLoading(driver, elementListCssSelector);

                    webElementList = driver.findElements(By.cssSelector(elementListCssSelector));

                    if (!webElementList.isEmpty()) {
                        for (WebElement webElement : webElementList) {
                            String siteLink = webElement.getAttribute("href");

                            String imgLink = webElement.findElement(By.cssSelector("[class=\"sc-hgHYgh ieNgVs\"] img")).getAttribute("src");

                            String title = webElement.findElement(By.cssSelector("[class=\"sc-gtfDJT brQSgh\"]")).getText();

                            String area = webElement.findElement(By.cssSelector("[class=\"sc-hzDEsm eaWqIm\"]")).getText();

                            String price;

                            // 가격이 적혀있는 글은 가격을 가져오고, 가격에 연락요망 이라고 해놓은 글은 연락요망을 가져온다.
                            try {
                                price = webElement.findElement(By.cssSelector("div [class=\"sc-fOICqy ikGLLE\"]")).getText() + "원";
                            } catch (NoSuchElementException e) {
                                price = webElement.findElement(By.cssSelector("[class=\"sc-fOICqy gwMnKn\"]")).getText();
                            }

                            String date = webElement.findElement(By.cssSelector("div [class=\"sc-jtRlXQ kjBXGS\"] span")).getText();

                            Search search = Search
                                    .builder()
                                    .link(siteLink)
                                    .sellDate(date)
                                    .price(price)
                                    .title(title)
                                    .area(area)
                                    .imageLink(imgLink)
                                    .provider("번개장터")
                                    .build();

                            searchRepository.save(search);
                        }
                    }

                } catch (Exception e) {
                }
            }
        }
    }

    public void searchJoongna(WebDriver driver, List<String> keywords) {
        List<WebElement> webElementList;

        // 키워드 1개 1페이지 40개
        for (int j = 0; j < keywords.size(); j++) {
            for (int i = 1; i < 3; i++) {
                String url = "https://web.joongna.com/search/" + keywords.get(j) + "?page=" + i;

                try {
                    driver.get(url);

                    String ElementListCssSelector = "[class=\"ant-col col css-t7ixlq e312bqk0\"] a";

                    waitPageLoading(driver, ElementListCssSelector);

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
                }
            }
        }
    }

    // css selector가 나타날 때까지 페이지 로딩 기다리기(3초)
    private void waitPageLoading(WebDriver driver, String selector) {
        ExpectedCondition<Boolean> pageLoadCondition = driver1 -> driver1.findElement(By.cssSelector(selector)).isDisplayed();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));

        wait.until(pageLoadCondition);
    }
}
